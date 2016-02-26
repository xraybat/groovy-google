//??package beans

class Stadium {
  // default map-based ctor as well as default ctor

  // properties/attributes assumed private
  int    id
  String name
  String city
  String state
  String team
  double latitude
  double longitude

  // methods assumed public
  String toString() { "($team, $name, $latitude, $longitude)" }
} // Stadium
