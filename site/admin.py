import webapp2

from src.config import ConfigHandler

app = webapp2.WSGIApplication([
  webapp2.Route('/admin/config/<env:.*>', handler=ConfigHandler),
])
