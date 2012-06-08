import cgi
import datetime
import urllib
import webapp2

from google.appengine.ext import db
from google.appengine.api import users


class NoteEntry(db.Model):
  date = db.DateTimeProperty(auto_now_add=True)
  content = db.StringProperty(multiline=True)


def notekey():
  return db.Key.from_path('user', str(users.get_current_user()))


class MainPage(webapp2.RequestHandler):
  def get(self):
    self.response.out.write('<html><body>')


    self.response.out.write("""
          <form action="/note" method="post">
            <div><textarea name="content" rows="3" cols="40"></textarea></div>
            <div><input type="submit" value="Note"></div>
          </form>
          <hr>""")


    entries = db.GqlQuery("SELECT * "
                            "FROM NoteEntry "
                            "WHERE ANCESTOR IS :1 "
                            "ORDER BY date DESC",
                            db.Key.from_path('user', str(users.get_current_user())))

    for entry in entries:
        self.response.out.write('<b>%s</b> &nbsp;&nbsp;' % str(entry.date)[:19])
        self.response.out.write(cgi.escape(entry.content))
        self.response.out.write('<br/>\n')

    self.response.out.write("""
        </body>
      </html>""") 
                
  def post(self):
    entry = NoteEntry(parent=notekey())
    entry.content = self.request.get('content')
    entry.put()
    self.redirect('/note')

app = webapp2.WSGIApplication([('/', MainPage),
                               ('/note', MainPage)],
                              debug=True)
