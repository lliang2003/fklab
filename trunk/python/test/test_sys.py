#!/usr/bin/env python
print "Hello, world!"

import sys, os
import glob
print '***Test Sys Module***'
print "path =", sys.path
print "platform =", sys.platform
print "maxint =", sys.maxint
print "version =", sys.version

print '***Test OS Module***'
if (sys.platform == "win"):
    print os.getpid()
    print os.getcwd()
    print os.listdir("c:\\")

    print os.abort.__module__
    print os.path.dirname("d:\\")
    print os.path.split("d:\\test.txt")
    print os.path.splitext("test.txt")
    print [f for f in os.listdir("d:\\") if os.path.isfile(os.path.join('d:\\', f))]
    print (os.pardir, os.curdir,os.sep, os.pathsep, os.linesep)
    print os.listdir(r'c:/')
    print os.path.getsize(r'c:\boot.ini')
    print os.path.abspath('.')
    os.system('dir')
    print os.popen('dir ..').read()
    print os.environ
    os.environ['user'] = 'fk'
    print os.environ['user']

    print glob.glob(r"d:\*\*txt")


