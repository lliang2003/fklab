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


test_news_xml="""
<NEWS>
<DOC>
<DOCNO> NYT19990101.0001 </DOCNO>
<DOCTYPE> NEWS STORY </DOCTYPE>
<DATE_TIME> 1999-01-01 00:01 </DATE_TIME>
<HEADER>
</HEADER>
<BODY>
<SLUG> BC-MONEY-SAVING-LADN </SLUG>
<HEADLINE>
TWENTY SIMPLE STEPS TOWARD PERSONAL UDGET SURPLUS
</HEADLINE>
 (For use by NYTimes News Service clients) 
By DEBORAH ADAMSON  
 c.1999 Los Angeles Daily News  
<TEXT>
<P>
   LOS ANGELES -- Every year, millions of Americans pledge to put
their financial house in order. This year, they say to themselves,
it will be different.
</P>
<P>
   They'll save and invest more. They'll even stick to a budget.
</P>
<P>
   Indeed, the second-most popular New Year's resolution is to
achieve financial goals, according to Citibank. The first? Lose
weight and live more healthfully.
</P>
<P>
   XXX
</P>
</TEXT>
</BODY>
<TRAILER>
</TRAILER>
</DOC>
<DOC>
<DOCNO> NYT19990101.0002 </DOCNO>
<DOCTYPE> NEWS STORY </DOCTYPE>
<DATE_TIME> 1999-01-01 00:11 </DATE_TIME>
<BODY>
<TEXT>
<P>
    hello
</P>
</TEXT>
</BODY>
</DOC>
</NEWS>
"""



def test_minidom_news():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    try:
        recordDelim = "###"
        dom = minidom.parseString(test_news_xml)
        for doc in dom.getElementsByTagName("DOC"):
            docno_xml = doc.getElementsByTagName("DOCNO")[0]
            docno = docno_xml.childNodes[0].data.strip()
            print "id=%s"%(docno)
            print "time=%s"%(docno[:8])
            body = doc.getElementsByTagName("BODY")[0]
            if doc.getElementsByTagName("HEADLINE") != []:
                headline_xml = doc.getElementsByTagName("HEADLINE")[0]
                title = headline_xml.childNodes[0].data.strip()
            else:
                title = docno
            print "title=%s"%(title)
            print "body=",
            textxml = body.getElementsByTagName("TEXT")[0]
            for para_xml in textxml.getElementsByTagName("P"):
                print para_xml.childNodes[0].data.strip()
            print recordDelim


        dom.unlink()
    except Exception, e:
        print e

test_minidom_news()


