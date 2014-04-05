package org.smg.server.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.util.AccessSignatureUtil;

import static org.smg.server.servlet.developer.DeveloperConstants.*;

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
   * Returns Map of Developer properties or throws exception if not found.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Map getDeveloperMap(long developerId) throws EntityNotFoundException {
    try {
      Key key = KeyFactory.createKey(DEVELOPER, developerId);
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
  public static List<Entity> queryDeveloperByProperty(String property, Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(DEVELOPER).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

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
    Entity entity = new Entity(DEVELOPER);
    
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
     if (((String) entry.getKey()).equals(PASSWORD)) {
    	     String hashedPw = AccessSignatureUtil.getHashedPassword((String)entry.getValue());
    	     entity.setProperty((String) entry.getKey(),  hashedPw);
     }
     else
     {
      entity.setProperty((String) entry.getKey(), entry.getValue());
     }
      
    }
    
    if (queryDeveloperByProperty(EMAIL, (String) properties.get(EMAIL)).isEmpty()) {
      key = datastore.put(entity).getId();
      txn.commit(); 
    }
    else {
      key = -1;
      txn.rollback();
    }
       
    return key;
  }
  
  /**
   * Inserts an entity with specified keyId (used in the future for AI, etc.).
   */
  public static boolean insertDeveloper(long developerId, Map<Object, Object> properties) {
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(DEVELOPER, developerId);
    
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			if (((String) entry.getKey()).equals(PASSWORD)) {
				String hashedPw = AccessSignatureUtil
						.getHashedPassword((String) entry.getValue());
				entity.setProperty((String) entry.getKey(), hashedPw);
			} else {
				entity.setProperty((String) entry.getKey(), entry.getValue());
			}
		}
    
    List<Entity> sameEmailList = queryDeveloperByProperty(EMAIL, (String) properties.get(EMAIL));
    
    // Either there is nobody else with the email, or the one with the same email is me
    if (sameEmailList.isEmpty() || sameEmailList.get(0).getKey().getId() == developerId) {
      datastore.put(entity);
      txn.commit();
      return true;
    }
    else {
      txn.rollback();
      return false;
    }  
  }
  
  /**
   * Updates a (non-nested) developer entity with specified keyId.
   */
  public static boolean updateDeveloper(long developerId, Map<Object, Object> properties) {
    return insertDeveloper(developerId, properties);    
  }
  
  /**
   * Deletes an entity (ie., a developer of kind DEVELOPER).
   */
  public static boolean deleteDeveloper(long developerId) {
    Transaction txn = datastore.beginTransaction();
    try {
      Key key = KeyFactory.createKey(DEVELOPER, developerId);
      datastore.delete(key);
      txn.commit();
      return true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  @SuppressWarnings("rawtypes")
  public static boolean verifyDeveloperAccess(long developerId, String accessSignature) 
      throws EntityNotFoundException {
    
    try {
      Map developerMap = getDeveloperMap(developerId);
      if (developerMap.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        return true;
      }
      return false;
    }
    catch (EntityNotFoundException e) {
      throw e;
    }
  }
  
}
