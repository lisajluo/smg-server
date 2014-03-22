package org.smg.server.database;

import java.util.List;

import org.smg.server.servlet.container.Constants;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class DatabaseDriver {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  
  /**
   * Returns a list of entities given a query by kind (ex: DEVELOPER), property (ex: EMAIL),
   * and the string query (ex: "foo@bar.com").
   */
  public static List<Entity> queryByProperty(String kind, String property, Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    
    return result;
  }
  
  public static Entity queryById(String kind, Long id) {
    Key key = KeyFactory.createKey(kind, id);
    try {
      return datastore.get(key);
    } catch (EntityNotFoundException e) {
      // TODO Auto-generated catch block
      return null;
      //e.printStackTrace();
    }
  }

  public static long insertMatchEntity(JSONObject match) throws JSONException {
    Transaction txn = datastore.beginTransaction();
    //Key key = KeyFactory.createKey("Match", match.getInt("matchId"));
    Entity entity = new Entity(Constants.MATCH);
    //entity.setProperty("matchId", match.getInt("matchId"));
    entity.setProperty(Constants.GAME_ID, match.getLong(Constants.GAME_ID));
    entity.setUnindexedProperty(Constants.PLAYER_IDS,
        match.getJSONArray(Constants.PLAYER_IDS).toString());
    entity.setProperty(Constants.PLAYER_THAT_HAS_TURN, 
        match.getLong(Constants.PLAYER_THAT_HAS_TURN));
    entity.setUnindexedProperty(Constants.GAME_OVER_SCORES, 
        match.getJSONObject(Constants.GAME_OVER_SCORES).toString());
    entity.setProperty(Constants.GAME_OVER_REASON, match.getString(Constants.GAME_OVER_REASON));
    entity.setUnindexedProperty(Constants.HISTORY, 
        match.getJSONArray(Constants.HISTORY).toString());
    datastore.put(entity);
    txn.commit();
    return entity.getKey().getId();
  }
}