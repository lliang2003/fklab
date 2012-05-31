import cgi, os, cgitb, time

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class FileStore(db.Model):
    id = db.IntegerProperty()
    name = db.StringProperty()
    content = db.BlobProperty()
    mime = db.StringProperty()
    size = db.IntegerProperty()

class StorePage(webapp.RequestHandler):
    def get(self):
        form = cgi.FieldStorage()
        if form.getvalue('action') == 'download':
            f = FileStore.all().filter('id', int(form.getvalue('id'))).get()
            self.response.headers['Content-Type'] = f.mime
            self.response.headers['Content-Disposition'] = "attachment; filename=%s"%f.name
            self.response.out.write(f.content)
        elif form.getvalue('action') == 'delete':
            f = FileStore.all().filter('id', int(form.getvalue('id'))).get()
            f.delete()
            self.redirect('/store')
        else:
            path = os.path.join(os.path.dirname(__file__), 'store.html')
            self.response.out.write(template.render(path, {'flist':FileStore.all()}))
    def post(self):
        cgitb.enable()
        form = cgi.FieldStorage()
        fileitem = form["ufile"]
        if fileitem.file:
            content = form.getvalue('ufile')
            name = form['ufile'].filename
            f = FileStore()
            f.name = os.path.basename(name)
            f.content = content
            f.id = int(time.time())
            f.mime = form['ufile'].headers['content-type']
            f.size = len(content)
            f.put()
            dat = '%s stored' % f.name
        else:
            dat = 'Not file'
        path = os.path.join(os.path.dirname(__file__), 'store.html')
        self.response.out.write(template.render(path, {'flist':FileStore.all()}))

application = webapp.WSGIApplication([('/store', StorePage)] , debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
