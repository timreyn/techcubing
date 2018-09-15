import httplib
import json
import urllib
import webapp2

from src.jinja import JINJA_ENVIRONMENT
from src.models import OAuthConfig


class QRCodeHandler(webapp2.RequestHandler):
  def get(self):
    env = self.request.get('state') or self.request.get('env') or 'prod'
    oauth = OAuthConfig.get_by_id(env)
    if not oauth:
      self.response.status = 404
      return
    code = self.request.get('code')
    if code:
      post_data = {
          'grant_type': 'authorization_code',
          'code': code,
          'client_id': oauth.client_id,
          'client_secret': oauth.client_secret,
          'redirect_uri': self.request.host_url + '/qr',
      }
      conn = httplib.HTTPSConnection(oauth.wca_site + '/oauth/token')
      conn.request('POST', '', urllib.urlencode(post_data), {})
      response = conn.getresponse()
      response_json = json.loads(response.read())

      uri = 'techcubing://acquire_device/' + response_json['access_token']
      template = JINJA_ENVIRONMENT.get_template('qr.html')
      self.response.write(template.render({
        'qrcode': uri,
        'clean_url': '/qr?env=' + env
      }))
    else:
      params = {
          'client_id': oauth.client_id,
          'response_type': 'code',
          'redirect_uri': self.request.host_url + '/qr',
          'scope': 'public',
          'state': env,
      }
      self.redirect(str('https://' + oauth.wca_site + '/oauth/authorize?' + urllib.urlencode(params)))
