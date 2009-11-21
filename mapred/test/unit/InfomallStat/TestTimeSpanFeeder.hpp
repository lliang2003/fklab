/** 
 * @file TestTimeSpanFeeder.hpp
 * .
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 */
#ifndef _TESTTIMESPAN_HPP
#define _TESTTIMESPAN_HPP 1

#include <cxxtest/TestSuite.h>
#include "FileStream.hpp"
#include "FileStreamLocal.hpp"
#include "InfomallStat/InfomallStatValue.hpp"
#include "InfomallStat/InfomallStatFeeder.hpp"
#include "TKeyValueFeeder.hpp"
#include "TKeyValueWriter.hpp"
#include <string>
#include <vector>
#include <iostream>
#include <ctime>

using namespace std;
using namespace mapreduce;

class TestTimeSpanFeeder: public CxxTest::TestSuite 
{
public:
    
    void testFeeder(){
        InfomallStatTimeSpanFeeder feeder;
        feeder.initialize(0, true);
        feeder.open(new FileStreamLocal("url_time.dat", FileStream::ReadOnly, false));
        
        void* key = feeder.createKeys();
        void* value = feeder.createValues();
        void* doKey = key;
        void* doValue = value;
        int count = 0;
        vector<string> test;
     
        while(feeder.next(doKey, doValue) ){
            int num = *(int*)doKey;
            int* pnum = (int*)doKey;
            for(int i = 0; i < num ; i ++){
                cout << pnum[i+1] << " ";
            }
            cout << endl;
            feeder.flush();
            count ++;
        }
        
        cout << count << endl;
    }
   
};


#endif /* _TESTPAGERANKMAPPER_HPP */
