/**
 * @file    11.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 20 05:37:53 PM CST 2009
 */


#include <iostream>
using namespace std;

int main() {
    int n, num, r;
    cin >>n;
    for (int i = 0; i < n; ++i) {
        cin >>num;
        r = 0;
        while (num > 0) {
            num /= 5;
            r += num;
        }
        cout <<r <<endl;
    }

    return 0;
}


