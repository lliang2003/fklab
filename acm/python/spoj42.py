#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 24 06:14:08 PM CST 2009

def foo(a, b):
    a = int(''.join(reversed(str(a))))
    b = int(''.join(reversed(str(b))))
    return int(''.join(reversed(str(a+b))))

def test():
    assert foo(24,1) == 34
    assert foo(305, 794) == 1

test()

n = input()
while n > 0:
    n -= 1
    a, b = raw_input().split()
    a, b = int(a), int(b)
    print foo(a, b)

