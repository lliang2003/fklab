#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 23 04:14:34 PM CST 2008

import threading, time, sys, os, random

def disp():
    print 'Current:', threading.currentThread()
    print 'pid = ', os.getpid()
    print 'ppid = ', os.getppid()
    print 'uid = ', os.getuid()
    print 'gid = ', os.getgid()

def info():
    print 'Thread count:', threading.activeCount()
    print 'mt count=%d create=%d calc=%d sleep=%d' % ( mt.count, mt.create, mt.calc, mt.sleep)
    #print 'All:', threading.enumerate()

class mt(threading.Thread):
    def run(self):
        glock.acquire()
        t = threading.currentThread()
        print 'Thread %s begins.' % t.getName()
        mt.count += 1
        mt.create += 1
        info()
        glock.release()

        #t.setName('abc thread')
        if random.random() < 0.5:
            mt.calc += 1
            for i in range(3000000): random.random() ** 2
            mt.calc -= 1
        else:
            mt.sleep += 1
            time.sleep(3)
            mt.sleep -= 1

        glock.acquire()
        print 'Thread %s ends.' % t.getName()
        mt.count -= 1
        info()
        glock.release()
        sema.release()

sys.stdout = sys.stderr
glock = threading.Lock()
sema = threading.Semaphore(3)
threads = []
mt.count = 0
mt.create = 0
mt.calc = 0
mt.sleep = 0

#print 'thread stack size = %d', threading.stack_size()
#print 'thread stack size = %d', threading.stack_size()
for i in range(900):
    sema.acquire()
    t = mt()
    t.setDaemon(True)
    threads.append(t)
    t.start()

for t in threads:
    t.join()
