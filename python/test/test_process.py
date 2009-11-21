#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 26 06:32:09 CST 2008

import sys, os, time

def test_fork():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    pid = os.fork()
    if pid == 0: 
        print 'child', os.getpid()
        for i in xrange(3, 0, -1):
            print 'child exits in %d seconds'%i
            time.sleep(1)
        print 'child exits.'
        sys.exit(12)
    else:
        print 'parent', os.getpid()
        for i in xrange(3, 0, -1):
            print 'parent exits in %d seconds'%i
            time.sleep(1)
        #print 'kill', pid
        #os.kill(pid, signal.SIGKILL)
        #print os.wait()
        print 'parent exits.'
        sys.exit(0)
        
def test_spawn():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    args = ['a0', 'a1', 'a2']
    print os.spawnlp(os.P_NOWAIT, 'echo', 'echo', 'a', 'b')
    while True: pass

def test_exec():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    args = ['a0', 'a1', 'a2']
    #os.execl('/home/kevin/test', 'joke', 'hello')
    #os.execv('/home/kevin/test', args)
    #os.execv('/home/kevin/py.py', args)
    os.execlp('echo', 'echo', 'a', 'b')

sys.stdout=sys.stderr
#test_exec()
#test_spawn()
test_fork()
