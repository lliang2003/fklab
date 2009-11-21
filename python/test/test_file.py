#!/usr/bin/env python

import time, os, timeit

try:
    f = open("data/test.txt", "rb")
except IOError:
    print "data/test.txt not exist."
else:    
    print f.name
    print f.readlines()
    print f.tell()
    f.seek(0, 0);
    print f.read(4)
    print f.read()
finally:
    f.close()
    
ts = time.clock()
f = open('data/test.dat', 'w')
for i in range(1000000):
    f.write('abcdefg')
print "Write %d bytes cost %f seconds" % (f.tell(), time.clock() - ts)
f.close()

ts = time.clock()
f = open('data/test.dat', 'rb')
s = f.read()
print "Read %d bytes cost %f seconds" % (f.tell(), time.clock() - ts)
f.close()


ts = time.clock()
f = open('data/test2.dat', 'w')
f.write(s)
print "Write %d bytes cost %f seconds" % (f.tell(), time.clock() - ts)
f.close()
