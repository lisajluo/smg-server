package org.smg.server.database;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.database.models.PlayerHistory;
import org.smg.server.database.models.PlayerHistory.MatchResult;
import org.smg.server.database.models.PlayerStatistic;
import org.smg.server.database.models.PlayerStatistic.StatisticProperty;
import org.smg.server.util.AccessSignatureUtil;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DatabaseDriverPlayerStatisticTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  Player legalPlayer;
  
  @Before 
  public void initPlayer() {
    legalPlayer = new Player();
    legalPlayer.setProperty(PlayerProperty.EMAIL, "abc@nyu.edu");
    legalPlayer.setProperty(PlayerProperty.FIRSTNAME, "foo");
    legalPlayer.setProperty(PlayerProperty.LASTNAME, "bar");
    legalPlayer.setProperty(PlayerProperty.NICKNAME, "foobar");
    legalPlayer.setProperty(PlayerProperty.HASHEDPASSWORD,
        AccessSignatureUtil.getHashedPassword("foobar"));
  }
  
  @Before
  public void setUp() {
      helper.setUp();
  }

  @After
  public void tearDown() {
      helper.tearDown();
  }
  @Test
  public void testSavePlayerStatisticFromHistory() {
    PlayerHistory ph = new PlayerHistory(1,1,1);
    ph.setMatchResult(MatchResult.WIN);
    DatabaseDriverPlayerStatistic.savePlayerStatisticFromHistory(ph);
    ph = new PlayerHistory(1,1,2);
    ph.setMatchResult(MatchResult.LOST);
    DatabaseDriverPlayerStatistic.savePlayerStatisticFromHistory(ph);
    PlayerStatistic ps = DatabaseDriverPlayerStatistic.getPlayerStatistic((long)1, (long)1);
    assertEquals("1",ps.getProperty(StatisticProperty.WIN));
    assertEquals("1",ps.getProperty(StatisticProperty.LOST));
  }

  @Test
  public void testGetPlayerStatistic() {
    DatabaseDriverPlayerStatistic.getPlayerStatistic((long)1, (long)1);
  }


  @Test
  public void testSetPlayerToken() {
    int token = DatabaseDriverPlayerStatistic.getPlayerToken(1, 1);
    assertEquals(0,token);
    DatabaseDriverPlayerStatistic.setPlayerToken(1, 1, 1);
    token = DatabaseDriverPlayerStatistic.getPlayerToken(1, 1);
    assertEquals(1,token);
  }
  @Test
  public void testGetPlayerStatistics() {
    PlayerHistory ph = new PlayerHistory(1,1,1);
    ph.setMatchResult(MatchResult.WIN);
    DatabaseDriverPlayerStatistic.savePlayerStatisticFromHistory(ph);
    ph = new PlayerHistory(1,1,2);
    ph.setMatchResult(MatchResult.LOST);
    DatabaseDriverPlayerStatistic.savePlayerStatisticFromHistory(ph);
    PlayerStatistic ps = DatabaseDriverPlayerStatistic.getPlayerStatistic((long)1, (long)1);
    List<PlayerStatistic> pss = DatabaseDriverPlayerStatistic.getPlayerStatistics((long)1);
    assertEquals(pss.size(),1);
    assertEquals(pss.get(0).getAllProperties(),ps.getAllProperties());
  }

}
