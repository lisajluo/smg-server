package org.smg.server.database;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.database.models.PlayerHistory;
import org.smg.server.database.models.PlayerHistory.MatchResult;
import org.smg.server.util.AccessSignatureUtil;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DatabaseDriverPlayerHistoryTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  Player legalPlayer;
  
  PlayerHistory ph1;
  PlayerHistory ph2;
  
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
  public void initHistory() throws InterruptedException {
    ph1 = new PlayerHistory(1,1,1);
    ph1.setDate(new Date());
    ph1.setMatchResult(MatchResult.WIN);
    ph1.setScore(1);
    ph1.setTokenChange(0);
    ph1.addOpponentId((long)2);
    ph1.addOpponentId((long)3);
    Thread.sleep(2000);
    ph2 = new PlayerHistory(1,1,2);
    ph2.setDate(new Date());
    ph2.setMatchResult(MatchResult.LOST);
    ph2.setScore(-1);
    ph2.setTokenChange(0);
    ph2.addOpponentId((long)2);
    ph2.addOpponentId((long)3);
    
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
  public void testSavePlayerHistory() {
    PlayerHistory ph = new PlayerHistory(1,1,1);
    ph.setDate(new Date());
    ph.setMatchResult(MatchResult.WIN);
    ph.setScore(1);
    ph.setTokenChange(0);
    ph.addOpponentId((long)2);
    ph.addOpponentId((long)3);
    DatabaseDriverPlayerHistory.savePlayerHistory(ph);
  }
  
  @Test
  public void testGetPlayerHistory() {
    DatabaseDriverPlayerHistory.savePlayerHistory(ph1);
    DatabaseDriverPlayerHistory.savePlayerHistory(ph2);
    List<PlayerHistory> lphs = DatabaseDriverPlayerHistory.getPlayerHistory(1,1);
    System.out.println(lphs.get(0).getMatchId());
    System.out.println(lphs.get(0).getDate());
    System.out.println(lphs.get(1).getDate());
    assertEquals(lphs.get(0),ph2);
    assertEquals(lphs.get(1),ph1);
    lphs = DatabaseDriverPlayerHistory.getPlayerAllHistory(1);
    assertEquals(lphs.get(0),ph2);
    assertEquals(lphs.get(1),ph1);
    
  }
}
