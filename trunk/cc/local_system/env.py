#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 27 20:36:03 CST 2008

import getopt, sys, os

opts, args = getopt.getopt(sys.argv[1:], '')

usage = 'Usage: %s [CMD]'

if len(sys.argv) < 2:
    print usage
    sys.exit(0)

if sys.argv[1] == 'clear':
    print 'Clear logs.'
    cmd = 'find -name "*.log" |xargs rm'
    print cmd
    os.system(cmd)

