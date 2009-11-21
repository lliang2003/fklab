#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 29 09:31:11 PM CST 2008

from numpy import *
a = array(xrange(1,9), int)
b = a.astype('float')
print a.shape
print a.dtype
print a+a
print a*a
print a/a
print a/3
print b/3
c = arange(6, dtype='float')
print c
d = c * pi / len(c)
print d
print d[0:6:2]
t = array([False,True,False,True,True], bool)
print d[t]
dt = sort(abs(d-1.5))
print dt
print abs(d-1.5) <= dt[3]
e = d[abs(d-1.5) <= dt[3]]
print e

a = array([[0,1,2],[3,4,5],[6,7,8]])
b=a[1:3,0:2]
print b
bc = b.copy()
a[1:3,0:2] = 2 
print b
print bc
print dot(b, bc)
print b * bc

x,y = mgrid[0:5:1, 0:10:2]
print x
print y
print x + y
print (x+y)**2

a = zeros((2,2), dtype=int)
print a
print ones((3,4), dtype=float)

a = array([[0,1],[2,3]])
print a.repeat(2, axis=0)
print a.repeat(3, axis=1)
print a.repeat(2, axis=None)

