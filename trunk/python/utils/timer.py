#!/usr/bin/env python
import time

class Timer:
    "A Timer like boost::timer"
    cnt = 0
    def __init__(self):self.restart();Timer.cnt += 1
    def __repr__(self):return self.t
    def __str__(self):return str(self.t)   
    def __private(self):print "private func %d" % self.cnt
    def public(self):self.__private()
    @staticmethod
    def static():print 'static method %d' % Timer.cnt
    #@classmethod
    #def cm():print 'class method %d' % Timer.cnt
    def restart(self):
         self.t = time.clock()     
    def elapsed(self):
        return time.clock() - self.t

if __name__ == '__main__':    
    print Timer.cnt
    t = Timer();
    print Timer.cnt
    print t.cnt
    print t.__doc__
    print t.elapsed()
    time.sleep(.1)
    print t.elapsed()
    t.restart()
    time.sleep(.1)
    print t.elapsed()
    print t
    t.public()
    print dir(t)
    print t.t
    t.static()
    Timer.static()
    #Timer.cm()
    tt = Timer()
    print Timer.cnt, tt.cnt
