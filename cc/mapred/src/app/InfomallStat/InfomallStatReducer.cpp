/**
 * @file InfomallStatReducer.cpp
 *
 * */
#include "InfomallStatReducer.hpp"
#include "KeyValueIterator.hpp"
#include "Collector.hpp"
#include <string> 
#include <iostream>

using namespace std;
using namespace mapreduce;


bool InfomallStatReducer::reduce(const void* key, KeyValueIterator& values, Collector& collector) const
{
    int totalCount = 0;
    while(values.hasMore()){
        int* pCount = (int*)(values.next() );
        totalCount += *pCount;
    }
    
    //cout << "reducer: " << *(char**)key << " " << totalCount << endl;
    
    collector.collect(key, &totalCount);
    
    return true;
}
