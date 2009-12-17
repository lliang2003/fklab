/**
 * @file    poj3666.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 23 21:58:42 CST 2009
 */


#include <iostream>
#include <vector>
#include <set>
using namespace std;

int main() {
    int n; 
    cin >>n; 
    vector<int> v(n);
    set<int> ele;
    for (int i = 0; i < n; ++i) {
        cin >>v[i]; ele.insert(v[i]);
    }

    vector<int> cost(ele.size());
    for (int i = 0, j = 0; i < n; ++i) {
        for (set<int>::iterator it = ele.begin(); 
                it != ele.end(); ++it, ++j) {
            cost[j] += abs(*it-v[i]);
            if (j > 0) cost[j] = min(cost[j-1], cost[j]);
        }
    }

    cout <<cost.back() <<endl;

    return 0;
}


