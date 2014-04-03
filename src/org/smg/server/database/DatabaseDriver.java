package org.smg.server.database;

import java.util.List;

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
   * Returns the entity of kind (ex. DEVELOPER) keyed from keyString.
   * @deprecated use specific [Developer|Game|Container|Player]DatabaseDriver methods instead. 
   */
  @Deprecated
  public static Entity getEntityByKey(String kind, long keyId) {
    Entity entity = null;
    
    try {
      Key key = KeyFactory.createKey(kind, keyId);
      entity = datastore.get(key);
    }
    catch (Exception e) {
    }
    return entity;
  }

  /**
   * Returns a list of entities given a query by kind (ex: DEVELOPER), property (ex: EMAIL),
   * and the query (ex: "foo@bar.com").
   * @deprecated use specific [Developer|Game|Container|Player]DatabaseDriver methods instead. 
   */
  @Deprecated
  public static List<Entity> queryByProperty(String kind, String property,
      Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());

    return result;
  }

  /**
   * Deletes an entity (ie., a developer of kind DEVELOPER).
   * @deprecated use specific [Developer|Game|Container|Player]DatabaseDriver methods instead. 
   */
  @Deprecated
  public static boolean deleteEntity(String kind, long keyId) {
    Transaction txn = datastore.beginTransaction();
    try {
      Key key = KeyFactory.createKey(kind, keyId);
      datastore.delete(key);
      txn.commit();
      return true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}