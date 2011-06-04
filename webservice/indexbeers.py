from django.core.management import setup_environ

import settings
setup_environ(settings)

from webservice.json.models import Drink, CanonicalBeer
from django.db.models import Count
from operator import itemgetter

# Defines the criteria by which a beer is worthy of being returned
# as search results. Some minimum number of independent drinks is
# probably the best method for now.
MINDRINKS = 10
def canon_worthy(cand):
    return (cand['num'] >= MINDRINKS)

MINWITHVALUE = 0.5
def get_best_value(candvals, numdrinks):
    # Now which value is the best?
    if len(candvals) > 0:
        candvals = sorted(candvals.iteritems(), key=itemgetter(1), reverse=True)

        if (candvals[0][1]/numdrinks >= MINWITHVALUE):
            return candvals[0][0]

    return None

def update_canonbeer(canonbeer):
    canonbeer.save()

def gen_canonbeer(cand):
    name = cand['beername']
    drinks = Drink.objects.filter(beername = name)
    canonbeer = CanonicalBeer(beername = name)

    print "Generating a canonical beer for %s" % (name)

    for prop in ['brewery', 'location', 'abv', 'style']:
        candvals = {}
        # Count up the candidates
        for d in drinks:
            dprop = getattr(d, prop)
            if dprop != None and dprop != "":
                if dprop in candvals:
                    candvals[dprop]+=1
                else:
                    candvals[dprop]=1

        # Now which value is the best?
        propval = get_best_value(candvals, len(drinks))
        if propval != None:
            print "%s: %s" % (prop, propval)
            setattr(canonbeer, prop, propval)

    # Save or update
    canonbeer.num_drunk = len(drinks)
    update_canonbeer(canonbeer)
    print

def do_index():
    # TODO - It'd be nice for the num to be distinct users
    candidate_beers = Drink.objects.values('beername').annotate(num=Count('user'))

    for cand in candidate_beers:
        if canon_worthy(cand):
            canon = gen_canonbeer(cand)

do_index()

