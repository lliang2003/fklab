/**
 * @file InfomallStat.cpp
 * @description 
 *   do statistics on infomall metadata
 *
 * @author Chen Rishan
 * @date 2008-11-20
 * */


#include "InfomallStatMapper.hpp"
#include "InfomallStatReducer.hpp"
#include "CountStrMapper.hpp"
#include "CountStrReducer.hpp"
#include "TimeSpanMapper.hpp"
#include "TimeSpanReducer.hpp"
#include "TextFileFeeder.hpp"
#include "Mapper.hpp"
#include "Collector.hpp"
#include "Reducer.hpp"
#include "KeyValueIterator.hpp"
#include "TKeyValueFeeder.hpp"
#include "TKeyValueWriter.hpp"
#include "TStringHashableComparable.hpp"
#include "THashableComparable.hpp"
#include "ClassLoader.hpp"
#include "TextFileWriter.hpp"
#include "InfomallStatFeeder.hpp"
#include <string>
#include <vector>
#include <iostream>
#include <typeinfo>

using namespace std;
using namespace mapreduce;

REGISTER_CLASS_BEGIN();
REGISTER_FEEDER_CLASS(char*,char*);
REGISTER_FEEDER_CLASS(char*,int);
REGISTER_FEEDER_CLASS(int,int);
REGISTER_WRITER_CLASS(char*,int);
REGISTER_WRITER_CLASS(int,int);
REGISTER_TEXTFILEWRITER_CLASS(char*,int);
REGISTER_CLASS(TStringHashableComparable<char*>);
REGISTER_CLASS(THashableComparable<int>);
REGISTER_CLASS(InfomallStatUrlFeeder);
REGISTER_CLASS(InfomallStatHostFeeder);
REGISTER_CLASS(InfomallStatTimeSpanFeeder);
REGISTER_CLASS(InfomallStatMapper);
REGISTER_CLASS(InfomallStatReducer);
REGISTER_CLASS(CountStrMapper);
REGISTER_CLASS(CountStrReducer);
REGISTER_CLASS(TimeSpanMapper);
REGISTER_CLASS(TimeSpanReducer);
REGISTER_CLASS_END(); 

