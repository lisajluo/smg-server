package org.smg.server.database;

import java.util.ArrayList;
import java.util.List;

import org.smg.server.database.models.FriendBlackList;
import org.smg.server.database.models.PlayerStatistic;
import org.smg.server.database.models.PlayerStatistic.StatisticProperty;
import org.smg.server.util.NamespaceUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DatabaseDriverFriendBlackList {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final String FRIENDBLACKLIST = NamespaceUtil.VERSION+"FRIENDBLACKLIST";
  private static final String FACEBOOK = "FACEBOOK";
  private static final String GOOGLE = "GOOGLE";
  
  public static String saveFriendBlackList(FriendBlackList fbl) {
    Entity psDB = new Entity(FRIENDBLACKLIST,String.valueOf(fbl.getId()));
    psDB.setProperty(FACEBOOK , new ArrayList<String>(fbl.getFacebookSet()));
    psDB.setProperty(GOOGLE, new ArrayList<String>(fbl.getGoogleSet()));
    datastore.put(psDB);
    return "SUCCESS";
  }
  
  @SuppressWarnings("unchecked")
  public static FriendBlackList getFriendBlackList(String id) {
    FriendBlackList fbl;
    Key psKey = KeyFactory.createKey(FRIENDBLACKLIST, 
        id);
    Entity fblDB;
    try {
      fblDB = datastore.get(psKey);
    } catch (EntityNotFoundException e) {
      fbl = new FriendBlackList(id,null,null);
      return fbl;
    }
    List<String> glist = (List<String>)fblDB.getProperty(GOOGLE);
    List<String> flist = (List<String>)fblDB.getProperty(FACEBOOK );
    fbl = new FriendBlackList(id,glist,flist);
    return fbl;
  }
}
