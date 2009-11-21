#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 20 22:43:40 CST 2009

from bisect import *
import random

def get_seeds(max):
    roots=(4,7)
    seeds=[]
    for i in range(1, max+1):
        for j in range(0, 2**i):
            n = j
            m = 0
            for k in range(i):
                m *= 10
                m += roots[j % 2]
                j /= 2
            #print m
            seeds.append(m);
    seeds.sort()
    #print seeds
    return seeds

def naive(max):
    seeds = get_seeds(max)
    maxn = 10**max
    size = len(seeds)
    result = seeds
    while True:
        #print size
        result += [a*b for a in result for b in result if a*b<=maxn]
        #print result
        result = list(set(result))
        if len(result) == size: 
            result.sort()
            return result
        size = len(result)

def lucky_number(max):
    seeds = get_seeds(max)
    maxn = 10**max
    result = set()
    v = [1]

    for s in seeds:
        #print len(result)
        #if s * s > maxn: break
        print 's=', s
        a = v
        while a:
            b = []
            for i in a:
                if i*s > maxn: break
                #if i*s in result: continue
                b.append(i*s)
                result.add(i*s)
            #print "a=", a
            print "b=", b
            v += b
            a = b
        #result.sort()
        #print result
        v.sort()
        v = v[:bisect(v, maxn/s)]
        #print v
    result = list(result)
    result.sort()
    return result

def mainflow():
    r = lucky_number(9)
    #print len(r), len(naive(4))
    #print ' '.join(map(str, naive(3)))
    #print len(naive(3)); return
    #print r
    #print ' '.join(map(str, r))
    print len(r);return
    #print naive(3)
    #return 
    n = input()
    for i in range(n):
        #a, b = map(int, raw_input().split())
        #a = random.randint(0, 10**12)
        a = 0
        b = random.randint(a, 10**12)
        print a, b
        print bisect(r, b) - bisect_left(r, a)

mainflow()



