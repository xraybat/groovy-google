import org.junit.Test

class GetGameDataTest {
  GetGameData ggd = new GetGameData(month: 10, day: 28, year: 2007)

  @Test
  void testFillInStadiumMap() {
    assert 4 == ggd.stadiumMap.size()
    //assert 30 == ggd.stadiumMap.size()

    def stadiums = ggd.stadiumMap.values()
    stadiums.each { Stadium stadium ->
      assert stadium.latitude > 25 && stadium.latitude < 48
      assert stadium.longitude > -123 && stadium.longitude < -71
    }
  }

  @Test
  void testGetGame() {
    GameResult gr = ggd.getGame 'bos', 'col', '1'

    assert 'Boston Red Sox' == gr.away
    assert 'Colorado Rockies' == gr.home
    assert 4 == gr.aScore.toInteger()
    assert 3 == gr.hScore.toInteger()
  }
}
