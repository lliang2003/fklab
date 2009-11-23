/** 
 * @file TestInfomallStat.hpp
 * .
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 */
#ifndef _TESTINFOMALLSTAT_HPP
#define _TESTINFOMALLSTAT_HPP 1

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

class TestInfomallStat: public CxxTest::TestSuite 
{
public:
    
    void testFeeder(){
    
        
        InfomallStatUrlFeeder feeder;
        feeder.initialize(0, true);
        feeder.open(new FileStreamLocal("url_time.dat", FileStream::ReadOnly, false));
        //feeder.open(new FileStreamLocal("test.par", FileStream::ReadOnly, false));
        
        void* key = feeder.createKeys();
        void* value = feeder.createValues();
        void* doKey = key;
        void* doValue = value;
        int count = 0;
        vector<string> test;
     
        while(feeder.next(doKey, doValue) ){
            cout << "url " << *(char**)doKey << " " << *(int*)doValue << endl;
            feeder.flush();
            count ++;
        }
        
        cout << count << endl;
        
    }
    
    void testArchive(){
        
        InfomallStatValue value1;
        value1.uri = "http://www.pku.edu.cn";
        value1.crawl_count = 3;
        value1.time_list.push_back(1139343842);
        value1.time_list.push_back(1138816706);
        value1.time_list.push_back(1118777963);
        
        char* pkey = "werwesdfsdfr";

        TKeyValueWriter<char*, InfomallStatValue> writer;
        writer.initialize(8*1024, true);
        writer.open(new FileStreamLocal("test.par", FileStream::Append, false));
        writer.append(&pkey, &value1);
        writer.close();
        
        TKeyValueFeeder<char*, InfomallStatValue> feeder;
        feeder.initialize(0, true);
        feeder.open(new FileStreamLocal("test.par", FileStream::ReadOnly, false));
        
        void* key = feeder.createKeys();
        void* value = feeder.createValues();
        void* doKey = key;
        void* doValue = value;
        feeder.next(doKey, doValue);
        TS_ASSERT_SAME_DATA(*(char**)doKey, pkey, strlen(pkey) );
        TS_ASSERT_EQUALS(((InfomallStatValue*)doValue)->crawl_count, value1.crawl_count);
        for(int i = 0; i < value1.crawl_count; i ++){
            TS_ASSERT_SAME_DATA( &((InfomallStatValue*)doValue)->time_list[i], &value1.time_list[i], sizeof(time_t));
        }
        feeder.releaseKeys(key);
        feeder.releaseValues(value);
        feeder.close();
    }
};


#endif /* _TESTPAGERANKMAPPER_HPP */
