# -*- coding: utf-8 -*-
#!/usr/bin/env python
#print dir("")
import re, codecs
print "d".rjust(5)
print 'fuck'.replace("u", "v").upper()
print 'FUCK'.lower()
print 'fuck'.find('f')
print 'fuck'.find('d')
print '-'.join(['a','b','c'])
print 'a-b-c'.split('-')
#print 'aaabbbbcc'.partition('b')
print ' abc '.strip()
print '123'.zfill(5)

import re 
s = '100 NORTH BROAD ROAD'
print re.sub('ROAD$', 'RD.', s)
print re.search('NORT', s).groups()
print re.sub('<[^>]*>', '()', 'jj<abc>adf<j>dd')

txt = '北京大学'
print [txt], len(txt), txt
pat = '北[^学]+'
print [re.search(pat, txt).group(0)]

s = unicode('utf-8')
print [s], len(s)
s = 'utf-8'.encode('utf-16')
print [s], len(s)


txt = u'北京大学'
print [txt], len(txt)
pat = u'北[^学]+'
print [re.search(pat, txt).group(0)]


