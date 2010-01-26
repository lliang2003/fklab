#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 11 02:44:54 AM EST 2009

import urllib, sys

def getcnt(thread_url):
    print >>sys.stderr, thread_url
    npages, nposts = 0, 0
    for line in urllib.urlopen(thread_url):
        if line.startswith("var o"):
            npages = line.split(",")[4]
        elif line.startswith("o.o"):
            nposts = len(line.split("["))-2
        #else:
            #print line
    return npages, nposts

def proc_board(board):
    board_url = "http://www.newsmth.net/bbsdoc.php?board=%s&ftype=6"%board
    for line in urllib.urlopen(board_url).read().split("\n"):
        if line.startswith("c.o"):
            parts = line.split(",")
            gid = parts[1]
            thread_url = "http://www.newsmth.net/bbstcon.php?board=%s&gid=%s"%(board, gid)
            npages, nposts = getcnt(thread_url)
            parts[5] = parts[5][0]+"[%s,%s]"%(npages,nposts)+parts[5][1:]
            print ",".join(parts)
        else:
            print line

proc_board("EconForum")

