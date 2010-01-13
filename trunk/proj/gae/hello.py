import cgi, os, cgitb, time

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class MainPage(webapp.RequestHandler):
    def get(self):
        path = os.path.join(os.path.dirname(__file__), 'index.html')
        dat = {}
        self.response.out.write(template.render(path, dat))

class TestPage(webapp.RequestHandler):
    def get(self):
        path = os.path.join(os.path.dirname(__file__), 'test.html')
        dat = {'items':range(0, 5), 'var':'abc\nDEF'}
        self.response.out.write(template.render(path, dat))

application = webapp.WSGIApplication(
        [('/test', TestPage), ('/.*', MainPage)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
