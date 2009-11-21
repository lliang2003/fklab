#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 26 10:34:01 CST 2008

import sys, os, time, getopt

usage='''Usage: repeat [INTERVAL] [COUNT] [COMMAND]
Repeat COMMAND every INTERVAL seconds up to COUNT times. '''

opts, args = getopt.getopt(sys.argv[1:], 'h')
if '-h' in dict(opts) or len(args) < 3:
    print usage
    sys.exit(0)

interval = int(args[0])
count = int(args[1])
cmd = ' '.join(args[2:])
try:
    while count != 0:
        os.system('date')
        os.system(cmd)
        count -= 1
        time.sleep(interval)
except KeyboardInterrupt:
    pass


