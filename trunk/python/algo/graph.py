#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 04 10:57:30 AM CST 2009

import random, sys

def graph(n, m=0):
    h = [[m]*(i+1)+[random.randint(1,99) for j in range(i+1, n)] for i in range(n)]
    g = [[h[j][i]-m for j in range(i)]+[h[i][j]-m for j in range(i, n)] for i in range(n)]
    return g

def prim(g):
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    s = [0]
    v = range(1, len(g))
    es = []
    t = 0
    for i in range(len(g) - 1):
        min = 99999
        for r in v:
            for d in s:
                if g[r][d] < min:
                    min = g[r][d]
                    j = r
                    e = (r, d)
        s.append(j)
        v.remove(j)
        es.append(e)
        t += min
        print s, v, es
    print es, t

def kruskal(g):
    size = len(g)
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    parts = [[i] for i in range(len(g))]
    es = [(g[i][j], i, j) for i in range(size) for j in range(i+1,size)]
    es.sort()
    te = []
    t = 0
    for e in es:
        for part in parts:
            if e[1] in part:
                p1 = part
            if e[2] in part:
                p2 = part
        if p1 != p2:
            p = p1.extend(p2)
            parts.remove(p2)
            te.append(e)
            t += e[0]
            #print parts, te
    print te, t

g = graph(9, 9)
for r in g:print r
#prim(g)
kruskal(g)



    
    




