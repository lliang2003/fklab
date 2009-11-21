#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Apr 24 14:59:56 CST 2009

import random

def average_sim(sim, item, group):
    if not group: return 1
    return sum([sim[item][x] for x in group])/float(len(group))

def kmeans(sim, group_count):
    num = len(sim)
    seeds = random.sample(range(0, num), group_count)
    groups = [[seeds[i]] for i in range(group_count)]

    for round in range(0, 10):
        print groups
        gsim = [[average_sim(sim, i, g) for g in groups] for i in range(num)]
        #for row in gsim: print row
        new_groups = [[] for g in range(group_count)]
        for i in range(num):
            mg = max(zip(gsim[i], range(group_count)))[1]
            new_groups[mg].append(i)
        if new_groups == groups: break
        groups = new_groups
        

sim = [[1]*9 for i in range(9)]
for i in range(1, 9):
    for j in range(0, i):
        sim[i][j] = sim[j][i] = random.random()
#print sim
kmeans(sim, 5)



