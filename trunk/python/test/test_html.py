#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 13 18:59:18 CST 2008

import HTMLParser, htmllib, formatter, re

hstr = ' '.join(open('data/any.html').read().split())

class MyHTMLParser(HTMLParser.HTMLParser):
    def __init__(self):
        HTMLParser.HTMLParser.__init__(self)
        self.links = []
    def handle_starttag(self, tag, attrs):
        if tag != 'a': return
        for name, value in attrs:
            if name == 'href' and value not in self.links:
                self.links.append(value)

class LinkParser(htmllib.HTMLParser):
    def __init__(self, f):
        htmllib.HTMLParser.__init__(self, f)
        self.inside_a = False
        self.lastlink = ''
        self.links = []
        self.anthor_text = ''
    def feed(self, data):
        nd = data.split('>')
        for i in range(0, len(nd)-1):
            nd[i] = nd[i] + '>'
        for p in nd:
            if self.inside_a: self.anthor_text += p 
            htmllib.HTMLParser.feed(self, p)
    def start_a(self, attrs):
        htmllib.HTMLParser.start_a(self, attrs)
        self.inside_a = True
        for name, value in attrs:
            if name == 'href':
                self.lastlink = value
    def end_a(self):
        htmllib.HTMLParser.end_a(self)
        self.inside_a = False 
        atext = re.sub('<[^>]*>', '', self.anthor_text)
        atext = atext.replace('&gt;', '>').replace('&lt;', '<')
        if atext: self.links.append((atext, self.lastlink))
        self.anthor_text = ''

h = MyHTMLParser()
h.feed(hstr)
h.close()
print h.links

h = LinkParser(formatter.NullFormatter())
h.feed(hstr)
h.close()
print h.links

for r in re.finditer('<a[^>]*href="([^"]+)[^>]*>([^<]+)</a>', \
        re.sub('<[^a/][^>]*>|</[^>]*[^a]>', '', hstr)):
    print r.group(2), '->', r.group(1)

print re.search('<title>([^<]*)</title>', hstr).group(1)


