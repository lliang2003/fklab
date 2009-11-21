/**
 * @file	check.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年06月01日 20时55分35秒 CST
 *
 */

#include <iostream>
#include <fstream>
using namespace std;

const int LR = 100;
const int LK = 10;

int main(int argc, char **argv) {
    if (argc != 2) {
        cout <<"Usage: " <<argv[0] <<" <input_file>" <<endl;
        return 0;
    }
    ifstream fin(argv[1], ios::binary);
    char s[LR], t[LR];
    fin.read(s, LR);
    int num = 1;
    while(fin.read(t, LR), fin.gcount() > 0) {
        if (memcmp(s, t, LK) > 0) {
            cout <<"Error in the " <<num <<"th record: " <<endl;
            cout.write(s, LR) <<endl;
            cout.write(t, LR) <<endl;
            return 1;
        }
        memcpy(s, t, LR);
        ++ num;
    }

    cout <<"Check " <<num <<" records ok." <<endl;
    return 0;
}

