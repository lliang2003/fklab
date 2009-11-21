/**
 * @file    27.cpp
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 20 21:40:30 CST 2009
 */

#include <iostream>
#include <vector>
#include <string>
using namespace std;

char line[100];

int main() {
    int n, m; 
    cin >>n;
    for (int i = 0; i < n; ++i) {
        vector<string> vs;
        cin >>m;
        cin.getline(line, 99);
        for (int j = 0; j < m; ++j) {
            cin.getline(line, 99);
            vs.push_back(line);
        }
        sort(vs.begin(), vs.end());
        string last = vs[0];
        int count = 1;
        for (int j = 1; j < m; ++j) {
            if (vs[j] == last)
                ++count;
            else {
                cout <<last <<" " <<count <<endl;
                last = vs[j];
                count = 1;
            }
        }
        cout <<last <<" " <<count <<endl;
        cout <<endl;
    }
    return 0;
}


