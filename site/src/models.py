from google.appengine.ext import ndb

class OAuthConfig(ndb.Model):
  client_id = ndb.StringProperty()
  client_secret = ndb.StringProperty()
