import webapp2

from src.jinja import JINJA_ENVIRONMENT

class QRCodeHandler(webapp2.RequestHandler):
  def get(self):
    template = JINJA_ENVIRONMENT.get_template('qr.html')
    self.response.write(template.render({
      'qrcode': 'abc',
    }))
