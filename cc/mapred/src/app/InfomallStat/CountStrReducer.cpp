/**
 * @file CountStrReducer.cpp
 *
 * */
#include "CountStrReducer.hpp"
#include "KeyValueIterator.hpp"
#include "Collector.hpp"
#include <string> 
#include <iostream>

using namespace std;
using namespace mapreduce;


bool CountStrReducer::reduce(const void* key, KeyValueIterator& values, Collector& collector) const
{
    int totalCount = 0;
    while(values.hasMore()){
        int* pCount = (int*)(values.next() );
        totalCount += *pCount;
    }
    
    //cout << "reducer: " << *(int*)key << " " << totalCount << endl;
    
    collector.collect(key, &totalCount);
    
    return true;
}
