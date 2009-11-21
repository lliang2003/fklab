#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 29 07:23:04 CST 2008

import sys, threading, random, time, optparse, os
from socket import *

class Receiver(threading.Thread):
    def __init__(self, connection):
        threading.Thread.__init__(self)
        self.connection = connection
    def run(self):
        start = time.time()
        mbytes = 0
        if opts.file:
            fname = '%s_%f.tmp' % (opts.file, start)
            f = open(fname, 'wb')
        while True:
            data = self.connection.recv(Receiver.buffer_size)
            if not data: break
            if opts.file: f.write(data)
            Receiver.mbytes += len(data)/1024.0/1024.0
            mbytes += len(data)/1024.0/1024.0
        sender = self.connection.getpeername()
        self.connection.close()
        if opts.file:
            f.close()
            #os.remove(fname)
        interval = time.time() - start
        if opts.verbose:
            print 'sender=%s bytes=%.2fMB speed=%.2fMB/s'\
                    %(sender, mbytes, mbytes/interval)

class Sender(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
    def run(self):
        start = time.time()
        sock = socket(AF_INET, SOCK_STREAM)
        sock.settimeout(3)
        sock.connect((opts.host, opts.port)) 
        self.mbytes = 0;
        if opts.file:
            f = open(opts.file, 'rb')
            while True:
                data = f.read(1024*1024)
                if not data: break
                sock.sendall(data)
                self.mbytes += len(data)/1024.0/1024.0
        else:
            while self.mbytes < Sender.size:
                bsize = random.randint(Sender.block_size/2, Sender.block_size)
                sock.sendall(' ' * bsize)
                self.mbytes += bsize/1024.0/1024.0
        sock.close()
        interval = time.time() - start
        Sender.sema.release()
        if opts.verbose:
            print 'sent=%.2fMB speed=%.2fMB/s'%(self.mbytes, self.mbytes/interval)

class SpeedCalculator(threading.Thread):
    def run(self):
        time_start = time.time()
        while True:
            time_last = time.time()
            mbytes_last = Receiver.mbytes
            time.sleep(1)
            mbytes = Receiver.mbytes - mbytes_last
            interval = time.time() - time_last
            if opts.verbose and mbytes > 0:
                print 'time=%.2f peers=%d  bytes=%.2fMB  speed=%.2fMB/s'\
                        %(time.time() - time_start, threading.activeCount()-2, 
                                Receiver.mbytes, mbytes/interval)

def start_server():
    Receiver.mbytes = 0
    tsc = SpeedCalculator()
    tsc.setDaemon(True)
    tsc.start()
    ss = socket(AF_INET, SOCK_STREAM)
    ss.bind((opts.host, opts.port))
    ss.listen(5)
    while True:
        connection, address = ss.accept()
        ts = Receiver(connection)
        ts.start()

def start_client():
    start = time.time()
    threads = []
    for i in range(Sender.thread):
        Sender.sema.acquire()
        t = Sender()
        t.start()
        threads.append(t)
    mbytes = 0
    for i in range(Sender.thread):
        threads[i].join()
        mbytes += threads[i].mbytes
    span = time.time() - start
    print 'server=%s thread=%d block=%dB  bytes=%.2fMB  speed=%.2fMB/s'\
            %((opts.host, opts.port), Sender.thread, Sender.block_size, mbytes, mbytes/span)

parser = optparse.OptionParser(description='Test efficiency of tcp transmission.')
parser.add_option('-v', '--verbose', action='store_true', default=False,
        help='show more information when running')
parser.add_option('-s', '--server', action='store_true', dest='server', default=False,
        help='start as server')
parser.add_option('-c', '--client', action='store_true', dest='client', default=False,
        help='start as client')
parser.add_option('-a', '--host', default='127.0.0.1',
        help='server host')
parser.add_option('-p', '--port', type=int, default=22345,
        help='server port')
parser.add_option('-u', '--buffer', metavar='SIZE', default='1024*1024',
        help='size of buffer server use to receive data')
parser.add_option('-b', '--block', metavar='SIZE', default='1024*1024',
        help='max size of each transmission, the default value is 1MB')
parser.add_option('-n', '--size', default='8',
        help='data size in MB, the default value is 8MB')
parser.add_option('-f', '--file', default='',
        help='read/write data from/to FILE')
parser.add_option('-t', '--thread', metavar='NUM', type='int', default=1,
        help='number of threads client starts')
parser.add_option('-k', '--concurrent', metavar='NUM', type='int', default=1,
        help='max number of concurrent client threads')

opts, args = parser.parse_args()
if opts.server:
    Receiver.buffer_size = eval(opts.buffer)
    start_server()
elif opts.client:
    Sender.sema = threading.Semaphore(opts.concurrent)
    Sender.block_size = eval(opts.block)
    Sender.size = eval(opts.size)
    Sender.thread = opts.thread
    start_client()
else:
    parser.print_help()
