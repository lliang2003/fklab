#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Mar 30 11:23:36 AM CST 2009

import xml.sax
from xml.dom import minidom

p = xml.sax.make_parser()
p.parse("pos.xml")

d = minidom.parse("pos.xml")
lexelts = d.getElementsByTagName("lexelt")
for lexelt in lexelts:
    instances = d.getElementsByTagName("instance")
    for instance in instances:
        print instance.toxml()
        words = d.getElementsByTagName("word")
        for word in words:
            print word.attributes['id'].value
            print word.attributes['pos'].value
            print word.getElementsByTagName("token")[0].firstChild.data

