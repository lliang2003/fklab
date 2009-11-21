#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 04 09:38:47 AM CST 2009

v = range(5)
e = [(0,1), (0,2), (0, 3), (0, 4), (1,3), (2, 3), (2, 4), (3, 4)]

size = 5
max = 0
maxr = [0]*5

def check(r, index):
    global max, maxr
    print 'check', index, r
    for i in range(index + 1):
        if r[i] == 0: continue
        for j in range(i + 1, index + 1):
            if r[j] == 0: continue
            if (i, j) not in e: return False
    m = sum(r[:index+1])
    if m > max:
        print 'found', m, max, r
        max = m
        for i in range(index+1):
            maxr[i] = r[i]
    return True

def floor(r, index):
    return sum(r[:index+1]) + size - index - 1

def back(n):
    r = [-1]*n
    index = 0
    cnt = 0

    while index >= 0:
        cnt += 1
        print index, r
        r[index] += 1
        if r[index] == 2:
            r[index] = -1
            index -= 1
        elif floor(r, index) > max and check(r, index):
            if index < n-1:
                index += 1

    print cnt

back(5)
print max, maxr


