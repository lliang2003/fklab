#!/usr/bin/env python

import sys, getopt, random
from socket import *  


PORT = 12345

def udp_server():
    s = socket(AF_INET, SOCK_DGRAM)
    s.bind(('', PORT))
    while True:
        print 'Received "%s" from %s.' % s.recvfrom(1024)
    s.close()

def udp_client(str):
    s = socket(AF_INET, SOCK_DGRAM)
    HOST = 'localhost'
    ADDR = (HOST, PORT)
    s.sendto(str, ADDR)
    while True:
        line = raw_input()
        if not line: break
        s.sendto(line, ADDR)
    print 'Sent ' + str
    s.close()

def tcp_server():
    myHost = ''                    
    sockobj = socket(AF_INET, SOCK_STREAM)  
    sockobj.bind((myHost, PORT))          
    sockobj.listen(5)                    

    while True:  
        connection, address = sockobj.accept()  
        print 'Server connected by', address   
        while True:
            data = connection.recv(1024)  
            if not data: break                  
            connection.send('Echo=>' + data)
        connection.close() 

def tcp_client():
    serverHost = 'localhost'          # server name, or: 'starship.python.net'
    message = ['Hello network world\n']        # default text to send to server
    sockobj = socket(AF_INET, SOCK_STREAM)  
    sockobj.connect((serverHost, PORT))
    print sockobj.getsockname()

    while True:
        line = raw_input()
        print sockobj.send(line)                 
        data = sockobj.recv(1024)         
        print 'Client received:', `data`
    sockobj.close()                             # close 

try:
    opts, args = getopt.getopt(sys.argv[1:], 'csud')
except getopt.GetoptError, e:
    print e
    sys.exit(1)
if len(opts) > 0:
    opt = opts[0][0]
    if opt == '-c': 
        print 'Start tcp client ...'
        tcp_client()
    elif opt == '-s':
        print 'Start tcp server ...'
        tcp_server()
    elif opt == '-u':
        print 'Start udp server ...'
        udp_server()
else:
    print 'Start udp client ...'
    udp_client(' '.join(sys.argv))
