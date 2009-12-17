/**
 * @file    poj3719.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 29 05:01:40 PM CST 2009
 */

#include <iostream>
#include <vector>
#include <set>
#include <algorithm>
#include <map>
#include <functional>
#include <cassert>
#include <cstdio>
using namespace std;

typedef vector<int> VI;
map<pair<string, VI>, double> mv;

struct Node {
    string s;
    int a, b, w;
};

void get_structure(vector<Node> &v, int i) {
    if (v[i].a < 0) {
        v[i].s = "1";
    } else {
        get_structure(v, v[i].a);
        get_structure(v, v[i].b);
        string sa = v[v[i].a].s;
        string sb = v[v[i].b].s;
        if (sa > sb) swap(v[i].a, v[i].b);
        v[i].s.push_back('0' + (sa[0]-'0') + (sb[0]-'0'));
        v[i].s += sb+sa;
    }
    //cout <<v[i].s <<endl;
}

bool next_combination(VI &v, int p) {
    if (v.front() >= v.back()) return false;
    int i = p-1, j = v.size()-1;
    while (v[i] >= v[j]) --i;
    while (j > p && v[j-1] > v[i]) --j;
    swap(v[i], v[j]);
    if (++i < p && ++j < v.size() && v[p-1] > v[j]) {
        int vsize = v.size();
        v.resize(v.size() + p - i);
        copy(v.begin() + i, v.begin() + p, v.begin() + vsize);
        copy(v.begin() + j, v.begin() + j + p - i, v.begin() + i);
        copy(v.begin() + j + p - i, v.end(), v.begin() + j);
        v.resize(vsize);
    }
    return true;
}

double proc(vector<Node> &v, int i, vector<int> vw) {
    if (v[i].a < 0) {
        v[i].w = vw[0];
        return 0;
    }
    if (mv.find(make_pair(v[i].s, vw)) != mv.end()) {
        v[i].w = 0; for (int j = 0;j < vw.size(); ++j) v[i].w += vw[j];
        return mv[make_pair(v[i].s, vw)];
    }
    //cout <<"proc: " <<v[i].s <<" ";for (int j = 0; j < vw.size(); ++j) cout <<vw[j] <<" "; cout <<"\n";
    int c = v[i].s[1] - '0';
    double mr = v.size() * 10000;
    do {
        double r = 0;
        vector<int> va(vw.begin(), vw.begin()+c);
        vector<int> vb(vw.begin()+c, vw.end());
        r += proc(v, v[i].a, va);
        r += proc(v, v[i].b, vb);
        int wa = v[v[i].a].w;
        int wb = v[v[i].b].w;
        v[i].w = wa + wb;
        double p = wa*1000.0/(wa+wb);
        r += (p>500)?(p-500):(500-p);
        //cout <<vw.size() <<" " <<v.size() << "\t";
        //for (int j = 0; j < c; ++j) cout <<vw[j] <<" "; cout <<"\t";
        //for (int j = c; j < vw.size(); ++j) cout <<vw[j] <<" "; cout <<endl;
        //cout <<"i=" <<i <<" " <<wa <<" " <<wb <<" " <<r <<endl;
        if (r < mr) mr = r;
    } while (next_combination(vw, c));
    sort(vw.begin(), vw.end());
    mv[make_pair(v[i].s, vw)] = mr;
    //cout <<"proc: " <<v[i].s <<" ";for (int j = 0; j < vw.size(); ++j) cout <<vw[j] <<" "; cout <<mr<<"\n";
    return mr;
}

void test() {
    VI v;
    v.push_back(1);
    v.push_back(2);
    v.push_back(3);
    v.push_back(3);
    v.push_back(5);
    do {
        for (int i = 0; i < v.size(); ++i) cout <<v[i] <<" ";
        cout <<endl;
    } while (next_combination(v, 3));
}

int main() {
    //test(); return 0;
    int tc;
    cin >>tc;
    while (tc--) {
        int n, m;
        cin >>n;
        vector<Node> v(n+1);
        for (int i = 1; i <= n; ++i) cin >> v[i].a >>v[i].b;
        get_structure(v, 1);
        cin >>m;
        vector<int> vw(m);
        for (int i = 0; i < m; ++i) cin >>vw[i];
        sort(vw.begin(), vw.end());
        printf("%.3f\n", proc(v, 1, vw));
        //cout <<proc(v, 1) <<endl;
        //break;
    }
    return 0;
}


