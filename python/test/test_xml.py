#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 14 09:12:53 CST 2008

import xml.dom.minidom as mxml

def AddToNode(node, data):
    d = mxml.Document()
    type = data.__class__.__name__
    if type == 'dict':
        for k, v in data.items():
            c = d.createElement(str(k))
            AddToNode(c, v)
            node.appendChild(c)
    else:
        node.appendChild(d.createTextNode(str(data)))

d = mxml.Document()
a={'name':'kevin', 'score':[('eng',99),('math',33),('cs',55)], 'room':1220}
AddToNode(d.appendChild(d.createElement('root')), a)
print d.toprettyxml()






