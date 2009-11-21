/**
 * @file    trie.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 20 18:36:42 CST 2009
 */

#include <iostream>
#include <string>
#include <map>
#include <vector>
#include <algorithm>
using namespace std;

class TrieNode {
public:
    TrieNode(int k=0) {
        key = k;
        count = 0;
    }
    int key;
    map<int, TrieNode> children;
    size_t count;
};

class Trie {
public:

    void add(const vector<int> &v) {
        TrieNode *p = &root;
        for (int i = 0; i < v.size(); ++i) {
            p->children[v[i]].key = v[i];
            p = &(p->children[v[i]]); 
            p->count ++;
        }
    }

    void print(const char *prefix="", TrieNode* pnode=NULL) {
        char s[1000];
        if (pnode == NULL) {
            pnode = &root;
            sprintf(s, "%s", prefix);
        }
        else
            sprintf(s, "%s %04d", prefix, pnode->key);

        if (pnode->children.size() == 0) {
            //cout <<pnode->key <<" " <<pnode->count <<endl;
            printf("%s %04d %d\n", prefix, pnode->key, pnode->count);
        } else {
            //cout <<pnode->children.size() <<" " <<pnode->count <<endl;
            for (map<int, TrieNode>::iterator it = pnode->children.begin();
                    it != pnode->children.end(); ++it)
                print(s, &(it->second));
        }
    }
private:
    TrieNode root;
};

int test() {
    Trie t;
    t.print();

    return 0;
}

int main() {
    int n, m; 
    cin >>n;
    for (int i = 0; i < n; ++i) {
        cin >>m;
        map<pair<int, int>, Trie> mt;
        for (int j = 0; j < m; ++j) {
            int a, b;
            cin >>a >>b;
            vector<int> v(4,0);
            for (int k = 0; k < 4; ++k) {
                cin >>v[k];
            }
            mt[make_pair(a,b)].add(v);
        }
        char prefix[1000];
        for (map<pair<int, int>, Trie>::iterator it = mt.begin();
                it != mt.end(); ++it) {
            sprintf(prefix, "%02d %08d", it->first.first, it->first.second);
            it->second.print(prefix);
        }
        cout <<endl;
    }
}

