/**
 * @file InfomallStatValue.hpp
 * @description: the value object 
 *
 * @author ChenRishan
 * @date 2008-11-20
 *
 * */

#ifndef INFOMALLSTATVALUE_HPP
#define INFOMALLSTATVALUE_HPP

#include <string>
#include <vector>
#include <iostream>

namespace mapreduce{
    
    class Archive;

    class InfomallStatValue{
    public:
        
        InfomallStatValue();
        ~InfomallStatValue();
        
        std::string uri;
        int crawl_count;
        std::vector<time_t> time_list;

        friend class Archive;
        
    };

    Archive& operator << (Archive& archive, const InfomallStatValue& value);
    
    Archive& operator >> (Archive& archive, InfomallStatValue*& value);
        
    std::ostream& operator << (std::ostream& out, const InfomallStatValue& value);
    
    std::istream& operator >> (std::istream& in, InfomallStatValue& value);
}

#endif
