#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 12 11:02:05 CST 2008

import sys, profile

nMethods = 0;

def check(index, pos):
    for i in range(0, index):
        if pos[i] == pos[index] or abs(pos[i]-pos[index]) == abs(i-index):
            return False
    return True
        
def found(pos):
    global nMethods
    nMethods += 1
    print "Method", nMethods
    return
    for i in range(0, len(pos)):
        print ' -'*pos[i],'*', '- '*(len(pos)-pos[i]-1)

def queens_rec(index, pos):
    n = len(pos)
    if index == n: found(pos)
    else:
        for i in range(0, n):
            pos[index] = i
            if check(index, pos): 
                queens_rec(index + 1, pos)
    
def queens_back(n):
    index = 0
    pos = [-1]*n
    while index >= 0:
        pos[index] += 1
        if pos[index] == n:
            pos[index] = -1
            index -= 1
        elif check(index, pos):
            if index == n - 1: found(pos)
            else: index += 1
            
def queens(nQueens):
    #queens_rec(0, [0]*nQueens)
    queens_back(nQueens)
    
if len(sys.argv) > 1:
    queens(int(sys.argv[1]))
else:
    profile.run('queens(8)')
    #queens(8)
