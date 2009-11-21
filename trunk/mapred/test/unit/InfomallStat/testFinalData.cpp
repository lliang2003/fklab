#include <iostream>
#include <cstdlib>
#include <string>
#include <fstream>
#include <ctime>
#include "TKeyValueFeeder.hpp"
#include "TKeyValueWriter.hpp"
#include "FileStream.hpp"
#include "FileStreamLocal.hpp"

using namespace std;
using namespace mapreduce;

int main(int argc, char** argv)
{
    TKeyValueFeeder<int,int> feeder;
        
    feeder.initialize(0, true);
        
    feeder.open(new FileStreamLocal(argv[1], FileStream::ReadOnly, false));
        
    void* key = feeder.createKeys();
    void* value = feeder.createValues();
    void* doKey = key;
    void* doValue = value;
    int count = 0;
        
    while(feeder.next(doKey, doValue) ){
        cout << *(int*)doKey << " " << *(int*)doValue << endl;
        feeder.flush();
        count ++;
    }

    cout << count << endl;
    return 0;
}
