#!/usr/bin/env python
print "Hello, world!"
from time import time

begin = time()
data = []
for line in open("D:\data.txt", "r"):
    tmp = []
    for x in line.split(","):
        tmp.append(float(x))
    data.append(tmp)

dimension = len(data[0]) - 1
print "number of training data:", len(data)
print "data dimemsion:", dimension

for line in open("D:\test.txt", "r"):
    test = line.split(",")
    ans = 0
    for d in data:
        dis = 0
        for i in range(0, dimension):
            dis += (float(test[i])-d[i+1])**2
        if dis == 0: dis = 1
        ans += d[0] / dis
    if ans > 0: print "positive"
    else: print "negative"
    
print "cost :", time() - begin, "s"
    

            



