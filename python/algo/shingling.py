#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 05 20:27:18 CST 2009

import random

def random_shuffle(a):
    size = len(a)
    for i in range(size-1):
        t = a[i]
        s = random.randint(i+1, size-1)
        a[i] = a[s]
        a[s] = t

def jc(a, b):
    sa = set(a)
    sb = set(b)
    r = .0 + len(sa.intersection(sb))
    r /= len(sa.union(sb))
    return r

def shingle(a, w):
    size = len(a)
    s = set([])
    for i in range(size-w+1):
        s.add(tuple(a[i:i+w]))
    return list(s)

x = [1,2,3,4,5]*55
y = [1,2,3,4,5]*55
random_shuffle(x)
random_shuffle(y)
xx = shingle(x, 3)
yy = shingle(y, 3)
import md5
xm = [md5.new(str(e)).hexdigest() for e in xx]
ym = [md5.new(str(e)).hexdigest() for e in yy]
print len(xx)
print len(yy)
print jc(xx,yy)
print jc(xm,ym)

cnt = 0
n = 10000
for i in range(1000):
    size = min(len(xx), len(yy))
    #pi = random.sample(range(n), n)
    #am = [pi[int(e, 16)%n] for e in xm]
    #bm = [pi[int(e, 16)%n] for e in ym]
    d = random.randint(1000000, 2000000)
    am = [int(e, 16) % d for e in xm]
    bm = [int(e, 16) % d for e in ym]
    if min(am) == min(bm):
        cnt += 1

print cnt


