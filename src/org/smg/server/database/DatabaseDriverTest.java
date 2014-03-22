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
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    //int i = DatabaseDriver.test();
    String resp = DatabaseDriver.savePlayer(legalPlayer);
    System.out.println(resp.split(":")[1]);
    Player p = DatabaseDriver.getPlayerById(Long.parseLong(resp.split(":")[1]));
    System.out.println(p.getProperty(PlayerProperty.EMAIL));
    assertTrue(p.isContain(legalPlayer));
  }

}
