#!/usr/bin/env python

import sys, os
sys.stdout=sys.stderr

def lcs(a, b):
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    nr = len(a) + 1
    nc = len(b) + 1
    c = [[0]*nc for i in range(nr)]
    s = [['*']*nc for i in range(nr)]
    for i in range(1, nr):
        for j in range(1, nc):
            if a[i-1] == b[j-1]:
                c[i][j] = c[i-1][j-1] + 1
                s[i][j] = '\\'
            else:
                el = c[i][j-1] 
                eu = c[i-1][j] 
                c[i][j] = el > eu and el or eu
                s[i][j] = el > eu and '-' or '|'
    for r in c:
        print r
    for r in s:
        print ' '.join(r)

    r = ''
    i = nr - 1
    j = nc - 1
    while i >=1 or j >=1:
        if s[i][j] == '\\':
            r = a[i-1] + r
            i -= 1
            j -= 1
        elif s[i][j] == '-':
            j -= 1
        else:
            i -= 1

    print 'lcs=', r



#lcs('aasdfuh;sadjfalskdjfc', 'acasdfhoaei;asdijfasduifha;sdifjf')

def qsort(dat):
    if dat == []: return []
    a = [e for e in dat[1:] if e < dat[0]]
    b = [e for e in dat[1:] if e >= dat[0]]
    print a, b
    return qsort(a) + dat[0:1] + qsort(b)

print qsort([3,2,1])
