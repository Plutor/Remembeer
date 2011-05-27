from django.http import HttpResponse
from django.template import Context, loader
from webservice.json.models import Drink, CanonicalBeer
from django.core import serializers
from django.utils import simplejson
from django.db import IntegrityError
from django.db.models import Q, Count
from django.shortcuts import render_to_response
from datetime import datetime, timedelta

def show_index():
    today = datetime.today()
    dayago = today - timedelta(1)
    pastdaycount = Drink.objects.filter(stamp__range=(dayago, today)).aggregate(Count('stamp'))
    
    totalcount = Drink.objects.aggregate(Count('stamp'))

    beercount = CanonicalBeer.objects.aggregate(Count('beername'))

    recent_beers = Drink.objects.all().order_by('-stamp')[:10]

    return render_to_response('index.html', {
        'recent_beers': recent_beers,
        'pastdaycount': pastdaycount['stamp__count'],
        'totalcount': totalcount['stamp__count'],
        'beercount': beercount['beername__count'],
    })

def search(q):
    beers = []
    if (q["beername"] != None):
        # TODO - Sort by number of beers, or by closeness of match, or something
        # TODO - Definitely return exact matches first
        beers = CanonicalBeer.objects.filter(beername__contains = q["beername"]).order_by("num_drunk")[:5]

    # Respond with json
    json = "[" + ",".join([b.json() for b in beers]) + "]"
    return HttpResponse( json )

def update(q):
    d = Drink()

    # Add all of the fields
    for key in Drink.publicfields:
        if q.has_key(key):
            setattr(d, key, q[key])
    for key in Drink.privatefields:
        if q.has_key(key):
            setattr(d, key, q[key])

    # save it
    # TODO - handle problems better
    try:
        d.save()
    except IntegrityError, err:
        return HttpResponse( "[ { \"error\": \"" + str(err) + "\" } ]" )

    # respond with the detailed results
    return HttpResponse( '[' + d.json(detailed=True) + ']' )

def query(request):
    if request.method == "POST":
        jsonquerystr = request.REQUEST['q']
        q = simplejson.loads(jsonquerystr)

        if jsonquerystr == None or jsonquerystr == "":
            return show_index()
        elif q.has_key('search') and (q['search'] == "false"):
            return update(q)
        else:
            return search(q)

    return show_index()

def form(request):
    t = loader.get_template('form.html')
    c = Context({})
    return HttpResponse(t.render(c))
