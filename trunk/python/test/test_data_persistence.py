#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 12 19:55:18 CST 2008

# Test data persistance
import shelve,pickle, os
import zlib, gzip, zipfile, tarfile
import ConfigParser, time
import csv, sys, random, timeit

file1 = 'test_pickle.dat'
file2 = 'test_shelve.dat'
file3 = 'test_dbm.dat'

data = {}
data['a'] = 'joke'
data['b'] = range(1,7)

def test_pickle():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    str = pickle.dumps(data)
    str0 = pickle.dumps(data, 0)
    str1 = pickle.dumps(data, 1)
    str2 = pickle.dumps(data, 2)
    strh = pickle.dumps(data, pickle.HIGHEST_PROTOCOL)
    print len(str), len(str0), len(str1), len(str2), len(strh)
    print 'loaded data from string:', pickle.loads(str)
    pickle.dump(data, open(file1, 'wb'))
    print 'loaded data from file:', pickle.load(open(file1, 'rb'))

def test_shelve():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    d = shelve.open(file2, writeback=True)
    for k in data.keys():
        d[k] = data[k]
    print d
    d['b'].append(0)
    print d
    d.close()
    print 'loaded data:', shelve.open(file2)

def ts_aux(count):
    print 'insert %d items' % count
    timeit.ds = shelve.open(timeit.f)
    #print timeit.Timer('k=str(random.random())\nks.append(k)\nds[k]=" "*1000\nds.sync()', 'import random').timeit(count)
    print timeit.Timer('k=str(random.random())\nks.append(k)\nds[k]=" "*1000', 'import random').timeit(count)
    print timeit.Timer('ds.sync()').timeit(1)
    print timeit.Timer('len(ds)').timeit(10000)
    print timeit.Timer('str(random.random()) in ds', 'import random').timeit(10000)
    print timeit.Timer('ks[random.randint(0,len(ks)-1)] in ds', 'import random').timeit(10000)
    timeit.ds = shelve.open(timeit.f)
    print timeit.Timer('ds[ks[random.randint(0,len(ks)-1)]]', 'import random').timeit(10000)
    timeit.ds.close()
    print 'db count=%d size=%d' % (len(timeit.ks), os.path.getsize(timeit.f))

def time_shelve():
    timeit.f = 'time_shelve.dat'
    if os.path.exists(timeit.f): os.remove(timeit.f)
    timeit.ks = []
    print timeit.Timer('str(random.random())', 'import random').timeit(10000)
    print timeit.Timer('ks.append(str(random.random()))', 'import random').timeit(10000)
    print timeit.Timer('len(ks)', 'import random').timeit(10000)
    print timeit.Timer('random.randint(0,len(ks))', 'import random').timeit(10000)
    print timeit.Timer('ks[random.randint(0,len(ks)-1)]', 'import random').timeit(10000)
    print timeit.Timer('d=shelve.open(f)\nd.close()', 'import shelve').timeit(100)
    timeit.ks=[]
    ts_aux(10000)
    ts_aux(10000)
    ts_aux(10000)
    ts_aux(10000)
    #os.remove(timeit.f)

sys.stdout=sys.stderr
time_shelve()

def test_zlib():
    str = ' '.join(map(chr, range(65, 123)))*88
    print 'len:', len(str)
    for i in range(0, 10):
        cs = zlib.compress(str, i)
    print 'compress level', i, len(cs)

def test_gzip():
    print '\nTesting gzip ...'
    gzip.open('test.gz', 'wb').write(str)
    assert str == gzip.open('test.gz').read()

def test_tarfile():
    print '\nTesting tarfile ...'
    tf = tarfile.open('test.tar', 'w')
    tf.add(file1, 'tmp')
    tf.add(file2)
    print tf.getnames()
    #tf.extract('d/dattest.txt')
    tf.close()
    print 'test.gz is tarfile? ', tarfile.is_tarfile('test.gz')
    print 'test.tar is tarfile? ', tarfile.is_tarfile('test.tar')

    print '\nTesting zipfile ...'
    zf = zipfile.ZipFile('test.zip', 'w', zipfile.ZIP_DEFLATED)
    zf.write('test.tar')
    #print zf.namelist()
    print zf.getinfo('test.tar').compress_size, zf.getinfo('test.tar').file_size
    zf.close()

def test_csv():
    w = csv.writer(open("test.csv", "wb"))
    w.writerow([1,'b',2,'c',3])
    w.writerow([2,'b',2,'c',3])
    del w
    r = csv.reader(open("test.csv", "rb"))
    try: 
        while True: print r.next()
    except:pass
    del r
    print '...'
    for row in csv.reader(open("test.csv", "rb")): print row

def test_configparser():
    try:
        conf = ConfigParser.ConfigParser()
        conf.read('data/test.ini')
        print conf.has_section('lab')
        print conf.options('lab')
        for sec in conf.sections():
            print sec, conf.items(sec)
        print conf.getint('lab', 'room')
        conf.set('lab', 'room', '1117')
        conf.write(open('data/test2.ini', 'wb'))
    except Exception, e:
        print e

def clean():
    os.remove(file1)
    os.remove(file2)
    os.remove(file3)
    os.remove('test.gz')
    os.remove('test.tar')
    os.remove('test.zip')
    os.remove('test.csv')


