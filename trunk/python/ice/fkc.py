#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 30 06:45:14 CST 2008

import sys, Ice, traceback, time
sys.path.append('ice')
import fk

def init_ic(str):
    try:
        ic = Ice.initialize(sys.argv)
        base = ic.stringToProxy(str)
        obj = fk.WorkerPrx.checkedCast(base)
        if not obj:
            raise RuntimeError('Invalid proxy')

        return obj, ic
    except:
        traceback.print_exc()

def destroy_ic(ic):
    try:
        if ic: ic.destroy()
    except:
        traceback.print_exc()

worker,ic = init_ic("Worker:default -p 12345")
print worker.work('cmd1', 'data1')
print worker.work('echo', 'fuck you')
print worker.work('cmd1', 'data1')

start = time.time()
for i in range(64):
    print worker.work('transfer', ' '*1024*512)
print 'spent=%f'%(time.time()-start)

destroy_ic(ic)
