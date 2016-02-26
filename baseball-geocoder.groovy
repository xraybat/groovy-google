// baseball-geocode.groovy
//
// url: https://maps.googleapis.com/maps/api/geocode/xml?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true_or_false

package beans //??or import

def stadiums = []
stadiums << new Stadium(name:'Angel Stadium',  city:'Anaheim',    state:'CA', team:'ana')
stadiums << new Stadium(name:'Chase Field',    city:'Phoenix',    state:'AZ', team:'ari')
stadiums << new Stadium(name:'Rogers Centre',  city:'Toronto',    state:'ON', team:'tor')
stadiums << new Stadium(name:'Nationals Park', city:'Washington', state:'DC', team:'was')
//println stadiums

def geo = new Geocoder()
stadiums.each { s ->
  print 'was ' + s; geo.fillInLatLng s; println ' now ' + s
}
