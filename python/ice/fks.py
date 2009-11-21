#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 30 08:25:22 CST 2008

import sys, Ice, traceback, time
sys.path.append('ice')
import fk

class WorkerI(fk.Worker):
    def work(self, cmd, data, current=None):
        #sys.stdout=sys.stderr
        print 'cmd=%s time=%s'%(cmd, time.asctime())
        if cmd == 'echo':
            return data
        else:
            return str(len(data))


try:
    status = 0
    ic = Ice.initialize(sys.argv)
    adapter = ic.createObjectAdapterWithEndpoints("Adapter", "default -p 12345")
    obj = WorkerI()
    adapter.add(obj, Ice.stringToIdentity("Worker"))
    adapter.activate()
    ic.waitForShutdown()
except:
    traceback.print_exc()
    status = 1

try:
    if ic: ic.destroy()
except:
    traceback.print_exc()
    status = 1

sys.exit(status)


