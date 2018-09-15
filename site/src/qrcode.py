import webapp2

class QRCodeHandler(webapp2.RequestHandler):
  def get(self):
    self.response.write('Hello world!')
