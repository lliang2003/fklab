#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 04 10:41:19 AM CST 2009

import random


def NF(objs):
    cnt = 1
    blank = 1
    for w in objs:
        if w < blank:
            blank -= w
        else:
            cnt += 1
            blank = 1 - w

    print cnt

def FF(objs):
    boxs = []
    for w in objs:
        done = False
        for i in range(len(boxs)):
            if w < boxs[i]:
                boxs[i] -= w
                done = True
                break
        if not done:
            boxs.append(1-w)
    print len(boxs)


objs = [random.random() for i in range(1000)]
NF(objs)
FF(objs)
FF(sorted(objs))
FF(sorted(objs, reverse=True))




