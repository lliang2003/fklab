#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 26 06:33:24 CST 2008

import sys, struct

def test_struct():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    fmt = 'hhl'
    s=struct.pack(fmt,1,2,3)
    print struct.unpack('il', s)
    buf = 'abcdef'
    print struct.calcsize(fmt)
    print struct.calcsize('xcbhilqfd')

def test_stringIO():
    import StringIO
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    output = StringIO.StringIO('joke\n')
    print output.getvalue()
    print output.tell()
    output.seek(0, 2)
    print output.tell()
    output.write('hello world.')
    print output.getvalue()
    output.truncate(8)
    print output.getvalue()
    print output.tell()
    output.seek(0)
    print output.readline()
    output.close()
test_stringIO()

def goodbye(name):
    print 'Goodbye,', name

def test_atexit():
    import atexit
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    atexit.register(goodbye, 'kevin')
    atexit.register(goodbye, 'kai')
    sys.exit(0)
#test_atexit()

from ctypes import *
from ctypes.util import *
def test_ctypes():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    cdll.LoadLibrary('libc.so.6')
    libc = CDLL('libc.so.6')
    print libc.time(0)
    a = c_short(5)
    print sizeof(a)
    print sizeof(c_int64)
    libc.printf("%p\n", addressof(a))
    buf = c_buffer("joke now")
    libc.printf("%d %p %s\n", sizeof(buf), buf, buf)
    buf = create_string_buffer('joke joke')
    libc.printf("%d %p %s\n", sizeof(buf), buf, buf)
    print repr(buf.raw)
    print find_library('c')
    print find_library('db')
#test_ctypes()

def test_getpass():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import getpass
    print 'User:', getpass.getuser()
    p = getpass.getpass()
    print p
#test_getpass()

def test_optparse():
    import optparse
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    parser = optparse.OptionParser(usage='%prog [options][args]', 
            description='Fuck the world', version='%prog 1.0')
    parser.add_option('-f', '--file', dest='filename',
            help='Configuration file', metavar='FILE')
    parser.add_option('-g')
    parser.add_option('-n', type='int', dest='num', default='0', help='default = %default')
    parser.add_option('-v', action='store_true', dest='verbose', default=False, help='make lots of noise [default]')

    parser.add_option('-t', action='store', choices=['a', 'b', 'c'])
    g = optparse.OptionGroup(parser, 'Extra Options', 'Run on your own risk.')
    g.add_option('-k', help='format yourself')
    parser.add_option_group(g)
    options, args = parser.parse_args()
    print options
test_optparse()

def test_getopt():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import getopt
    opts, args = getopt.getopt(sys.argv[1:], 'ab:', ['c=','bb=','d'])
    print 'opts:', opts
    print 'args:', args
#test_getopt()

def test_tempfile():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import tempfile
    print tempfile.gettempprefix()
    print tempfile.gettempdir()
    print tempfile.tempdir
    print tempfile.mkdtemp('fk','py')
    print tempfile.mkstemp('fk','py')
#test_tempfile()

def test_robotparser():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import robotparser
    rp = robotparser.RobotFileParser()
    rp.set_url("http://www.newsmth.net/robots.txt")
    rp.read()
    print rp.can_fetch("*", "http://www.newsmth.net/")
#test_robotparser()


confstr = '''
[lab]
room=1220
floor=2
room:1235
staff= fan, kai, kevin
[lab]
fake:1
'''
def test_configparser():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    import ConfigParser, StringIO
    try:
        conf = ConfigParser.ConfigParser()
        conf.readfp(StringIO.StringIO(confstr))
        print conf.has_section('lab')
        print conf.options('lab')
        print conf.items('lab')
        print conf.getint('lab', 'room')
        conf.set('lab', 'room', '1117')
        print conf.getint('lab', 'room')
        confsio = StringIO.StringIO()
        conf.write(confsio)
        print confsio.getvalue()
    except Exception, e:
        print e
test_configparser()

sys.stdout=sys.stderr
