/**
 * @file    test_io.h
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年07月06日 12时05分04秒 CST
 *
 */

#ifndef  TEST_IO_FILE_HEADER_INC
#define  TEST_IO_FILE_HEADER_INC

#include <fstream>
#include <iostream>
#include <ctime>
#include <algorithm>
#include <vector>
#include <boost/timer.hpp>
using namespace std;


void test_fio() {
    const int SIZE = 1024 * 1024 * 1024;
    const char *file = "test_io.dat";
    char *s = new char[SIZE];
    boost::timer t;
    ifstream fin;
    cout <<"Test size: 1G" <<endl;

    ofstream fout(file, ios::binary|ios::trunc);
    t.restart();
    fout.write(s, SIZE);
    fout.close();
    cout <<"Write data in " <<t.elapsed() <<" seconds." <<endl;

    fin.open(file, ios::binary);
    t.restart();
    fin.read(s, SIZE);
    fin.close();
    cout <<"Read data in " <<t.elapsed() <<" seconds." <<endl;

    /*
    t.restart();
    random_shuffle(s, s + SIZE);
    cout <<"Shuffle data in " <<t.elapsed() <<" seconds." <<endl;
    */
    
    vector<int> vs;
    vs.push_back(1024);
    vs.push_back(2*1024);
    vs.push_back(4*1024);
    vs.push_back(16*1024);
    vs.push_back(128*1024);
    vs.push_back(1024*1024);
    reverse(vs.begin(), vs.end());

    for (int i = 0; i < vs.size(); ++i) {
        t.restart();
        fin.open(file, ios::binary);
        for (int j = 0; j < SIZE; j += 1024) {
            if (j % vs[i] == 0)
                fin.read(s, vs[i]);
        }
        fin.close();
        cout <<"Read data with " <<vs[i] <<"-byte blocks cost " 
            <<t.elapsed() <<" seconds."<<endl;
    }

    t.restart();
    fin.open(file, ios::binary);
    while (fin.get() != EOF);
    fin.close();
    cout <<"Read data in each byte cost " <<t.elapsed() <<" seconds." <<endl;
}

void test_printf() {
    cout <<__func__ <<" ..." <<endl;
    cout <<__func__ <<" done." <<endl;
}

void test_io() {
    cout <<__func__ <<" ..." <<endl;
    test_printf();
    cout <<__func__ <<" done." <<endl;

    cout <<"Test IO ..." <<endl;
    cout <<"Test IO done." <<endl;
}

#endif   /* ----- #ifndef TEST_IO_FILE_HEADER_INC  ----- */

