package beans

class Geocoder {
  def base = "https://maps.googleapis.com/maps/api/geocode/xml?"

  def fillInLatLng(Stadium stadium) {
    def url = base + [sensor:false,
                      address: [stadium.name, stadium.city, stadium.state].collect {
                        URLEncoder.encode(it,'UTF-8')
                        }.join(',')
                     ].collect { k, v -> "$k=$v" }.join('&')
    //println url

    def response = new XmlSlurper().parse(url)
    //println response

    stadium.latitude = response.result[0].geometry.location.lat.toDouble()
    stadium.longitude = response.result[0].geometry.location.lng.toDouble()

    return stadium
  } // fillInLatLng
} // Geocoder
