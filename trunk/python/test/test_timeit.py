#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 28 18:29:49 CST 2008

import timeit, sys

def f0():
    pass

def f1(a):
    pass

def f2(*a):
    pass

def time_random():
    print timeit.Timer('random.random()', 'import random').timeit()
    print timeit.Timer('random.uniform(1,10)', 'import random').timeit()
    print timeit.Timer('random.randint(1,100)', 'import random').timeit()
    print timeit.Timer('random.randint(1,100000000)', 'import random').timeit()
    print timeit.Timer('random.randrange(0,101,2)', 'import random').timeit()
    print timeit.Timer('random.choice("abcdefg")', 'import random').timeit()
    print timeit.Timer('random.sample("abcdefg", 2)', 'import random').timeit()

def time_misc():
    print timeit.Timer('pass').timeit()
    print timeit.Timer('a=1').timeit()
    print timeit.Timer('b=None').timeit()
    print timeit.Timer('s="abcedfg"').timeit()
    print timeit.Timer('a=(1,2,3,4)').timeit()
    print timeit.Timer('a=[1,2,3,4]').timeit()
    print timeit.Timer('a={1:2,3:4}').timeit()
    print timeit.Timer('a=[8]*100').timeit()

    print timeit.Timer('a=1+2').timeit()
    print timeit.Timer('1+2').timeit()
    print timeit.Timer('1+2+3').timeit()
    print timeit.Timer('.1+.2+.3').timeit()
    print timeit.Timer('222*333').timeit()
    print timeit.Timer('22**33').timeit()
    print timeit.Timer('123450/333').timeit()
    print timeit.Timer('123450.0/333').timeit()
    print timeit.Timer('123450.0/333').timeit()

    print timeit.Timer('"*"*9').timeit()
    print timeit.Timer('str(123)').timeit()
    print timeit.Timer('int("123")').timeit()
    print timeit.Timer('float("123.45")').timeit()

def time_io():
    #print timeit.Timer('print 0,').timeit(10000)
    #print timeit.Timer('print 0').timeit(10000)
    if not os.path.exists('tmp'): os.mknod('tmp')
    print timeit.Timer('open("tmp")').timeit(10000)
    print timeit.Timer('open("tmp", "a")').timeit(10000)
    os.remove('tmp')
    print timeit.Timer('os.mknod("tmp")\nos.remove("tmp")', 'import os').timeit(10000)
    print timeit.Timer('os.mknod("tmp")\nos.rename("tmp", "tmp2")\nos.remove("tmp2")', 'import os').timeit(10000)
    print timeit.Timer('open("tmp","a").write(" "*10)', 'import os').timeit(10000)
    print timeit.Timer('open("tmp","a").write(" "*1000)', 'import os').timeit(10000)
    print timeit.Timer('open("tmp","w").write(" "*1000)', 'import os').timeit(10000)
    print timeit.Timer('open("tmp","a").write(" "*10000)', 'import os').timeit(10000)
    print timeit.Timer('open("tmp","w").write(" "*10000)', 'import os').timeit(10000)
    print timeit.Timer('open("tmp","a").write(" ")', 'import os').timeit(10000)
    print timeit.Timer('f=open("tmp")\nf.seek(12345)', 'import os').timeit(10000)
    os.remove('tmp')


def time_func():
    timeit.f0=f0
    timeit.f1=f1
    timeit.f2=f2
    timeit.arg=99
    print timeit.Timer('f0()').timeit()
    print timeit.Timer('f1(arg)').timeit()
    print timeit.Timer('f2(arg,arg,arg)').timeit()
    print timeit.Timer('f2([arg]*100)').timeit()

sys.stdout=sys.stderr
time_func()
#time_io()
#time_misc()
#time_random()


