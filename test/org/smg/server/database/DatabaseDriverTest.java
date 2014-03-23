package org.smg.server.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.util.AccessSignatureUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    String resp = DatabaseDriver.savePlayer(legalPlayer);
    Player p = DatabaseDriver.getPlayerById(Long.parseLong(resp.split(":")[1]));
    assertTrue(p.isContain(legalPlayer));
  }
  
  @Test
  public void testUpdatePlayer() throws NumberFormatException, EntityNotFoundException {
    String resp = DatabaseDriver.savePlayer(legalPlayer);
    long playerId = Long.parseLong(resp.split(":")[1]);
    Player p = DatabaseDriver.getPlayerById(playerId);
    System.out.println(p.getProperty(PlayerProperty.ACCESSSIGNATURE));
    p.setProperty(PlayerProperty.NICKNAME, "Jinxuan");
    String resp2 = DatabaseDriver.savePlayer(p);
    assertEquals(resp2,"UPDATED_PLAYER");
  }

  @Test
  public void testDeletePlayer() throws EntityNotFoundException {
    String resp = DatabaseDriver.savePlayer(legalPlayer);
    long playerId = Long.parseLong(resp.split(":")[1]);
    Player p = DatabaseDriver.getPlayerById(playerId);
    String accessSignature = p.getProperty(PlayerProperty.ACCESSSIGNATURE);
    String resp2 = DatabaseDriver.deletePlayer(playerId, accessSignature);
    assertEquals(resp2,"DELETED_PLAYER");
  }
  
  @Test
  public void testLogin() throws EntityNotFoundException {
    String resp = DatabaseDriver.savePlayer(legalPlayer);
    long playerId = Long.parseLong(resp.split(":")[1]);
    String resp2 = DatabaseDriver.loginPlayer(playerId, "foobar")[0];
    if (resp2 == "WRONG_PASSWORD") {
      fail("wrong password");
    } else {
      assertEquals(resp2,legalPlayer.getProperty(PlayerProperty.EMAIL));
    }
  }
}
