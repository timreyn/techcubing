import webapp2

from src.models import OAuthConfig

class ConfigHandler(webapp2.RequestHandler):
  def get(self, env):
    client_id = self.request.get('id')
    client_secret = self.request.get('secret')
    config = OAuthConfig.get_by_id(env) or OAuthConfig(id = env)
    config.client_id = client_id
    config.client_secret = client_secret
    config.put()
    self.response.write('ok')
