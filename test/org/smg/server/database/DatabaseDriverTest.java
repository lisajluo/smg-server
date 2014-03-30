package org.smg.server.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.database.models.PlayerHistory.MatchResult;
import org.smg.server.util.AccessSignatureUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DatabaseDriverTest {
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
  public void test() throws NumberFormatException, EntityNotFoundException {
    String resp = DatabaseDriverPlayer.savePlayer(legalPlayer);
    Player p = DatabaseDriverPlayer.getPlayerById(Long.parseLong(resp.split(":")[1]));
    assertTrue(p.isContain(legalPlayer));
  }
  
  @Test
  public void testUpdatePlayer() throws NumberFormatException, EntityNotFoundException {
    String resp = DatabaseDriverPlayer.savePlayer(legalPlayer);
    long playerId = Long.parseLong(resp.split(":")[1]);
    Player p = DatabaseDriverPlayer.getPlayerById(playerId);
    System.out.println(p.getProperty(PlayerProperty.ACCESSSIGNATURE));
    p.setProperty(PlayerProperty.NICKNAME, "Jinxuan");
    String resp2 = DatabaseDriverPlayer.savePlayer(p);
    assertEquals(resp2,"UPDATED_PLAYER");
  }

  @Test
  public void testDeletePlayer() throws EntityNotFoundException {
    String resp = DatabaseDriverPlayer.savePlayer(legalPlayer);
    long playerId = Long.parseLong(resp.split(":")[1]);
    Player p = DatabaseDriverPlayer.getPlayerById(playerId);
    String accessSignature = p.getProperty(PlayerProperty.ACCESSSIGNATURE);
    String resp2 = DatabaseDriverPlayer.deletePlayer(playerId, accessSignature);
    assertEquals(resp2,"DELETED_PLAYER");
  }
  
  @Test
  public void testLogin() throws EntityNotFoundException {
    String resp = DatabaseDriverPlayer.savePlayer(legalPlayer);
    long playerId = Long.parseLong(resp.split(":")[1]);
    String resp2 = DatabaseDriverPlayer.loginPlayer(playerId, "foobar")[0];
    if (resp2 == "WRONG_PASSWORD") {
      fail("wrong password");
    } else {
      assertEquals(resp2,legalPlayer.getProperty(PlayerProperty.EMAIL));
    }
  }
  
  @Test
  public void testDatabaseStringKey() throws EntityNotFoundException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


    Entity employee = new Entity("Employee", "123"+"456");

    employee.setProperty("firstName", "Antonio");
    employee.setProperty("lastName", "Salieri");
    List<Map<String,Object>> strings = new ArrayList<Map<String,Object>>();
    //strings.setProperty(StatisticProperty.PLAYERID, "1234");
    //strings.add(new PlayerStatistic());
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("playerId",12345);
    //m.put("date", new Date());
    strings.add(m);
    employee.setProperty("listString",strings);
    datastore.put(employee);
    Key k = KeyFactory.createKey("Employee", "123"+"456");
    Entity e2 = datastore.get(k);
    assertEquals(employee,e2);
    assertEquals(strings,e2.getProperty("listString"));
  }
  
  @Test
  public void testSt() {
    MatchResult m = MatchResult.valueOf("WIN");
    assertEquals(m,MatchResult.WIN);
    MatchResult m2 = MatchResult.valueOf(null);
    assertEquals(m2,MatchResult.WIN);
  }
}