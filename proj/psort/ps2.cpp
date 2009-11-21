/**
 * @file	penny_sort.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年06月01日 10时28分51秒 CST
 *
 */

#include <iostream>
#include <fstream>
#include <vector>
#include <functional>
#include <ctime>
#include <boost/timer.hpp>
#include <boost/lexical_cast.hpp>
#include <queue>
using namespace std;

const int LR = 100;
const int LK = 10;
char *buf;
vector<const char *> vcp;

struct sr {
    char *str;
    int index;
};

bool operator<(const struct sr &a, const struct sr &b) {
    return memcmp(a.str, b.str, LK) > 0;
}

bool cmp(const char * const a, const char * const b) {
    return memcmp(a, b, LK) < 0;
}

int creates(ifstream &fin, ofstream &fout, int n) {
    //cout <<"Sort " << n <<" records " <<endl;
    fin.read(buf, n * LR);
    n = fin.gcount() / LR;
    if (n == 0) return n;
    vcp.resize(n);
    for (int i = 0; i < n; ++i)
        vcp[i] = buf + i * LR;
    sort(vcp.begin(), vcp.end(), cmp);
    for (int i = 0; i < n; ++i)
        fout.write(vcp[i], LR);
    return n;
}

void merge(ifstream fins[], ofstream &fout, int n) {
    priority_queue<struct sr> sq;
    struct sr r;
    for (int i = 0; i < n; ++i) {
        r.str = new char[LR];
        fins[i].read(r.str, LR);
        if (!fins[i].good()) {
            delete r.str;
            continue;
        }
        r.index = i;
        sq.push(r);
    }

    while (!sq.empty()) {
        r = sq.top();
        sq.pop();
        fout.write(r.str, LR);
        fins[r.index].read(r.str, LR);
        if (!fins[r.index].good()) {
            delete r.str;
            continue;
        }
        sq.push(r);
    }
}

int main(int argc, char **argv) {
    if (argc != 4) {
        cout <<"Usage: " <<argv[0] <<" <buffer_size> <input_file> <output_file>" <<endl;
        return 0;
    }
    boost::timer t;
    vector<string> ss;

    // create tmp sorted files;
    ifstream fin(argv[2], ios::binary);
    ofstream fout;
    int bufsize = boost::lexical_cast<int>(argv[1])*1000000;
    buf = new char[bufsize];
    int maxn = bufsize / LR;
    string prefix = "data/.sorted";
    cout <<"Creating tmp files " <<flush;
    int n = 0, m;
    for (int i = 0; ; ++i) {
        string name = prefix + boost::lexical_cast<string>(i);
        fout.open(name.c_str(), ios::binary|ios::trunc);
        if ((m = creates(fin, fout, maxn)) == 0) break;
        n += m;
        fout.close();
        ss.push_back(name);
        //cout <<"Create " <<name <<endl;
        cout <<"." <<flush;
    }
    fout.close();
    fin.close();
    cout <<" cost " <<t.elapsed() <<" seconds." <<endl;
    cout <<n <<" records in total." <<endl;

    cout <<"Merging " <<ss.size() <<" tmp files ... " <<flush;
    if (ss.size() > 1) {
        // merge;
        ifstream *fins = new ifstream[ss.size()];
        for (int i = 0; i < ss.size(); ++i)
            fins[i].open(ss[i].c_str(), ios::binary);
        fout.open(argv[3], ios::binary|ios::trunc);
        merge(fins, fout, ss.size());
        delete []fins;
        fout.close();
    } else {
        string cmd = "mv .sorted0 ";
        cmd += argv[3];
        system(cmd.c_str());
    }
    cout <<"cost " <<t.elapsed() <<" seconds." <<endl;

    return 0;
}
