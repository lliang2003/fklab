#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 28 03:22:43 PM CST 2008

import ftplib
ftp = ftplib.FTP('127.0.0.1')
ftp.login()
#ftp.login('ftp')
#ftp.login('fankai')
print ftp.retrlines('LIST')
ftp.retrbinary('RETR CHANGES.txt', open('txt', 'wb').write)
#print ftp.getwelcome()
#print ftp.retrlines('RETR CHANGES.txt')
#print ftp.sendcmd('LIST')
#ftp.storlines('STOR incoming/txt2', open('txt'))
#ftp.storbinary('STOR incoming/txt3', open('txt'))
print ftp.retrlines('LIST incoming')

print ftp.nlst()
print ftp.nlst('incoming')
print ftp.dir()
print 
ftp.cwd('incoming')
print ftp.dir()
print ftp.pwd()
ftp.cwd('..')
print ftp.size('CHANGES.txt')
print ftp.dir('CHANGES.txt')
#print ftp.size('incoming')


ftp.quit()

