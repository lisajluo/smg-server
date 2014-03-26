package org.smg.server.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smg.server.servlet.developer.DeveloperConstants;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class DeveloperDatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  /**
   * Returns Map of Developer properties or null
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Map getDeveloperMap(long keyId) throws EntityNotFoundException {
    try {
      Key key = KeyFactory.createKey(DeveloperConstants.DEVELOPER, keyId);
      Entity entity = datastore.get(key);
      return new HashMap(entity.getProperties());
    }
    catch (Exception e) {
      throw new EntityNotFoundException(null);
    }
  }
  

  /**
   * Returns a list of entities given a query by kind (ex: DEVELOPER), property (ex: EMAIL),
   * and the query (ex: "foo@bar.com").
   */
  public static List<Entity> queryByProperty(String kind, String property,
      Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());

    return result;
  }

  /**
   * Inserts a developer, keyed by developerId, and adding a property for every <String, Object> 
   * property in the Map properties. The same transaction also overwrites any entity that has 
   * the same key. Flat (non-nested) maps only.
   */
  public static long insertDeveloper(Map<Object, Object> properties) {
    long key;
    Transaction txn = datastore.beginTransaction();

    Entity entity = new Entity(DeveloperConstants.DEVELOPER);
    
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      entity.setProperty((String)entry.getKey(), entry.getValue());
    }
    
    if (queryByProperty(DeveloperConstants.DEVELOPER, DeveloperConstants.EMAIL, 
        (String) properties.get(DeveloperConstants.EMAIL)).isEmpty()) {
      key = datastore.put(entity).getId();
    }
    else {
      key = -1;
    }
    
    txn.commit();    
    return key;
  }
  
  /**
   * Inserts an entity with specified keyId (used in the future for AI, etc.).
   */
  public static void insertDeveloper(long keyId, Map<Object, Object> properties) {
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(DeveloperConstants.DEVELOPER, keyId);
    
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      entity.setProperty((String)entry.getKey(), entry.getValue());
    }
    
    datastore.put(entity);
    txn.commit();    
  }
  
  /**
   * Updates a (non-nested) developer entity with specified keyId.
   */
  public static void updateDeveloper(long keyId, Map<Object, Object> properties) {
    insertDeveloper(keyId, properties);    
  }
  
  /**
   * Deletes an entity (ie., a developer of kind DEVELOPER).
   */
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
