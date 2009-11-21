#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 05 19:40:49 CST 2009

import math

def entropy(d):
    e = 0
    for p in d:
        e -= p*math.log(p, 2)
    return e

print entropy([0.5, 0.5])
print entropy([0.2, 0.8])
print entropy([0.25,0.25, 0.25, 0.25])
print entropy([0.25] * 4)
print entropy([0.05] * 20)

a=[(1,2), (3,4)]
b=[(1,3), (4,5)]

def cos(a, b):
    s = 0
    for i in range(len(a)):
        s += a[i]*b[i]
    aa = math.sqrt(sum(i*i for i in a))
    bb = math.sqrt(sum(i*i for i in b))
    return s / aa / bb

print cos((-1,0), (-1, 1))
print cos((-2,0), (-2, 2))

