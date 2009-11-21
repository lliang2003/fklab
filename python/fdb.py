#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 16 04:02:42 PM CST 2009

import os, time, cPickle

class fdb:
    def __init__(self, path):
        if os.path.exists(path):
            f = open(path, 'rb+')
            pos = int(f.read(8), 16)
            f.seek(pos)
            idxstr = f.read()
            self.idx = cPickle.loads(idxstr)
            #print self.idx
            self.pos =  pos
            self.dat = f
        else:
            self.idx = {}
            self.pos = 8
            os.mknod(path)
            self.dat = open(path, 'rb+')
        self.closed = False

    def __del__(self):
        pass
        #if not self.closed:
            #self.close()

    def keys(self):
        return self.idx.keys()

    def put(self, key, value):
        #print self.pos
        self.dat.seek(self.pos)
        self.idx[key] = (self.dat.tell(), len(value))
        self.dat.write(value)
        self.pos = self.dat.tell()

    def get(self, key):
        if key in self.idx:
            pos, len = self.idx[key]
            self.dat.seek(pos)
            return self.dat.read(len)

    def close(self):
        self.dat.seek(0)
        self.dat.write('%08x'%self.pos)
        idxstr = cPickle.dumps(self.idx)
        self.dat.seek(self.pos)
        #print self.pos
        self.dat.write(idxstr)
        #self.dat.truncate()
        self.dat.close()
        self.closed = True

if __name__ == "__main__":
    import sys
    #os.system('rm fdb_tmp')
    db = fdb('fdb_tmp')
    for i in range(10):
        db.put(i, str(i))
    db.close()
    db = fdb('fdb_tmp')
    for k in db.keys():
        print k, db.get(k)

