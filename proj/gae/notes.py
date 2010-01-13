import cgi, os, cgitb, time

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class NoteStore(db.Model):
    id = db.IntegerProperty()
    content = db.StringProperty(multiline=True)
    url = db.BooleanProperty()

class NotesPage(webapp.RequestHandler):
    def get(self):
        form = cgi.FieldStorage()
        if form.getvalue('action') == 'delete':
            notes = NoteStore.all().filter('id', int(form.getvalue('id'))).get()
            notes.delete()
            self.redirect('/notes')
        notes = NoteStore.all()
        path = os.path.join(os.path.dirname(__file__), 'notes.html')
        dat = {"notes":notes}
        self.response.out.write(template.render(path, dat))
    def post(self):
        form = cgi.FieldStorage()
        content = form.getvalue('note')
        n = NoteStore()
        n.id = int(time.time())
        n.content = content
        n.url = content.startswith("http://")
        n.put()
        self.redirect('/notes')

application = webapp.WSGIApplication([('/notes', NotesPage)] , debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
