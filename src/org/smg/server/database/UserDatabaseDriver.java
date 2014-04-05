package org.smg.server.database;

import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER;
import static org.smg.server.servlet.developer.DeveloperConstants.EMAIL;
import static org.smg.server.servlet.developer.DeveloperConstants.PASSWORD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smg.server.servlet.user.UserConstants.*;

import org.smg.server.util.AccessSignatureUtil;

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

public class UserDatabaseDriver {
	static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public static boolean insertUser(long userId, Map<Object, Object> properties) throws Exception{
	    Transaction txn = datastore.beginTransaction();
	    Entity entity = new Entity(USER, userId);
	    
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (((String) entry.getKey()).equals(PASSWORD)) {
					String hashedPw = AccessSignatureUtil
							.getHashedPassword((String) entry.getValue());
					entity.setProperty((String) entry.getKey(), hashedPw);
				} else {
					entity.setProperty((String) entry.getKey(), entry.getValue());
				}
			}
	    
	    List<Entity> sameEmailList = queryUserByProperty(EMAIL, (String) properties.get(EMAIL));
	    
	    // Either there is nobody else with the email, or the one with the same email is me
	    if (sameEmailList.isEmpty() || sameEmailList.get(0).getKey().getId() == userId) {
	      datastore.put(entity);
	      txn.commit();
	      return true;
	    }
	    else {
	      txn.rollback();
	      return false;
	    }  
	  }
	public static long insertUser(Map<Object, Object> properties) throws Exception {
	    long key;
	    Transaction txn = datastore.beginTransaction();
	    Entity entity = new Entity(USER);
	    
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
	    
	    if (queryUserByProperty(EMAIL, (String) properties.get(EMAIL)).isEmpty()) {
	      key = datastore.put(entity).getId();
	      txn.commit(); 
	    }
	    else {
	      key = -1;
	      txn.rollback();
	    }
	       
	    return key;
	  }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	  public static Map getUserMap(long userId) throws EntityNotFoundException {
	    try {
	      Key key = KeyFactory.createKey(USER, userId);
	      Entity entity = datastore.get(key);
	      return new HashMap(entity.getProperties());
	    }
	    catch (Exception e) {
	      throw new EntityNotFoundException(null);
	    }
	  }
	public static List<Entity> queryUserByProperty(String property, Object query)  {
	    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
	    Query q = new Query(USER).setFilter(filter);
	    List<Entity> result = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
	    return result;
	  }
	 public static boolean updateUser (long userId, Map<Object, Object> properties) throws Exception{
		    return insertUser(userId, properties);    
		  }
	 
	 public static boolean deleteUser(long userId) {
		    Transaction txn = datastore.beginTransaction();
		    try {
		      Key key = KeyFactory.createKey(USER, userId);
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
