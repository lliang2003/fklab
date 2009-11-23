/**
* @file InfomallStatFeeder.cpp
* @description 
*  RecordReader for Infomall statistics file
**/

#include "InfomallStatFeeder.hpp"
#include "FileStream.hpp"
#include "MapReduceException.hpp"
#include "InfomallStatValue.hpp"
#include <iostream>
#include <fstream>

using namespace std;
using namespace mapreduce;



//=================================================
//
// 1.url feeder <url, count>
InfomallStatUrlFeeder::InfomallStatUrlFeeder()
{
}

InfomallStatUrlFeeder::~InfomallStatUrlFeeder()
{
}

void InfomallStatUrlFeeder::open(FileStream* stream)
{
    m_fin.open(stream->getFilename().c_str(), ios::in);
}

void InfomallStatUrlFeeder::close()
{
    m_fin.close();
}

void InfomallStatUrlFeeder::initialize(int bufferSize , bool autoClearBuffer)
{
}

void InfomallStatUrlFeeder::flush()
{
}

bool InfomallStatUrlFeeder::next(void*& key, void*& value)
{
    string line;
    InfomallStatValue *pvalue = new InfomallStatValue();
   
    if(!m_fin){
        //return a nul object
        key = value = NULL;
        return false;
    }

    m_fin >> *pvalue;

    *(char**)key = (char*)pvalue->uri.c_str();
    *(int*)value = pvalue->crawl_count;
        
    if(*(int*)value == 0)
        return false;

    return true;
}

bool InfomallStatUrlFeeder::releaseKeys(void*& keys)
{
    if(keys != NULL){
        char** pkeys = (char**)keys;
        delete[] pkeys;
    }
    return true;
}

bool InfomallStatUrlFeeder::releaseValues(void*& values)
{
    if(values != NULL){
        int* pvalues = (int*)values;
        delete[] pvalues;
    }
    return true;
}


//=================================================
//
// 2.host feeder <host, count>
InfomallStatHostFeeder::InfomallStatHostFeeder()
{
}

InfomallStatHostFeeder::~InfomallStatHostFeeder()
{
}

void InfomallStatHostFeeder::open(FileStream* stream)
{
    m_fin.open(stream->getFilename().c_str(), ios::in);
}

void InfomallStatHostFeeder::close()
{
    m_fin.close();
}

void InfomallStatHostFeeder::initialize(int bufferSize , bool autoClearBuffer)
{
}

void InfomallStatHostFeeder::flush()
{
}

bool InfomallStatHostFeeder::next(void*& key, void*& value)
{
    string line;
    InfomallStatValue *pvalue = new InfomallStatValue();
   
    if(!m_fin){
        //return a nul object
        key = value = NULL;
        return false;
    }

    m_fin >> *pvalue;
    
    /**
     * get host address
     * */
    string host;
    int index = pvalue->uri.find("//");
    if(index < string::npos){
        int start = pvalue->uri.find('/', index + 2);
        if(start < string::npos){
            host = pvalue->uri.substr(0, start);
        }
        else{
            host = pvalue->uri;
        }
    }
    else{
        host = pvalue->uri;
    }

    *(char**)key = (char*)host.c_str();
    *(int*)value = pvalue->crawl_count;
        
    if(*(int*)value == 0)
        return false;

    return true;
}

bool InfomallStatHostFeeder::releaseKeys(void*& keys)
{
    if(keys != NULL){
        char** pkeys = (char**)keys;
        delete[] pkeys;
    }
    return true;
}

bool InfomallStatHostFeeder::releaseValues(void*& values)
{
    if(values != NULL){
        int* pvalues = (int*)values;
        delete[] pvalues;
    }
    return true;
}


//=================================================
//
// 3.timespan feeder <list<timespan>, null>
InfomallStatTimeSpanFeeder::InfomallStatTimeSpanFeeder()
{
}

InfomallStatTimeSpanFeeder::~InfomallStatTimeSpanFeeder()
{
}

void InfomallStatTimeSpanFeeder::open(FileStream* stream)
{
    m_fin.open(stream->getFilename().c_str(), ios::in);
}

void InfomallStatTimeSpanFeeder::close()
{
    m_fin.close();
}

void InfomallStatTimeSpanFeeder::initialize(int bufferSize , bool autoClearBuffer)
{
}

void InfomallStatTimeSpanFeeder::flush()
{
}

bool InfomallStatTimeSpanFeeder::next(void*& key, void*& value)
{
    string line;
   
    if(!m_fin){
        //return a nul object
        key = value = NULL;
        return false;
    }

    InfomallStatValue *pvalue = new InfomallStatValue();
    m_fin >> *pvalue;
    
    int *pSpan = NULL;
    if(pvalue->time_list.size() == 1){
        pSpan = new int[2];
        pSpan[0] = 1;
        pSpan[1] = -1;
    }
    else if(pvalue->time_list.size() > 1){
        pSpan = new int[pvalue->time_list.size()];
        pSpan[0] = pvalue->time_list.size() - 1;
        time_t oldtime = pvalue->time_list[0];
        for(int i = 1; i < pvalue->time_list.size(); i ++){
            time_t newtime = pvalue->time_list[i];
            double timespan = difftime(newtime, oldtime);
            // timespan count in weeks
            pSpan[i] = (int)timespan/(3600*24*7);
            oldtime = newtime;
        }
    }

    key = (void*)pSpan;
     
    delete pvalue;

    if(pvalue->crawl_count == 0)
        return false;

    return true;
}

bool InfomallStatTimeSpanFeeder::releaseKeys(void*& keys)
{
    if(keys != NULL){
        int* pkeys = (int*)keys;
        delete[] pkeys;
    }
    return true;
}

bool InfomallStatTimeSpanFeeder::releaseValues(void*& values)
{
    return true;
}


