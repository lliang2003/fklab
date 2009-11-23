/**
 * @file TimeSpanReducer.cpp
 *
 * */
#include "TimeSpanReducer.hpp"
#include "KeyValueIterator.hpp"
#include "Collector.hpp"
#include <string> 
#include <iostream>

using namespace std;
using namespace mapreduce;


bool TimeSpanReducer::reduce(const void* key, KeyValueIterator& values, Collector& collector) const
{
    int totalCount = 0;
    while(values.hasMore()){
        int* pCount = (int*)(values.next() );
        totalCount += *pCount;
    }
    
    //cout << "reducer: " << *(int*)key << " " << totalCount << endl;
    int week = *(int*)key + 1;
    collector.collect(&week, &totalCount);
    
    return true;
}
