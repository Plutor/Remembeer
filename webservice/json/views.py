from django.http import HttpResponse
from django.template import Context, loader
from webservice.json.models import Drink
from django.core import serializers
from django.utils import simplejson
from django.db import IntegrityError
from django.db.models import Q, Count

def query(request):
    if request.method == "POST" or request.GET.has_key('debug'):
        jsonquerystr = request.REQUEST['q']
        q = simplejson.loads(jsonquerystr)

        # Build object with known fields
        d = Drink()
        candidates_where = Q()
        for key in ['beername']:
            if q.has_key(key):
                setattr(d, key, q[key])
                candidates_where  = candidates_where | Q( **{'%s' % key: q[key]} )

        # For each unknown field, find most likely value and set it
        for key in Drink.publicfields:
            if not q.has_key(key):
                beerswithval = Drink.objects.filter( candidates_where ).aggregate(num=Count(key))
                if len(beerswithval) > 0:
                    beerswithval = float(beerswithval['num'])

                candidatevals = Drink.objects.filter( candidates_where ).filter( **{'%s__isnull' % key: False} ).values(key).annotate(num=Count('beername')).order_by('-num')

                # TODO - Come up with a better way than percentage to calculate confidence
                if (len(candidatevals) > 0):
                    mostlikely = candidatevals[0]
                    confidence = mostlikely['num'] / beerswithval

                    if (confidence > 0.75):
                        setattr(d, key, mostlikely[key])

        # If search is false
        if q.has_key('search') and (q['search'] == "false"):
            # Add the private fields
            for key in Drink.privatefields:
                if q.has_key(key):
                    setattr(d, key, q[key])

            # save it
            # TODO - handle problems better
            try:
                d.save()
            except IntegrityError, err:
                return HttpResponse( "{ \"error\": \"" + str(err) + "\" }" )

            # respond with the detailed results
            return HttpResponse( d.json(detailed=True) )
        else:
            # Else, respond with the simple results
            return HttpResponse( d.json() )

    return HttpResponse( "{}" )

def form(request):
    t = loader.get_template('form.html')
    c = Context({})
    return HttpResponse(t.render(c))
