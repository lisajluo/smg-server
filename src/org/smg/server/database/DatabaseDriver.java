package org.smg.server.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smg.server.util.AccessSignatureUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
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
   */
  public static Entity getEntityByKey(String kind, long keyId) {
    Key key = KeyFactory.createKey(kind, keyId);
    Entity entity = null;
    
    try {
      entity = datastore.get(key);
    }
    catch (EntityNotFoundException e) {
    }
    return entity;
  }
  
  /**
   * Returns the entity of kind (ex. DEVELOPER) keyed from keyString, in the form of a (copied) Map.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Map getEntityMapByKey(String kind, long keyId) {
    Entity entity = getEntityByKey(kind, keyId);
    if (entity == null) {
      return null;
    }
    else {
      return new HashMap(entity.getProperties());
    }
  }

  /**
   * Returns a list of entities given a query by kind (ex: DEVELOPER), property (ex: EMAIL),
   * and the query (ex: "foo@bar.com").
   */
  public static List<Entity> queryByProperty(String kind, String property, Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    
    return result;
  }

  /**
   * Inserts an entity of kind (ex. DEVELOPER), keyed by keyString (ex. developerId or playerId),
   * and adding a property for every <String, Object> property in the Map properties.
   * The same transaction also overwrites any entity that has the same key.
   * **** Note: does not currently handle nested values in the Map! ****
   * @TODO test for nested values and create nested entities
   */
  public static long insertEntity(String kind, Map<Object, Object> properties) {
    Transaction txn = datastore.beginTransaction();

    Entity entity = new Entity(kind);
    
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      entity.setProperty((String)entry.getKey(), entry.getValue());
    }
    
    long key = datastore.put(entity).getId();
    txn.commit();    
    return key;
  }
  
  /**
   * Inserts an entity with specified keyId (used in the future for AI, etc.).
   */
  public static void insertEntity(String kind, long keyId, Map<Object, Object> properties) {
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(kind, keyId);
    
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      entity.setProperty((String)entry.getKey(), entry.getValue());
    }
    
    datastore.put(entity);
    txn.commit();    
  }
  
  /**
   * Updates an entity with specified keyId.
   */
  public static void updateEntity(String kind, long keyId, Map<Object, Object> properties) {
    insertEntity(kind, keyId, properties);    
  }
  
  /**
   * Deletes an entity (ie., a developer of kind DEVELOPER).
   */
  public static void deleteEntity(String kind, long keyId) {
    Transaction txn = datastore.beginTransaction();
    Key key = KeyFactory.createKey(kind, keyId);
    datastore.delete(key);
    txn.commit();
  }
}