#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 08 19:31:50 CST 2009

import sys, os

def test_dbhash():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import dbhash, whichdb
    fdb='dbhash.tmp'
    db = dbhash.open(fdb, 'c')
    for i in range(9): db[str(i)] = str(i*i)
    for i in range(9):
        print i, db[str(i)]
    print db.keys()
    print db.first()
    print db.next()
    print db.next()
    print db.next()
    print db.last()
    db.set_location('2')
    print db.previous()
    db.close()
    print 'whichdb:', whichdb.whichdb(fdb)
    os.remove(fdb)
#test_dbhash()

def test_bsddb_hash():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import bsddb, whichdb
    fdb='bsddb_hash.tmp'
    db = bsddb.hashopen(fdb, 'c')
    for i in range(9): db[str(i)] = str(i*i)
    for i in range(9):
        print i, db[str(i)]
    print db.keys()
    print db.first()
    print db.next()
    print db.next()
    print db.next()
    print db.last()
    db.set_location('2')
    print db.previous()
    #db.set_location('22')
    db.close()
    print 'whichdb:', whichdb.whichdb(fdb)
    os.remove(fdb)
#test_bsddb_hash()

def test_bsddb_bt():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import bsddb, whichdb
    fdb='bsddb_bt.tmp'
    db = bsddb.btopen(fdb, 'c')
    for i in range(9): db[str(i)] = str(i*i)
    for i in range(9):
        print i, db[str(i)]
    print db.keys()
    print db.first()
    print db.next()
    print db.next()
    print db.next()
    print db.last()
    print db.set_location('2')
    print db.previous()
    print db.set_location('22')
    print db.next()
    db.close()
    print 'whichdb:', whichdb.whichdb(fdb)
    os.remove(fdb)
#test_bsddb_bt()

def test_bsddb_rn():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import bsddb, whichdb
    fdb='bsddb_rn.tmp'
    db = bsddb.rnopen(fdb, 'c')
    for i in range(1,9): db[i] = str(i*i)
    #for i in range(9): print i, db[str(i)]
    print db.keys()
    print db.first()
    print db.next()
    print db.next()
    print db.next()
    print db.last()
    print db.set_location(2)
    print db.previous()
    #print db.set_location(22)
    print db.next()
    db.close()
    print 'whichdb:', whichdb.whichdb(fdb)
    os.remove(fdb)
#test_bsddb_rn()

def test_gdbm():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import gdbm, whichdb
    fdb='gdbm.tmp'
    db = gdbm.open(fdb, 'c')
    for i in range(9): db[str(i)] = str(i*i)
    db.close()
    db = gdbm.open(fdb)
    k = db.firstkey()
    while k != None:
        print k
        k = db.nextkey(k)
    db.close()
    print 'whichdb:', whichdb.whichdb(fdb)
    os.remove(fdb)
#test_gdbm()

def test_anydbm():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import anydbm, whichdb
    fdb='anydbm.tmp'
    db = anydbm.open(fdb, 'c')
    for i in range(9): db[str(i)] = str(i*i)
    #print db.keys(), db.values()
    db.close()
    db = anydbm.open(fdb, 'w')
    del db['3']
    for k, v in db.iteritems():
        print k, v
    #k = db.firstkey()
    #while k != None:
        #print k
        #k = db.nextkey(k)
    db.close()
    print 'whichdb:', whichdb.whichdb(fdb)
    os.remove(fdb)
#test_anydbm()

def test_sqlite():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import sqlite, random
    fdb = 'sqlite.tmp'
    conn = sqlite.connect(fdb)
    c = conn.cursor()
    c.execute('''create table stocks (id integer, price real)''')
    for i in range(10):
        c.execute('insert into stocks values (%d, %f)', (i,random.random()))
    conn.commit()
    c.execute('select * from stocks order by price')
    for row in c:
        print row
    c.close()
    os.remove(fdb)
#test_sqlite()
    
def tgdbm():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import gdbm, random
    fdb = 'tgdbm.tmp'
    db = gdbm.open(fdb, 'c')
    len = 0
    for i in range(999):
        n = random.randint(999, 99999)
        db[str(i)] = 'a' * n
        len += n
    db.close()
    #os.remove(fdb)
    #db = gdbm.open(fdb, 'w')
    #db.reorgnize()
    #db.close()
    print len, os.path.getsize(fdb)
tgdbm()
