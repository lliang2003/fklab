/**
 * @file InfomallStatValue.cpp
 *
 * */

#include "InfomallStatValue.hpp"
#include "Archive.hpp"
#include "ArchiveBuffer.hpp"
#include <cstring>
#include <iostream>
#include <ctime>
#include <cassert>

using namespace std;
using namespace mapreduce;


InfomallStatValue::InfomallStatValue()
{
    crawl_count = 0;
}

InfomallStatValue::~InfomallStatValue()
{

}

namespace mapreduce{

std::ostream& operator << (std::ostream& out, const InfomallStatValue& value)
{
    out << value.uri << endl;
    out << value.crawl_count << endl;
    out << '{' << endl;
    assert(value.crawl_count == value.time_list.size() );
    
    for(int i = 0; i < value.crawl_count; i ++){
        out << value.time_list[i] << endl;
    }
    out << '}' << endl;
                                                 
    return out;
}                                                          
 
std::istream& operator >> (std::istream& in, InfomallStatValue& value)
{
    string line;
    getline(in, value.uri);
    getline(in, line);
    value.crawl_count = atoi(line.c_str() );
    getline(in, line);
    for(int i = 0; i < value.crawl_count; i ++)
    {
        getline(in, line);
        value.time_list.push_back(atoi(line.c_str() ));
    }
    getline(in, line);
    return in;
}
    
Archive& operator << (Archive& archive, const InfomallStatValue& value)
{
    archive.getBuffer()->appendZString(value.uri.c_str() );
    archive.getBuffer()->appendData(&value.crawl_count, sizeof(int) );
    for(int i = 0; i < value.crawl_count; i ++){
        archive.getBuffer()->appendData(&value.time_list[i], sizeof(time_t) );
    }
    return archive;
}

Archive& operator >> (Archive& archive, InfomallStatValue*& value)
{
    if(&archive == NULL){
        cout << "gan!!" << endl;
    }
    char* puri = (char*)archive.getBuffer()->readZString();
    int* pcount = (int*)archive.getBuffer()->readData(sizeof(int));
    if(value == NULL){
        value = new InfomallStatValue();
    }
    value->uri = *puri;
    value->crawl_count = *pcount;

    assert(*pcount > 0);
    
    for(int i = 0; i < *pcount; i ++){
        value->time_list.push_back(*( (time_t*)archive.getBuffer()->readData(sizeof(time_t) ) ));
    }
   
    return archive;
}


}
