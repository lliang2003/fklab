/**
 * @file CountStrMapper.cpp
 *
 * */
#include "CountStrMapper.hpp"
#include "Collector.hpp"
#include <stdio.h>
#include <cstdlib>
#include <iostream>

using namespace std;
using namespace mapreduce;

CountStrMapper::CountStrMapper()
{
}

CountStrMapper::~CountStrMapper()
{
}

bool CountStrMapper::map(const void* key, const void* value, Collector& collector) const
{
    int count = 1;
    if(key && value){
        
        collector.collect(value, &count);
    }
    
    return true;
}

