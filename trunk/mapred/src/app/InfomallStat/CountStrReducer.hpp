/**
 * @file CountStrReducer.hpp
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 * */
#ifndef COUNTSTRREDUCER_HPP
#define COUNTSTRREDUCER_HPP

#include "Reducer.hpp"

namespace mapreduce{

    class CountStrReducer : public Reducer{
    public:
    	bool reduce(const void* key, KeyValueIterator& values, Collector& collector) const;
    };

}

#endif /* INFOMALLSTATREDUCER_HPP:*/
