#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 21 05:19:15 CST 2011

import optparse, sys
parser = optparse.OptionParser(usage="%prog [options] [args]")
parser.add_option('-v', '--verbose', action='store_true', dest='verbose', default=False, help='make lots of noise')
options, args = parser.parse_args()
infile = len(args)>0 and args[0] or "../data/test.xml"

######### Test xml.sax ################

from xml.sax.handler import ContentHandler
from xml.sax import make_parser, SAXException
class SAXHandler(ContentHandler):
    def __init__(self, name):
        print "name =",name
        self.display = False

    def startElement(self, name, attrs):
        print "start tag = ", name, attrs
        if name == "tag1":
            self.display = True

    def endElement(self, name):
        print "end tag = ", name
        if name == "tag1":
            self.display = False 

    def characters(self, chars):
        if self.display:
            print "chars = '%s'"%chars


def test_sax(inputfile):
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    h = SAXHandler("SHandler")
    parser = make_parser()
    parser.setContentHandler(h)
    try:
        parser.parse(open(inputfile))
    except SAXException, e:
        print e

test_sax(infile)
test_sax("../data/error.xml")


######### Test xml.dom ################

from xml.dom import minidom
import xml.dom.minidom as mxml

def test_minidom_read():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    try:
        doc = minidom.parse(infile)
        tags = doc.getElementsByTagName("tag1")
        for tag in tags:
            print tag.toxml()
    except Exception, e:
        print e

test_minidom_read()


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

def test_minidom_gen():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    d = mxml.Document()
    a={'name':'kevin', 'score':[('eng',99),('math',33),('cs',55)], 'room':1220}
    AddToNode(d.appendChild(d.createElement('root')), a)
    print d.toprettyxml()

test_minidom_gen()









