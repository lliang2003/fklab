#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 19 10:21:12 AM CST 2009

import math

def primes(m):
    pp = range(2, m+1)
    p = []
    while pp[0] ** 2 <= m:
        p.append(pp[0])
        pp = [e for e in pp if e % pp[0] != 0]
    p.extend(pp)
    return p


def func(a, b):
    if a < 2: a =2
    ps = primes(int(math.sqrt(b)) + 1)
    nn = range(a, b+1)
    flag = [True] * (b-a+1)
    for p in ps:
        s = (a-1)/p*p + p
        for j in range(max(p*2, s), b+1, p):
            if j%p == 0:
                flag[j-a] = False
    for n in nn:
        if flag[n-a]: print n
    print 

def mainflow():
    n  = input()
    for i in range(n):
        a, b = raw_input().split()
        func(int(a), int(b))

mainflow()
