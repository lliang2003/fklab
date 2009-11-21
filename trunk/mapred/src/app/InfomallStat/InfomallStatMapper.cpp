/**
 * @file InfomallStatMapper.cpp
 *
 * */
#include "InfomallStatMapper.hpp"
#include "Collector.hpp"
#include <stdio.h>
#include <cstdlib>
#include <iostream>

using namespace std;
using namespace mapreduce;

InfomallStatMapper::InfomallStatMapper()
{
}

InfomallStatMapper::~InfomallStatMapper()
{
}

bool InfomallStatMapper::map(const void* key, const void* value, Collector& collector) const
{
    if(key && value){
        
        collector.collect(key, value);
    }
    
    return true;
}

