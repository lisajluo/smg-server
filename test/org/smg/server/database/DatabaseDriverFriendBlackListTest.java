package org.smg.server.database;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smg.server.database.models.FriendBlackList;
import org.smg.server.database.models.FriendBlackList.FriendType;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DatabaseDriverFriendBlackListTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  @Before
  public void setUp() {
      helper.setUp();
  }

  @After
  public void tearDown() {
      helper.tearDown();
  }
  
  @Test
  public void testGetFriendBlackList() {
    FriendBlackList fbl = new FriendBlackList("1",null,null);
    fbl.addFriend("2", FriendType.Facebook);
    fbl.addFriend("2", FriendType.Facebook);
    fbl.addFriend("3", FriendType.Facebook);
    fbl.addFriend("4", FriendType.Google);
    fbl.addFriend("5", FriendType.Google);
    
    DatabaseDriverFriendBlackList.saveFriendBlackList(fbl);
    FriendBlackList fbl2 = DatabaseDriverFriendBlackList.getFriendBlackList("1");
    assertTrue(fbl.equals(fbl2));
  }
  
  @Test
  public void testJSONArrayFilter() throws JSONException {
    JSONArray jsa2= new JSONArray("[{id:1},{id:2},{id:3}]");
    JSONArray jsa = new JSONArray();
    for (int i = 0; i < jsa2.length(); i ++) {
      JSONObject obj = jsa2.getJSONObject(i);
      if (obj.get("id").equals(1)){
        continue;
      }
      jsa.put(obj);
    }
    for (int i = 0; i < jsa.length(); i ++) {
      System.out.println(jsa.get(i).toString());
    }
  }

}
