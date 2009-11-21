/**
 * @file InfomallStatMain.cpp
 *
 * @author Chen Rishan
 * @date 2008-06-23
 * */

#include "TaskConfigure.hpp"
#include "SetMapper.hpp"
#include "Sorter.hpp"
#include "SetReducer.hpp"
#include "MapReduceException.hpp"
#include "TextFileFeeder.hpp"
#include "TKeyValueFeeder.hpp"
#include "InfomallStatValue.hpp"
#include "InfomallStatFeeder.hpp"
#include "FileStream.hpp"
#include "FileStreamLocal.hpp"
#include <string>
#include <vector>
#include <iostream>
#include <cstdlib>
#include "boost/filesystem.hpp"

using namespace std;
using namespace boost::filesystem;
using namespace mapreduce;

class InfomallStatMain{
public:
    void run(char* dllFile, char* inputFile, char* outputFileName, int mode);
};

void InfomallStatMain::run(char* dllFile, char* inputFile, char* outputFile, int mode)
{
    TaskConfigure configure;
    int recordCnt;

    configure.setDllName(dllFile);
    
    /*
        cout << "mode: " << endl
             << "    1. url, count" << endl
             << "    2. host, count " << endl
             << "    3. count, url_number " << endl
             << "    4. count, host_number " << endl;
    */
    //set up mapper reader class
    if(mode == 1)
        configure.setMapperRecordReaderClass("InfomallStatUrlFeeder");
    else if(mode == 2)
        configure.setMapperRecordReaderClass("InfomallStatHostFeeder");
    else if(mode == 3 || mode == 4)
        configure.setMapperRecordReaderClass("TKeyValueFeeder<char*,int>");
    else if(mode == 5)
        configure.setMapperRecordReaderClass("InfomallStatTimeSpanFeeder");


    switch(mode){
        case 1:
        case 2:
            {
                configure.setMapperOutKeyClass("char*");
                configure.setMapperOutValueClass("int");
                configure.setReducerClass("InfomallStatReducer");
                configure.setMapperClass("InfomallStatMapper");
                //set up reducer writer class
                configure.setReducerRecordWriterClass("TKeyValueWriter<char*,int>"); 
            }
            break;

        case 3:
        case 4:
            {
                configure.setMapperOutKeyClass("int");
                configure.setMapperOutValueClass("int");
                configure.setReducerClass("CountStrReducer");
                configure.setMapperClass("CountStrMapper");
    
                //set up reducer writer class
                configure.setReducerRecordWriterClass("TKeyValueWriter<int,int>"); 
            }
            break;

        case 5:
            {
                configure.setMapperOutKeyClass("int");
                configure.setMapperOutValueClass("int");
                configure.setReducerClass("TimeSpanReducer");
                configure.setMapperClass("TimeSpanMapper");
    
                //set up reducer writer class
                configure.setReducerRecordWriterClass("TKeyValueWriter<int,int>"); 
            }
            break;
        default:
            break;
    };

    cout << "configure over" << endl;
    vector<string> partitionFiles;

    partitionFiles.push_back("0.par");
    if(exists(partitionFiles[0])) 
        remove(path(partitionFiles[0]));

    cout << "setmapper ready..." << endl;
    SetMapper *setMapper;
    setMapper = new SetMapper(configure, inputFile, partitionFiles, false);
    cout << "begin map" << endl;
    recordCnt = setMapper->map();
    delete setMapper;
    cout << "run setmapper return : recordCnt = " << recordCnt << endl;

    string sortFile = "sorted.dat";
    if(exists(sortFile)) remove(path(sortFile));
    Sorter *sorter = new Sorter(configure, sortFile, partitionFiles);
    cout << "begin sort" << endl;
    if(!sorter->sort()){
        cout << "run sort fail..., bye!" << endl;
    }
    delete sorter;

    cout << "complete sort" << endl;
    string resultFile(outputFile);

    if(exists(resultFile)) 
        remove(path(resultFile));

    SetReducer *setReducer;
    setReducer = new SetReducer(configure, sortFile, resultFile, true, false);
    cout << "begin reduce" << endl;
    recordCnt = setReducer->reduce();
    cout << "run setreducer return : recordCnt = " << recordCnt << endl;
    delete setReducer;
}

/*
bool CheckResults(string file, bool check)
{
    TKeyValueFeeder<char*, InfomallStatValue> reader;
    reader.initialize(0, true);
    FileStream* stream = NULL;
    stream = new FileStreamLocal(file, FileStream::ReadOnly, false);
    reader.open(stream);
    if(!check)
        cout << "===================" << endl;
    void* key = reader.createKeys();
    void* value = reader.createValues();
    int count = 0;
    void* doKey = key;
    void* doValue = value;
    
    while(reader.next(doKey, doValue) ){
        if(!check){
            cout << *(char**)doKey << " " << ((InfomallStatValue*)doValue)->pagerank << " " << ((InfomallStatValue*)doValue)->linkoutNumber << endl;
        }
        count ++;
        reader.flush();
    }
    if(!check){
        cout << "===================" << endl;
        cout << count << endl;
    }
    
    reader.releaseKeys(key);
    reader.releaseValues(value);
    reader.close();
    delete stream;
    return false;
}
*/

//=======================
int main(int argc, char* argv[])
{
    if(argc!=5) {
        cout << "usage : " << argv[0] << " <app basename> <inputFileName> <outputFileName> <mode>" << endl;
        cout << "mode: " << endl
             << "    1. url, count" << endl
             << "    2. host, count " << endl
             << "    3. count, url_number " << endl
             << "    4. count, host_number " << endl
             << "    5. span, url_number " << endl;
        return -1;
    }

    InfomallStatMain* app = new InfomallStatMain();
    
    int mode = atoi(argv[4]);
    char temp[11] = "middle.dat";
    try{
        if(mode == 1 || mode == 2 || mode == 5)
            app->run(argv[1], argv[2], argv[3], mode);
        else if(mode == 3 || mode == 4){
            app->run(argv[1], argv[2], temp, mode-2);
            app->run(argv[1], temp, argv[3], mode);
        }
    }
    catch(MapReduceException& e){
        cout << "catch exception: " << e.what() << endl;
    }
    
    return 0;
}

