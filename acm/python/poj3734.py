#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 18 05:18:37 PM CST 2009

def fun(num):
    cnt = 0
    for i in xrange(0, 4**num):
        a = []
        s = i
        for j in xrange(0, num):
            a.append(s%4)
            s /= 4
        if a.count(0) % 2 == 0 and a.count(1) % 2 == 0:
            #print 'y',
            cnt += 1
            #print a
        #else:print 'n', 
        #print i, a

    return cnt

s={}

def fun2(num, r=0):
    if (num, r) in s: 
        return s[(num, r)]
    if r == num: re = max(r, 1)
    elif num < r: re = 0
    elif r == 0 or r == 2:
        re = fun2(num-1, r)*2 + fun2(num-1, 1)*2
    else:
        re = fun2(num-1, r)*2 + fun2(num-1, 0) + fun2(num-1, 2)
    #print 'num=%d r=%d result=%d'%(num, r, re)
    s[(num, r)] = re
    return re

def fun3(num, r=0):
    if (num, r) in s: 
        return s[(num, r)]
    if r == num: re = max(r, 1)
    elif num < r: re = 0
    elif r == 0 or r == 2:
        re = fun3(num-1, r)*2 + fun3(num-1, 1)*2
    else:
        re = fun3(num-1, r)*2 + fun3(num-1, 0) + fun3(num-1, 2)
    #print 'num=%d r=%d result=%d'%(num, r, re)
    re %= 10007
    s[(num, r)] = re
    return re

def fun4(num, r = 0):
    if (num, r) in s: 
        return s[(num, r)]
    if r == num: 
        re = max(r, 1)
    elif num < r: 
        re = 0
    elif r == 0:
        if num % 2 == 0: 
            re = fun4(num/2)**2 + fun4(num/2, 1)**2*2 + fun4(num/2, 2)**2 
        else:
            re = 2*fun4(num-1, 0) + 2*fun4(num-1, 1)
    elif r == 2:
        if num % 2 == 0: 
            re = fun4(num/2)*fun4(num/2, 2)*2 + fun4(num/2, 1)**2*2 
        else:
            re = 2*fun4(num-1, 2) + 2*fun4(num-1, 1)
    elif r == 1:
        if num % 2 == 0: 
            re = fun4(num/2)*fun4(num/2, 1) * 2 + fun4(num/2, 1)*fun4(num/2, 2)*2 
        else:
            re = 2*fun4(num-1, 1) + fun4(num-1, 0) + fun4(num-1, 2)

    #print 'num=%d r=%d result=%d'%(num, r, re)

    re %= 10007
    s[(num, r)] = re
    return re

fun4(2)
for n in range(1,9):
    #print n
    #break
    s= {}
    #print fun(n), fun2(n)
    #print fun2(n) % 10007  , 
    #print fun4(n), len(s)
    #s = {}
    print n, fun4(n), len(s)

for n in range(999999990, 1000000000):
    print n, fun4(n)

#print fun3(99999)
#print fun3(5)
#print fun4(5)
            


