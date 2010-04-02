#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Apr 02 18:47:07 CST 2010


import cgi, os, cgitb, time
import re, string

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class WordStore(db.Model):
    word = db.StringProperty()

class WordPage(webapp.RequestHandler):
    def get(self):
        path = os.path.join(os.path.dirname(__file__), 'word.html')
        self.response.out.write(template.render(path, None))

    def post(self):
        html = ""
        txt = self.request.get('text')
        kw = set()
        wpath = os.path.join(os.path.dirname(__file__), 'w.w')
        for item in WordStore.all():
            kw.add(item.word)
        for token in open(wpath).read().split():
            kw.add(token)
        for line in txt.split("\n"):
            for rtoken in line.split():
                token = rtoken.strip(",.\"'").lower()
                if "'" in token or token in kw:
                    html += token+" "
                else:
                    html+="<span id='%s' class='unknown' onclick='know(\"%s\")'>%s</span> "%(rtoken, rtoken,rtoken)
            html+="<br>\n"

        path = os.path.join(os.path.dirname(__file__), 'check.html')
        dat = {"txt":html}
        self.response.out.write(template.render(path, dat))

class WordBrowse(webapp.RequestHandler):
    def get(self):
        path = os.path.join(os.path.dirname(__file__), 'w.w')
        dat = {"items":open(path).read().split()[:100]}
        self.response.out.write(template.render(path, dat))

class WordImport(webapp.RequestHandler):
    def get(self):
        form = cgi.FieldStorage()
        kw = form.getvalue("known")
        if kw:
            for token in kw.split(","):
                w = WordStore()
                w.word = token.lower()
                w.put()
        self.redirect("/word")

    def post(self):
        cgitb.enable()
        form = cgi.FieldStorage()
        fileitem = form["mfile"]
        if fileitem.file:
            content = form.getvalue('mfile')
            for record in content.split():
                record = record.strip()
                n = WordStore();
                n.word = record
                n.put()
                break
        self.redirect('/word')

application = webapp.WSGIApplication([('/word', WordPage), ('/word/import', WordImport), ('/word/browse', WordBrowse)] , debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
