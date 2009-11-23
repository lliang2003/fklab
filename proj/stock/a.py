#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Aug 26 11:17:06 HKT HKT 2009

import os, sys, time

dates = []

def fun(f):
    last = 0
    for line in open(f):
        date = line.split(",")[0]
        dates.append(date)
        po,ph,pl,pc = map(float, line.split(",")[1:5])
        if last > 0:
            r = pc*100/last - 100
            print len(dates), r, abs(r) 
        last = pc


for f in os.listdir("data"):
    fun("data/"+f)
    

