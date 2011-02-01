from django.utils import simplejson
from django.db import models
import uuid

class UUIDField(models.CharField) :
    def __init__(self, *args, **kwargs):
        kwargs['max_length'] = kwargs.get('max_length', 64 )
        kwargs['blank'] = True
        models.CharField.__init__(self, *args, **kwargs)
    
    def pre_save(self, model_instance, add):
        if add :
            value = str(uuid.uuid4())
            setattr(model_instance, self.attname, value)
            return value
        else:
            return super(models.CharField, self).pre_save(model_instance, add)

class Drink(models.Model):
    uuid = UUIDField(primary_key=True, editable=False)
    beername = models.CharField(max_length=255)
    brewery = models.CharField(max_length=255, blank=True, null=True)
    location = models.CharField(max_length=255, blank=True, null=True)
    abv = models.FloatField(blank=True, null=True)
    style = models.CharField(max_length=255, blank=True, null=True)
    about_this_beer = models.TextField(blank=True, null=True, db_column='beernotes')
    user = models.CharField(max_length=20)
    container = models.CharField(max_length=32)
    stamp = models.DateTimeField()
    tasting_notes = models.TextField(blank=True, null=True, db_column='drinknotes')
    rating = models.FloatField(blank=True, null=True)
    created = models.DateTimeField(auto_now_add=True)
    updated = models.DateTimeField(auto_now=True)

    privatefields = [ 'uuid', 'about_this_beer', 'user', 'container',
                     'stamp', 'tasting_notes', 'rating', 'created', 'updated' ]
    publicfields = [ 'beername', 'brewery', 'location', 'abv', 'style' ]

    def __str__(self):
        if (self.created == None):
            return self.beername + " drunk by " + self.user
        else:
            return self.beername + " drunk by " + self.user + " at " + self.created.strftime('%Y-%m-%d %H:%M:%S')

    def json(self, detailed=False):
        if detailed:
            return simplejson.dumps( {'beername': self.beername,
                                      'brewery': self.brewery,
                                      'location': self.location,
                                      'abv': self.abv,
                                      'style': self.style,
                                      'about_this_beer': self.about_this_beer,
                                      
                                      'uuid': self.uuid,
                                      'user': self.user,
                                      'container': self.container,
                                      'stamp': self.stamp,
                                      'tasting_notes': self.tasting_notes,
                                      'rating': self.rating} )
        else:
            return simplejson.dumps( {'beername': self.beername,
                                      'brewery': self.brewery,
                                      'location': self.location,
                                      'abv': self.abv,
                                      'style': self.style} )

