#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 20 05:18:35 PM CST 2009

def Z(num):
    r = 0
    while num > 0:
        num /= 5
        r += num 
    print r

def mainflow():
    n  = input()
    for i in range(n):
        num = input()
        Z(num)

mainflow()

