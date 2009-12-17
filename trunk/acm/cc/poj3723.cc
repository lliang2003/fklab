/**
 * @file    3723.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 23 10:26:43 AM CST 2009
 */


#include <iostream>
#include <algorithm>
#include <vector>
#include <cstdlib>
#include <queue>
#include <cstdio>
#include <functional>
using namespace std;

typedef pair<int, pair<int, int> > Edge;

int kruskal(int num, int default_distance, vector<pair<int, pair<int, int> > > &edges) {
    //vector<pair<int, int> > edge_seq;
    vector<int> parent;
    for (int i = 0; i < num; ++i) parent.push_back(i);
    sort(edges.begin(), edges.end());
    int group_count = num;
    int cost = 0;
    for (int i = 0; i < edges.size() && group_count > 1; ++i) {
        int dist = edges[i].first;
        int a = edges[i].second.first;
        int b = edges[i].second.second;
        int pa = a, pb = b;
        while (pa != parent[pa]) pa = parent[pa];
        while (pb != parent[pb]) pb = parent[pb];
        if (pa == pb) continue;
        //edge_seq.push_back(edges[i].second);
        parent[pa] = pb;
        cost += dist;
        group_count --;
    }
    return cost + (group_count-1)*default_distance;
}

int kruskal2(int num, int default_distance, const vector<Edge> &edges) {
    vector<int> parent;
    for (int i = 0; i < num; ++i) parent.push_back(i);
    priority_queue<Edge, vector<Edge>, greater<Edge> > pq(edges.begin(), edges.end());
    int group_count = num;
    int cost = 0;
    while (pq.size() > 0 && group_count > 1) {
        pair<int, pair<int, int> > edge = pq.top();
        pq.pop();
        int dist = edge.first;
        int a = edge.second.first;
        int b = edge.second.second;
        int pa = a, pb = b, ca = 0, cb = 0;
        while (pa != parent[pa]) {pa = parent[pa]; ca++;}
        while (pb != parent[pb]) {pb = parent[pb]; cb++;}
        if (pa == pb) continue;
        if (ca < cb) parent[pa] = pb;
        else parent[pb] = pa;
        cost += dist;
        group_count --;
    }
    return cost + (group_count-1)*default_distance;
}

int main() {
    int ncases;
    cin >>ncases;
    while (ncases --) {
        vector<Edge> edges;
        int n, m, r, x, y, d;
        cin >>n >>m >>r;
        for (int i = 0; i < r; ++i) {
            scanf("%d %d %d", &x, &y, &d);
            //cin >>x >>y >>d;
            edges.push_back(make_pair(10000-d, make_pair(x, y+n)));
        }
        cout <<kruskal2(n+m, 10000, edges) + 10000 <<endl;
    }

    return 0;
}


