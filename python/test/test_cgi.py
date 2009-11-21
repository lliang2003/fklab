#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 03 07:14:53 CST 2009

import cgi, cgitb, Cookie, os

cgitb.enable()

if os.environ.has_key('HTTP_COOKIE'):
    cstr = os.environ['HTTP_COOKIE']
else:
    cstr = ''
cookie = Cookie.SimpleCookie(cstr)
if 'count' in cookie:
    cookie['count'] = int(cookie['count'].value) + 1
else:
    cookie['count'] = 0
cookie['count']['max-age'] = 60*60*24
print cookie

print '''Content-Type: text/html'''
print

print'''<html>
<title>Test CGI</title>
<body>
<h1>CGI in Python</h1>
fuck the world<br> '''

print 'found %d cookies' %len(cookie.keys())
print '<ul>'
for k in cookie.keys():
    m = cookie[k]
    print '<li>%s: %s' % (k, m.value)
print '</ul><p>'

form = cgi.FieldStorage()

#print form.keys()
cgi.print_environ()

print '</body></html>'

