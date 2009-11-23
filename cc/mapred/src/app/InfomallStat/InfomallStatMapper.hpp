/**
 * @file InfomallStatMapper.hpp
 * @description it is implementation of the interface mapper 
 * the input is <char*,InfomallStatValue>
 * 
 * @author Chen Rishan
 * @date 2008-11-20
 * */
#ifndef INFOMALLSTATMAPPER_HPP
#define INFOMALLSTATMAPPER_HPP

#include "Mapper.hpp"


namespace mapreduce{

    class InfomallStatMapper: public Mapper{
    public:
        InfomallStatMapper();
        ~InfomallStatMapper();
        bool map(const void* key, const void* value, Collector& collector) const;
    private:
    };

}

#endif /* INFOMALLSTATMAPPER_HPP:*/
