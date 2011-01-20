#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Oct 01 17:50:09 CST 2010

cnta = 4
cntb = 5

edges = [[0, 0], [0,1], [1,0]]

lines = [0, 0]



def find_edge(point, ex):
    for edge in edges:
        if edge[0] == point and edge[1] not in includedb:
            return edge[1]
    return None
        
def find_edge2(point, ex):
    for edge in edges:
        if edge[1] == point and edge[0] not in includeda:
            return edge[0]
    return None
        
def find_lines():
    for edge in edges:
        if edge[0] not in includeda and edge[1] not in includedb:
            includeda.append(edge[0])
            includedb.append(edge[1])
            return edge
    return None


def find(lines):
    if lines[0] != -1:
        a = find_edge(lines[0], lines[1])
        b = find_edge2(lines[-1], lines[-2])
        if a == None or b == None: return None
        includedb.append(a)
        includeda.append(b)
        lines = [-1, a]+lines+[b]
    else:
        a = find_edge2(lines[1], lines[2])
        b = find_edge(lines[-1], lines[-2])
        if a == None or b == None: return None
        includeda.append(b)
        includedb.append(a)
        lines = [a]+lines[1:]+[b]
    return lines

includeda = []
includedb = []
while True:
    lines = find_lines()
    if lines == None: break
    print lines, includeda, includedb
    while lines != None:
        lines = find(lines)
        print lines, includeda, includedb
        


