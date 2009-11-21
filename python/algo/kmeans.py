#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 05 17:08:28 CST 2009

import random, sys

sys.stdout=sys.stderr
size=18
dim=3
k=3
pts=[[random.randint(-9,9) for i in range(dim)] for j in range(size)]

print pts

s = [None]*3
for i in range(k):
    s[i] = [p for p in pts[i]]

print s


clast = None
while True:
    cluster = [[] for i in range(k)]
    for i in range(size):
        p = pts[i]
        dmin = None
        for c in range(k):
            d = 0
            for j in range(dim):
                d += (p[j] - s[c][j]) ** 2
            if dmin is None or d < dmin:
                dmin = d
                cc = c
        cluster[cc].append(i)

    print clast, cluster

    if cluster == clast:
        break
    clast = [[e for e in c] for c in cluster]
    raw_input()

    for i in range(k):
        s[i] = [0]*dim
        for j in range(dim):
            s[i][j] = sum(pts[i][j] for i in cluster[i]) / len(cluster[i])
    print s


print cluster






