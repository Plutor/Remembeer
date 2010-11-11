import os
import sys

path = '/web/remembeer/webservice'
if path not in sys.path:
    sys.path.append(path)

os.environ['DJANGO_SETTINGS_MODULE'] = 'webservice.settings'

import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()
