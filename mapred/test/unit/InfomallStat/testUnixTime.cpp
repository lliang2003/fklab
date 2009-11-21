
#include <iostream>
#include <cstdlib>
#include <string>
#include <fstream>
#include <ctime>

using namespace std;

int main(int argc, char** argv)
{
    ifstream fin(argv[1], ios::in);
    int num = 0;
    char c;
    string url;
    string line;
    time_t mytime;
    time(&mytime);
    
    time_t mytime1 = 1158176665;
    time_t mytime2 = 1078822862;
    cout << ctime(&mytime1) << endl;
    cout << ctime(&mytime2) << endl;
    double timespan = difftime(mytime1, mytime2);
    cout << timespan/(3600*24*365) << endl;

    while(getline(fin, url)){
        getline(fin, line);
        num = atoi(line.c_str());
        int index = url.find("//");
        string host;
        if(index < string::npos){
            int start = url.find('/', index+2);
            if(start < string::npos){
                host = url.substr(0, start);
            }
            else{
                host = url;
            }
        }
        else{
            host = url;
        }
        //cout << url << " " << num << endl;
        //cout << host << endl;
        getline(fin, line);
        for(int i = 0; i < num; i ++)
        {
            getline(fin, line);
            mytime = atoi(line.c_str());
            struct tm* t = gmtime(&mytime);
            /*
            cout << 1900 + t->tm_year << " "
                 << 1 + t->tm_mon << " "
                 << t->tm_mday << " "
                 << t->tm_hour << endl;
            */     
        }
        getline(fin, line);
    }

    fin.close();
    return 0;
}
