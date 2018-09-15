import webapp2

from src.qrcode import QRCodeHandler

app = webapp2.WSGIApplication([
  webapp2.Route('/qr', handler=QRCodeHandler),
])
