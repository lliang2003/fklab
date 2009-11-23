/**
 * @file TimeSpanMapper.hpp
 * @description it is implementation of the interface mapper 
 * the input is <int*,null>
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 * */
#ifndef TIMESPANMAPPER_HPP
#define TIMESPANMAPPER_HPP

#include "Mapper.hpp"


namespace mapreduce{

    class TimeSpanMapper: public Mapper{
    public:
        TimeSpanMapper();
        ~TimeSpanMapper();
        bool map(const void* key, const void* value, Collector& collector) const;
    private:
    };

}

#endif 
