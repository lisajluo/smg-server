package org.smg.server.database;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class DatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Returns a list of entities given a query by kind (ex: DEVELOPER), property (ex: EMAIL),
   * and the string query (ex: "foo@bar.com").
   */
  public static List<Entity> queryByProperty(String kind, String property, String query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    
    return result;
  }

  /**
   * Inserts an entity of kind (ex. DEVELOPER), keyed by keyString (ex. developerId or playerId),
   * and adding a property for every <String, Object> property in the Map properties.
   */
  public static void insertEntity(String kind, String keyString, Map<Object, Object> properties) {
    Transaction txn = datastore.beginTransaction();
    Key key = KeyFactory.createKey(kind, keyString);
    Entity entity = new Entity(kind, key);
    
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      entity.setProperty((String)entry.getKey(), entry.getValue());
    }
    
    datastore.put(entity);
    txn.commit();
  }
}