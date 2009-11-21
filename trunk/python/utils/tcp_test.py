#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 31 04:43:12 PM CST 2008

import os, time
import sys

def test_local():
    os.system('pkill -f "python.*tcp.py"')
    for i in range(6, 25, 2):
        scmd = './tcp.py -s -u %d -p 12345 &' % 2**i
        print scmd
        os.system(scmd)

        time.sleep(1)

        for j in range(6, 25, 2):
            ccmd = './tcp.py -c -b %d -n %d -t 1 -a 127.0.0.1 -p 12345' % (2**j, 2**(24-j))
            #print ccmd
            os.system(ccmd)

        os.system('pkill -f "python.*tcp.py"')

def test():
    for j in range(4, 18):
        ccmd = './tcp.py -b %d -n %d -t 1 -a %s %s' % (2**j, 2**(24-j), sys.argv[1], sys.argv[2])
        #print ccmd
        os.system(ccmd)

test_local()


