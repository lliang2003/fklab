import cgi, os, cgitb, time

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

intervals=[0, 1, 3, 5, 8, 10, 30, 60, 120, 300]

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
                item.round += 1
                item.lastpass = int(time.time())/3600/24
                item.save()
        ritems = []
        items = MemoStore.all()
        today = int(time.time())/3600/24
        for item in items:
            if today - item.lastpass > intervals[item.round]:
                ritems.append(item)
        path = os.path.join(os.path.dirname(__file__), 'memo.html')
        dat = {"allitems":items, "items":ritems}
        self.response.out.write(template.render(path, dat))

    def post(self):
        n = MemoStore()
        n.id = int(time.time())
        n.question = self.request.get('question')
        n.answer = self.request.get('answer')
        n.round = 0
        n.lastpass = -1
        n.put()
        self.redirect('/memo')

application = webapp.WSGIApplication([('/memo', MemoPage)] , debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
