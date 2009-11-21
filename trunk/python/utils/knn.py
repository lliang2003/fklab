#!/usr/bin/env python
from time import *
from heapq import *
import math

def initData():
    data = []
    for line in open("D:\data.txt", "r"):
        tmp = []
        for x in line.split(","):
            tmp.append(float(x))
        data.append(tmp)
    print "Number of training data:", len(data)
    return data


def testData(k, data):
    e = 0
    s = 0
    for test in data:
        ans = 0
        neighbors = []
        for d in data:
            tmp = [0, 0]
            dis = 0
            for i in range(0, dimension):
                dis += ((test[i]-d[i])*ss[i])**2
            if dis == 0: continue
            tmp[0] = math.sqrt(dis)
            tmp[1] = d[dimension] - 0.5
            neighbors.append(tuple(tmp))

        nn = nsmallest(k, neighbors)
        for x in nn:
            ans += x[1]/x[0]
#        print ans, nn
        if ans > 0: ans = 1
        else: ans = 0
        s += 1
        if int(test[dimension]) != ans:
            e += 1
#            print e, s, test[dimension], ans
    return e

dimension = 57
begin = time()
data = initData()
ss = []
for i in range(0, dimension):
    ss.append(1)
    
for x in range(0, dimension):
    ss[x] = 0.1
    error = testData(20, data)
    for i in range(0, 12):
        ss[x] *= 1.5
        e = testData(20, data)
        print x, i, ss[x], e, error
        if e > error:break
    
print "Cost", time()-begin, "seconds"
    

            



