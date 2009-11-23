/**
 * @file CountStrMapper.hpp
 * @description it is implementation of the interface mapper 
 * the input is <char*,int>
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 * */
#ifndef COUNTSTRMAPPER_HPP
#define COUNTSTRMAPPER_HPP

#include "Mapper.hpp"


namespace mapreduce{

    class CountStrMapper: public Mapper{
    public:
        CountStrMapper();
        ~CountStrMapper();
        bool map(const void* key, const void* value, Collector& collector) const;
    private:
    };

}

#endif /* COUNSTRMAPPER_HPP:*/
