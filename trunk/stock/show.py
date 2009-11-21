#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Aug 26 07:40:11 HKT HKT 2009

import os, sys
from pylab import *

#fname = sys.argv[1]
fname = "tmp"

data = [[], [], []]

for line in open(fname):
    parts = map(float, line.split())
    for i in range(len(parts)):
        data[i].append(parts[i])

#for d in data[1:]:
    #plot(data[0], d)
#show()

up = [0]*50
down = [0]*50
span = 0
for i in range(1, len(data[1])):
    if data[1][i] * data[1][i-1] > 0:
        span += 1
    else:
        if data[1][i] > 0: down[span-1] += 1
        else: up[span-1] += 1
        span = 1

print up
print down
    
