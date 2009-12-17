#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 20 21:54:45 CST 2009

def mainflow():
    n = input()
    for i in range(n):
        if i > 0: raw_input()
        m = input()
        alist = []
        for j in range(m):
            line = raw_input()
            alist.append(line)
        alist.sort()

        last = alist[0]
        count = 0

        for j in range(1, m):
            if alist[j] == last:
                count += 1
            else:
                print last, count
                last = alist[j]
                count = 1
        print last, count
        print

mainflow()



