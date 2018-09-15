import webapp2

from src.models import OAuthConfig

class ConfigHandler(webapp2.RequestHandler):
  def get(self, env):
    config = OAuthConfig.get_by_id(env) or OAuthConfig(id = env)
    config.client_id = self.request.get('id')
    config.client_secret = self.request.get('secret')
    config.wca_site = self.request.get('wca')
    config.put()
    self.response.write('ok')
