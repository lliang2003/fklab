/**
 * @file TimeSpanMapper.cpp
 *
 * */
#include "TimeSpanMapper.hpp"
#include "Collector.hpp"
#include <stdio.h>
#include <cstdlib>
#include <iostream>

using namespace std;
using namespace mapreduce;

TimeSpanMapper::TimeSpanMapper()
{
}

TimeSpanMapper::~TimeSpanMapper()
{
}

bool TimeSpanMapper::map(const void* key, const void* value, Collector& collector) const
{
    int *pnum = NULL;
    int *pcount = new int;
    *pcount = 1;
    if(key){
        pnum = (int*)key;
        int size = *pnum;
        //cout << "mapper: ";
        for(int i = 0; i < size; i ++){
            //cout << pnum[i+1] << " ";
            collector.collect(&pnum[i+1], pcount);
        }
        //cout << endl;
    }
    
    return true;
}

