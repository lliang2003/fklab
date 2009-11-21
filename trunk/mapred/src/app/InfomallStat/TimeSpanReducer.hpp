/**
 * @file TimeSpanReducer.hpp
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 * */
#ifndef TIMESPANREDUCER_HPP
#define TIMESPANREDUCER_HPP

#include "Reducer.hpp"

namespace mapreduce{

    class TimeSpanReducer : public Reducer{
    public:
    	bool reduce(const void* key, KeyValueIterator& values, Collector& collector) const;
    };

}

#endif /* INFOMALLSTATREDUCER_HPP:*/
