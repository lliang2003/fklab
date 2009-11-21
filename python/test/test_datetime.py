#!/usr/bin/env python
# @author   FAN Kai (fankai@pku.edu.cn), Peking University  
# @date     Dec 25 08:49:51 CST 2008

import datetime

dd = datetime.timedelta(1,1,1)
de = datetime.timedelta(0,6,9)
print dd.min
print dd.max
print dd.resolution
print dd+de
print

td = datetime.date.today()
print td
print td.min
print td.max
print td.toordinal()
print td.weekday()
print td.isocalendar()
print td.ctime()
print

dt = datetime.datetime(td.year, td.month, td.day)
print dt
print dt.today()
print dt.now()
print dt.utcnow()
print dt.tzinfo
print

tz = datetime.tzinfo()
print tz
print

t = datetime.time()
print t
print t.isoformat()
print t.utcoffset()
print t.dst()


