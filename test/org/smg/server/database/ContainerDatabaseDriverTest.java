package org.smg.server.database;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smg.server.servlet.container.ContainerConstants;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ContainerDatabaseDriverTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  
  public void insertMatches() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity e1 = new Entity(ContainerConstants.MATCH);
    e1.setProperty(ContainerConstants.GAME_ID,12121212121212121L);
    e1.setUnindexedProperty(ContainerConstants.PLAYER_IDS,"[12345677777,77889000998]");
    e1.setProperty(ContainerConstants.GAME_OVER_REASON,ContainerConstants.NOT_OVER);
    Entity e2 = new Entity(ContainerConstants.MATCH);
    e2.setProperty(ContainerConstants.GAME_ID,12121212121212121L);
    e2.setUnindexedProperty(ContainerConstants.PLAYER_IDS,"[123456777778,77889000998]");
    e2.setProperty(ContainerConstants.GAME_OVER_REASON,ContainerConstants.OVER);
    ds.put(e1);
    ds.put(e2);
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
  public void testGetAllMatchesByPlayerId() {
    insertMatches();
    assertEquals(2, ContainerDatabaseDriver.getAllMatchesByPlayerId(77889000998L).size());   
  }

  @Test
  public void testGetAllUnfinishedMatchesByGameID() {
    insertMatches();
    assertEquals(1, 
        ContainerDatabaseDriver.getAllUnfinishedMatchesByGameID(12121212121212121L).size());   
  }
}
