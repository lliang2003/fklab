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
#include <cassert>
using namespace std;

const int LR = 100;
const int LK = 10;
char *buf;
int ngroups;

struct sr {
    char *str;
    char *buf;
    int num;
    int index;
};

struct record {
    char s[100];
};

bool operator<(const struct sr &a, const struct sr &b) {
    return memcmp(a.str, b.str, LK) > 0;
}

bool operator<(const struct record &a, const struct record &b) {
    return memcmp(&a, &b, LK) < 0;
}

int creates(ifstream &fin, ofstream fouts[], int n) {
    boost::timer t;
    fin.read(buf, n * LR);
    n = fin.gcount() / LR;
    if (n == 0) return n;
    assert(fin.good() || fin.eof());
    for (int i = 0; i < n; ++i) {
        fouts[((buf[LR*i]-' ')*95 + buf[LR*i + 1]-' ') * ngroups / 95 / 95].write(buf + LR*i, LR);
    }
    return n;
}


int main(int argc, char **argv) {
    if (argc != 4) {
        cout <<"Usage: " <<argv[0] <<" <buffer_size> <input_file> <output_dir>" <<endl;
        return 0;
    }
    boost::timer t;
    vector<string> ss;
    ifstream fin;
    ofstream fout;
    system((string("rm -rf ") + argv[3]).c_str());
    system((string("mkdir ") + argv[3]).c_str());

    // create tmp sorted files;
    fin.open(argv[2], ios::binary);
    if (!fin.is_open()) {
        cout <<"Can not open " <<argv[2] <<endl;
        return 1;
    }
    int bufsize = boost::lexical_cast<int>(argv[1])*1000000;
    buf = new char[bufsize];
    fin.seekg(0, ios::end);
    int nr = fin.tellg() / LR;
    fin.seekg(0, ios::beg);
    ngroups = nr * LR * 3/ bufsize / 2 ;
    int maxn = bufsize / LR;
    struct record *bufr = (struct record *)buf;
    string prefix;
    prefix += argv[3];
    prefix += "/part";
    cout <<"Creating tmp files " <<flush;
    int n = 0, m;
    ofstream *fouts = new ofstream[ngroups];
    for (int i = 0; i < ngroups; ++i) {
        string name = prefix + (i < 10 ? "0" : "") + boost::lexical_cast<string>(i);
        fouts[i].open(name.c_str(), ios::binary|ios::trunc);
        ss.push_back(name);
    }
    for (int i = 0; ; ++i) {
        if ((m = creates(fin, fouts, maxn)) == 0) break;
        n += m;
        cout <<"." <<flush;
    }
    fin.close();
    delete []fouts;
    cout <<" cost " <<t.elapsed() <<" seconds." <<endl;
    cout <<n <<" records in total." <<endl;

    cout <<"Sorting " <<ss.size() <<" parts " <<flush;
    for (int i = 0; i < ss.size(); ++i) {
        fin.open(ss[i].c_str(), ios::binary);
        fin.read(buf, bufsize);
        fin.close();
        int n = fin.gcount() / LR;
        sort((struct record *)buf, (struct record *)buf + n);
        fout.open(ss[i].c_str(), ios::binary|ios::trunc);
        fout.write(buf, n * LR);
        fout.close();
        cout <<"." <<flush;
    }
   cout <<" cost " <<t.elapsed() <<" seconds." <<endl;

    return 0;
}
