/**
 * @file InfomallStatReducer.hpp
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 * */
#ifndef INFOMALLSTATREDUCER_HPP
#define INFOMALLSTATREDUCER_HPP

#include "Reducer.hpp"

namespace mapreduce{

    class InfomallStatReducer : public Reducer{
    public:
    	bool reduce(const void* key, KeyValueIterator& values, Collector& collector) const;
    };

}

#endif /* INFOMALLSTATREDUCER_HPP:*/
