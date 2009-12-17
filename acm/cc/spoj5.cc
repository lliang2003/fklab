/**
 * @file    5.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 19 05:58:22 PM CST 2009
 */


#include <iostream>
#include <string>
using namespace std;

void proc(string &s) {
    if (s.find_first_of("012345678") == string::npos) {
        cout <<'1';
        for (int i = 1; i < s.size(); ++i) cout <<"0";
        cout <<"1" <<endl;
        return;
    } 

    int ml = s.size() / 2 - 1;
    int mr = (s.size() + 1) / 2;
    int i = ml, j = mr;
    for (;i >= 0; --i, ++j) {
        if (s[i] > s[j]) {
            for (int k = 0; k < j; ++k) cout <<s[k];
            for (int k = j; k < s.size(); ++k) cout <<s[ml+mr-k];
            cout <<endl;
            return;
        } else if (s[i] < s[j]) {
            break;
        }
    }
    for (i = mr - 1; i >= 0; --i) {
        if (s[i] < '9') {
            j = mr + ml -i;
            s[i] ++;
            for (int k = 0; k <= i && k < j; ++k) cout <<s[k];
            for (int k = i+1; k < j; ++k) cout <<0;
            for (int k = j; k < s.size(); ++k) cout <<s[ml+mr-k];
            cout <<endl;
            return;
        }
    }
}

void test() {
    string ss;
    ss = "23432"; cout <<ss <<endl; proc(ss);
    ss = "2332"; cout <<ss <<endl; proc(ss);
    ss = "9999"; cout <<ss <<endl; proc(ss);
    ss = "999"; cout <<ss <<endl; proc(ss);
    ss = "2882"; cout <<ss <<endl; proc(ss);
    ss = "2992"; cout <<ss <<endl; proc(ss);
    ss = "29992"; cout <<ss <<endl; proc(ss);
    ss = "285582"; cout <<ss <<endl; proc(ss);
    ss = "285572"; cout <<ss <<endl; proc(ss);
    ss = "275582"; cout <<ss <<endl; proc(ss);
    ss = "185582"; cout <<ss <<endl; proc(ss);
    ss = "2"; cout <<ss <<endl; proc(ss);
}

int main() {
    //test();
    int n;
    string s;
    cin >>n;
    for (int round = 0; round < n; ++round) {
        cin >> s;
        proc(s);
    }

    return 0;
}


