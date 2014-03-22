package org.smg.server.database;

import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
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

  public static void insertMatchEntity(JSONObject match) throws JSONException {
    Transaction txn = datastore.beginTransaction();
    //Key key = KeyFactory.createKey("Match", match.getInt("matchId"));
    Entity entity = new Entity("Match", match.getInt("matchId"));
    entity.setProperty("matchId", match.getInt("matchId"));
    entity.setProperty("gameId", match.getInt("gameId"));
    String playerIds = match.getJSONArray("playerIds").toString();
    entity.setUnindexedProperty("playerIds", playerIds);
    entity.setProperty("playerThatHasTurn", match.getInt("playerThatHasTurn"));
    entity.setUnindexedProperty("gameOverScores", match.getJSONObject("gameOverScores").toString());
    entity.setProperty("gameOverReason", match.getString("gameOverReason"));
    entity.setUnindexedProperty("history", match.getJSONArray("history").toString());
    datastore.put(entity);
    txn.commit();
  }
}