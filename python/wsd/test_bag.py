#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Mar 30 19:29:23 CST 2009

from xml.dom import minidom
import cPickle

data = cPickle.load(open('wsd.dat'))
answer = open('test.key')

right_cnt = 0
wrong_cnt = 0
print "begin test"
#testdoc = minidom.parse("tpos.xml")
testdoc = minidom.parse("test_pos.xml")
lexelts = testdoc.getElementsByTagName("lexelt")
for lexelt in lexelts:
    item = lexelt.attributes['item'].value
    print item.encode('utf-8'), data[item].keys()
    instances = lexelt.getElementsByTagName("instance")
    for instance in instances:
        instance_id = instance.attributes["id"].value
        bestscore = 0
        for sense in data[item].keys():
            score = 0
            words = instance.getElementsByTagName("word")
            for word in words:
                token = word.getElementsByTagName("token")[0].firstChild.data
                if token in data[item][sense]["wordbag"]:
                    score += 1
            if score >= bestscore: 
                bestscore = score
                bestsense = sense
        rightsense = answer.readline().split()[2]
        print item.encode("utf-8"), instance_id.encode("utf-8"), bestsense
        if bestsense == rightsense:
            right_cnt += 1
        else:
            wrong_cnt += 1

print right_cnt, wrong_cnt
     

