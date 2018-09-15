import os
from google.appengine.ext import vendor

lib_directory = os.path.dirname(__file__) + '/lib'

vendor.add(lib_directory)
