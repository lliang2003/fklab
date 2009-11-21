#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 16 21:18:52 CST 2008

import scipy.linalg
from scipy import array, matrix
import time, random
n = 4
a = array([random.randint(0, n*n) for e in range(n*n)])
a.resize(n,n)
m = matrix(a)
#print a*a
#print time.clock()
im = scipy.linalg.inv(m)
aa = matrix([1,2,3,4]).T
s = scipy.linalg.solve(m,aa)
print m
print s 
print m*s
print scipy.linalg.norm(m)
print scipy.linalg.eig(m)


