/**
 * @file    4194.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 21 07:44:28 CST 2009
 */

#include <iostream>
#include <vector>
#include <set>
#include <algorithm>
using namespace std;

vector<int64_t> get_seeds(int power) {
    vector<int64_t> seeds;
    int64_t m = 1;
    int roots[] = {4,7};
    for (int i = 0; i < power; ++i) {
        m *= 2;
        for (int64_t j = 0; j < m; ++j) {
            int64_t a = j, n = 0;
            for (int k = 0; k <= i; ++k) {
                n *= 10;
                n += roots[a%2];
                a /= 2;
            }
            seeds.push_back(n);
        }
    }
    return seeds;
}

set<int64_t> lucky_number(int power) {
    int64_t maxn=1;
    for (int i = 0; i < power; ++i) maxn *= 10;
    vector<int64_t> seeds = get_seeds(power);
    sort(seeds.begin(), seeds.end());
    //cout <<seeds.size() <<endl;
    set<int64_t> lnumbers;
    set<int64_t> all_candidates;
    vector<int64_t> candidates;
    all_candidates.insert(1);
    for (int i = 0; i < seeds.size(); ++i) {
        //cout <<i <<endl;
        //cout <<lnumbers.size() <<" " <<all_candidates.size() <<endl;
        //vector<int64_t>::iterator it = upper_bound(all_candidates.begin(), all_candidates.end(), m);
        //all_candidates.resize(it-all_candidates.begin());
        candidates.clear();
        set<int64_t>::iterator it = all_candidates.begin(); 
        /*
        cout <<"all candidates: ";
        for (set<int64_t>::iterator jt = all_candidates.begin(); 
                jt != all_candidates.end(); ++jt) cout <<*jt <<" "; 
        cout <<endl;
        */
        int64_t maxc = maxn/seeds[i]+1;
        while (it != all_candidates.end()) {
            //cout <<*it <<endl;
            if (*it >=maxc) break;
            candidates.push_back(*it);
            ++it;
        }
        all_candidates.erase(it, all_candidates.end());
        if (0) {
            cout <<"all candidates: ";
            for (set<int64_t>::iterator jt = all_candidates.begin(); 
                    jt != all_candidates.end(); ++jt) cout <<*jt <<" "; 
            cout <<endl;
            cout <<"seed: " <<seeds[i] <<" maxc:" <<maxc<<endl;
        }
        while (candidates.size() > 0) {
            vector<int64_t> results;
            for (int j = 0; j < candidates.size(); ++j) {
                if (candidates[j] > maxc) break;
                int64_t n = seeds[i]*candidates[j];
                if (n > maxn) break;
                lnumbers.insert(n);
                if (n <= maxc) {
                    results.push_back(n);
                    all_candidates.insert(n);
                }
            }
            if (0) {
                cout <<"candidates:" ;
                for (int j = 0; j < candidates.size(); ++j)
                    cout <<candidates[j] <<" ";
                cout <<endl;
                cout <<"results:" ;
                for (int j = 0; j < results.size(); ++j)
                    cout <<results[j] <<" ";
                cout <<endl; 
                cout <<"lnumbers:" <<lnumbers.size() <<endl;
            }
            candidates = results;
        }
        //sort(all_candidates.begin(), all_candidates.end());

    }
    return lnumbers;
}



int main() {
    set<int64_t> vv = lucky_number(9);
    set<int64_t>::iterator it = vv.begin(); 
    vector<int64_t> v;
    for (;it != vv.end(); ++it) v.push_back(*it);
    //for (int i = 0; i < v.size(); ++i) cout <<v[i] <<" ";
    //cout <<v.size() <<endl;

    int n; 
    cin >>n; 
    for (int i = 0; i < n; ++i) {
        int64_t a, b;
        cin >>a >>b;
        cout <<upper_bound(v.begin(), v.end(), b)
            - lower_bound(v.begin(), v.end(), a) <<endl;
    }

    return 0;
}

