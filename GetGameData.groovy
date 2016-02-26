@GrabConfig(systemClassLoader=true)
@Grapes(
	@Grab(group='mysql', module='mysql-connector-java', version='5.1.37')
	//@Grab(group='com.h2database', module='h2', version='1.4.190')
)

import groovy.sql.Sql
import java.util.regex.Matcher

class GetGameData {
  def day
  def month
  def year

  String base = 'http://gd2.mlb.com/components/game/mlb/'

  Map stadiumMap = [:]
  Map abbrevs = [
    ana: 'Los Angeles (A)', ari: 'Arizona',     atl: 'Atlanta',
    bal: 'Baltimore',       bos: 'Boston',      cha: 'Chicago (A)',
    chn: 'Chicago (N)',     cin: 'Cincinnati',  cle: 'Cleveland',
    col: 'Colorado',        det: 'Detroit',     flo: 'Florida',
    hou: 'Houston',         kca: 'Kansas City', lan: 'Los Angeles (N)',
    mil: 'Milwaukee',       min: 'Minnesota',   nya: 'New York (A)',
    nyn: 'New York (N)',    oak: 'Oakland',     phi: 'Philadelphia',
    pit: 'Pittsburgh',      sdn: 'San Diego',   sea: 'Seattle',
    sfn: 'San Francisco',   sln: 'St. Louis',   tba: 'Tampa Bay',
    tex: 'Texas',           tor: 'Toronto',     was: 'Washington'
  ]

  GetGameData() {
    fillInStadiumMap()
  }

  def fillInStadiumMap() {
    // move credentials to env variable and Config class??
    def credentialsFile = new File('../xml/credentials.groovy')	// inbuilt config files and parsing of
    def configSlurper = new ConfigSlurper()
    def credentials = configSlurper.parse(credentialsFile.toURL())

    println '>>>>>> 1111111111'
    /*Sql db = Sql.newInstance('jdbc:h2:~/baseball',
                             credentials.database.user,
                             credentials.database.password,
                             'org.h2.Driver')*/
		 Sql db = Sql.newInstance('jdbc:mysql://localhost:3306/baseball',
														  credentials.database.user,
													    credentials.database.password,
													    'com.mysql.jdbc.Driver')
    println '>>>>>> 22222222222'

    db.eachRow('select * from stadium') { row ->
      Stadium stadium = new Stadium(name: row.name,
                                    team: row.team,
                                    latitude: row.latitude,
                                    longitude: row.longitude)
      println '>>>>>> ' + row.name + ', ' + row.team + ', ' + row.latitude + ', ' + row.longitude
      stadiumMap[stadium.team] = stadium
    }

    db.close()
  }

  def getGame(away, home, num) {
    println "${abbrevs[away]} at ${abbrevs[home]} on ${month}/${day}/${year}"

    def url = base + "year_${year}/month_${month}/day_${day}/"
    def game = "gid_${year}_${month}_${day}_${away}mlb_${home}mlb_${num}/boxscore.xml"

    def boxscore = new XmlParser().parse("$url$game")
    def awayName = boxscore.@away_fname
    def awayScore = boxscore.linescore[0].@away_team_runs
    def homeName = boxscore.@home_fname
    def homeScore = boxscore.linescore[0].@home_team_runs

    println "$awayName $awayScore, $homeName $homeScore (game $num)"

    GameResult result = new GameResult(home: homeName,
                                       away: awayName,
                                       hScore: homeScore,
                                       aScore: awayScore,
                                       stadium: stadiumMap[home])
    return result
  }

  def getGames() {
    def gameResults = []
    println "Games for ${month}/${day}/${year}"

    String url = base + "year_$year/month_$month/day_$day/"
    String gamePage = url.toURL().text

    def pattern = /\"gid_${year}_${month}_${day}_(\w*)mlb_(\w*)mlb_(\d)/
    Matcher m = gamePage =~ pattern
    if (m) {
      m.count.times { line ->
        String away = m[line][1]
        String home = m[line][2]
        String num = m[line][3]
        try {
          GameResult gr = this.getGame(away,home,num)
          gameResults << gr
        } catch (Exception e) {
          println "${abbrevs[away]} at ${abbrevs[home]} not started yet"
        }
      }
    }

    return gameResults
  }
}
