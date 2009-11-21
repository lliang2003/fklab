#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 25 06:09:25 PM CST 2009


def foo(s):
    m = 1
    ms = s
    for i in range(1, len(s)):
        s = s[1:] + s[0]
        #print 'ms=',ms, 's=', s
        if s < ms:
            ms = s
            m = i+1
            #print 'ms=', ms, 'm=', m
    return m

def test():
    assert foo('helloworld')==10
    assert foo('amandamanda')==11
    assert foo('aaabaaa')==5
    assert foo('dontcallmebfu')==6

test()
n = input()
while n > 0:
    n -= 1
    print foo(raw_input())


