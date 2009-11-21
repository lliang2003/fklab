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
vector<char *> vcp;

struct sr {
    char *str;
    char *buf;
    int num;
    int index;
};

bool operator<(const struct sr &a, const struct sr &b) {
    return memcmp(a.str, b.str, LK) > 0;
}

bool cmp(char *a, char *b) {
    return memcmp(a, b, LK) < 0;
}

int creates(ifstream &fin, ofstream &fout, int n) {
    boost::timer t;
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
    //cout <<"Sort " << n <<" records in " <<t.elapsed() <<" seconds. " <<endl;
    return n;
}

void merge(ifstream fins[], ofstream &fout, int n, int bufsize) {
    int maxn = bufsize / n / LR;
    priority_queue<struct sr> sq;
    struct sr r;
    for (int i = 0; i < n; ++i) {
        r.buf = buf + maxn * LR * i;
        fins[i].read(r.buf, LR * maxn);
        if (fins[i].gcount() == 0) {
            continue;
        }
        r.str = r.buf;
        r.num = fins[i].gcount() / LR;
        r.index = i;
        //cout <<"fill " <<r.num <<" records to fin " <<r.index <<endl;
        sq.push(r);
    }

    while (!sq.empty()) {
        r = sq.top();
        sq.pop();
        fout.write(r.str, LR);
        r.num --;
        r.str += LR;
        if (r.num == 0) {
            fins[r.index].read(r.buf, LR * maxn);
            if (fins[r.index].gcount() == 0) {
                continue;
            }
            r.str = r.buf;
            r.num = fins[r.index].gcount() / LR;
            //cout <<"fill " <<r.num <<" records to fin " <<r.index <<endl;
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
    if (!fin.is_open()) {
        cout <<"Can not open " <<argv[2] <<endl;
        return 1;
    }
    ofstream fout;
    int bufsize = boost::lexical_cast<int>(argv[1])*1000000;
    buf = new char[bufsize];
    int maxn = bufsize / LR;
    string prefix = ".sorted";
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
        for (int i = 0; i < ss.size(); ++i) {
            fins[i].open(ss[i].c_str(), ios::binary);
            if (!fins[i].is_open()) cout <<"Can not open " <<ss[i] <<endl;
        }
        fout.open(argv[3], ios::binary|ios::trunc);
        merge(fins, fout, ss.size(), bufsize);
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
