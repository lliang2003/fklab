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
using namespace std;

const int LR = 100;
const int LK = 10;

bool cmp(const char * const a, const char * const b) {
    return memcmp(a, b, LK) < 0;
}

int main(int argc, char **argv) {
    if (argc != 3) {
        cout <<"Usage: " <<argv[0] <<" <input_file> <output_file>" <<endl;
        return 0;
    }
    char *ifile = argv[1];
    char *ofile = argv[2];
    boost::timer t;

    ifstream fin(ifile, ios::binary);
    fin.seekg(0, ios::end);
    int NR = fin.tellg() / LR;
    fin.seekg(0, ios::beg);

    cout <<"Sort " <<NR <<" records" <<endl;
    cout <<"Begin read data at " <<t.elapsed() <<endl;
    char *s = new char[NR * LR];
    fin.read(s, NR * LR);
    fin.close();

    cout <<"Begin push data at " <<t.elapsed() <<endl;
    vector<char *> vcp(NR);
    for (int i = 0; i < NR; ++i)
        vcp[i] = s + i * LR;

    cout <<"Begin sort data at " <<t.elapsed() <<endl;
    sort(vcp.begin(), vcp.end(), cmp);

    cout <<"Begin output data at " <<t.elapsed() <<endl;
    ofstream fout(ofile, ios::binary|ios::trunc);
    for (int i = 0; i < NR; ++i) {
        fout.write(vcp[i], LR);
    }
    fout.close();
    printf("Exit at %.2f\n", t.elapsed());
    return 0;
}
