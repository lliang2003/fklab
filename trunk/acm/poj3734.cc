/**
 * @file    3734.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 22 03:40:05 PM CST 2009
 */


#include <iostream>
#include <map>
using namespace std;

map<pair<int, int>, int> m;

int func(int num, int state = 0) {
    pair<int, int> p = make_pair(num, state);
    int result = 0;
    if (m.find(p) != m.end()) {
        return m[p];
    } else if (state == num) {
        result = max(state, 1);
    } else if (num < state) {
        result = 0;
    } else if (state == 0) {
        if (num % 2 == 0)
            result = func(num/2)*func(num/2) + func(num/2, 1)*func(num/2,1)*2 + func(num/2, 2)*func(num/2, 2);
        else
            result = func(num-1, 0)*2 + func(num-1, 1)*2;
    } else if (state == 2) {
        if (num % 2 == 0) 
            result = func(num/2)*func(num/2, 2)*2 + func(num/2, 1)*func(num/2, 1)*2;
        else
            result = func(num-1, 2)*2 + func(num-1, 1)*2;
    } else if (state == 1) {
        if (num % 2 == 0)
            result = func(num/2)*func(num/2, 1)*2 + func(num/2, 1)*func(num/2, 2)*2;
        else
            result = func(num-1, 1)*2 + func(num-1, 0) + func(num-1, 2);
    }
    result %= 10007;
    m[p] = result;
    return result;
}

void test() {
    for (int i = 0; i < 10; ++i)
        cout <<func(i) <<endl;
    for (int i = 999999990; i < 1000000000; ++i)
        cout <<func(i) <<endl;
}

int main() {
    //test(); return 0;
    int n, num;
    cin >>n;
    for (int i = 0; i < n; ++i) {
        cin >>num;
        cout <<func(num) <<endl;
    }
    return 0;
}


