import cgi, os, cgitb, time
import re

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

intervals=[1, 0, 2, 4, 3, 0, 1, 0, 9]

#secondsPerDay = 2
secondsPerDay = 24*3600

class MemoStore(db.Model):
    id = db.IntegerProperty()
    question = db.TextProperty()
    answer = db.TextProperty()
    round = db.IntegerProperty()
    lastpass = db.IntegerProperty()

class MemoPage(webapp.RequestHandler):
    def get(self):
        form = cgi.FieldStorage()
        if form.getvalue('action') == 'delete':
            item = MemoStore.all().filter('id', int(form.getvalue('id'))).get()
            item.delete()
            self.redirect('/memo')
        elif form.getvalue('action') == 'finish':
            for r in form.list:
                if r.name == "action": continue
                id = int(r.name[1:])
                item = MemoStore.all().filter("id =", id).get()
                #if not item: continue
                if r.value == "pass":
                    current = int(time.time())/secondsPerDay
                    if current-item.lastpass>=intervals[item.round]:
                        item.lastpass = current
                        item.round += 1 
                elif r.value == "done":
                    item.round = 99 
                item.save()
            self.redirect('/memo')
        elif form.getvalue('action') == 'export':
            self.response.headers['Content-Type'] = "txt/html"
            self.response.headers['Content-Disposition'] = "attachment; filename=memo.txt"
            items = MemoStore.all()
            for item in items:
                self.response.out.write("%s#%s#%d#%d#%d$$$"%(item.question, item.answer, item.id, item.round, item.lastpass))
            return

        ritems = []
        items = MemoStore.all()
        today = int(time.time())/secondsPerDay
        for item in items:
            if len(ritems)>=10:break
            if item.round < len(intervals) and today - item.lastpass >= intervals[item.round] and item.round%2 == 0:
                item.answer = "<br>".join(item.answer.split("\n")).strip()
                ritems.append(item)
        if len(ritems) > 0:
            path = os.path.join(os.path.dirname(__file__), 'memo.html')
            dat = {"allitems":items, "items":ritems[:10]}
            self.response.out.write(template.render(path, dat))
        else:
            for item in items:
                if len(ritems)>=10:break
                if item.round < len(intervals) and today - item.lastpass >= intervals[item.round]:
                    line = item.answer.split("\n")[0]
                    sindex = line.find("[")
                    if sindex < 0: sindex = line.find("/")
                    if sindex < 0: sindex = len(line)
                    item.word = line[:sindex].lower().strip().replace("'"," ")
                    item.phonetic = line[sindex:]
                    item.answer = "<br>".join(item.answer.split("\n")[1:]).strip()
                    for token in item.word.split():
                        if len(token)<4: continue
                        item.question = re.sub(token[1:-1], "*", item.question.lower())
                    ritems.append(item)
            path = os.path.join(os.path.dirname(__file__), 'memo2.html')
            dat = {"allitems":items, "items":ritems}
            self.response.out.write(template.render(path, dat))

    def post(self):
        n = MemoStore()
        n.id = int(time.time())
        n.question = self.request.get('question')
        n.answer = self.request.get('answer')
        n.round = 0
        n.lastpass = -1
        if len(n.question)>0 and len(n.answer)>0:
            n.put()
        self.redirect('/memo')

class MemoImportPage(webapp.RequestHandler):
    def post(self):
        cgitb.enable()
        form = cgi.FieldStorage()
        fileitem = form["mfile"]
        if fileitem.file:
            content = form.getvalue('mfile')
            for record in content.split("$$$"):
                if not record or len(record)==0: break
                self.response.out.write("record='%s'\n"%(record))
                n = MemoStore();
                n.id = int(time.time())
                parts = unicode(record, "utf-8").split("#")
                n.question, n.answer = parts[:2]
                n.id, n.round, n.lastpass = map(int, parts[2:])
                n.put()
        self.redirect('/memo')
 
application = webapp.WSGIApplication([('/memo', MemoPage),('/memo_import', MemoImportPage)] , debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
