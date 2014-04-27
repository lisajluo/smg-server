package org.smg.server.database;

import static org.smg.server.servlet.user.UserConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.EMAIL;
import static org.smg.server.servlet.developer.DeveloperConstants.PASSWORD;
import static org.smg.server.servlet.game.GameConstants.AUTHORIZED;
import static org.smg.server.servlet.game.GameConstants.DEVELOPER_LOWER;
import static org.smg.server.servlet.game.GameConstants.GAME_ID;
import static org.smg.server.servlet.game.GameConstants.GAME_META_INFO;
import static org.smg.server.servlet.game.GameConstants.PICS;
import static org.smg.server.servlet.game.GameConstants.RATING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smg.server.servlet.user.UserConstants.*;

import org.smg.server.servlet.container.ContainerConstants;
import org.smg.server.util.AccessSignatureUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserDatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory
      .getDatastoreService();
  private static List<JSONObject> parseEntityToJSON(List<Entity> entityList)
  {
	  List<JSONObject> queryResult = new ArrayList<JSONObject>();
		try {
			for (Entity result : entityList) {
				JSONObject currentQueryResult = new JSONObject();
				Map<String, Object> userInfo = new HashMap<String, Object>(
						result.getProperties());
				for (String key : userInfo.keySet()) {
					        //TODO : Only put in necessary userInfo. e.g.:Name, ID,etc
					        if (key.equals(FIRSTNAME)||key.equals(EMAIL))
							currentQueryResult.put(key, userInfo.get(key));
					}
				currentQueryResult.put(USER_ID, String.valueOf(result.getKey().getId()));
				queryResult.add(currentQueryResult);
				
				}
		    
			return queryResult;
		}
	  catch (Exception e)
	  {
		  return null;
	  }
  }
  public static List<JSONObject> getAllUser()
  {
	  List<Entity> resultAsEntity= getAllUserAsEntity();
	  List<JSONObject> jsonList = parseEntityToJSON(resultAsEntity);
	  return jsonList;
  }
  private static List<Entity> getAllUserAsEntity()
  {
	  try
      {
			Query q = new Query(USER);
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = new ArrayList<Entity>();
			for (Entity entity : pq.asIterable()) {
				result.add(entity);
			}
			return result;
		} catch (Exception e) {
			return null;
		}
  }
  public static boolean insertUser(long userId, Map<Object, Object> properties)
      throws Exception {
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(USER, userId);

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      if (((String) entry.getKey()).equals(PASSWORD)) {
        String hashedPw = AccessSignatureUtil.getHashedPassword((String) entry
            .getValue());
        entity.setProperty((String) entry.getKey(), hashedPw);
      } else {
        entity.setProperty((String) entry.getKey(), entry.getValue());
      }
    }

    List<Entity> sameEmailList = queryUserByProperty(EMAIL,
        (String) properties.get(EMAIL));

    // Either there is nobody else with the email, or the one with the same
    // email is me
    if (sameEmailList.isEmpty()
        || sameEmailList.get(0).getKey().getId() == userId) {
      datastore.put(entity);
      txn.commit();
      return true;
    } else {
      txn.rollback();
      return false;
    }
  }

  public static void updateUserAccessSignature(long userId,
      String accessSignature) throws Exception {
    Key key = KeyFactory.createKey(USER, userId);
    Transaction txn = datastore.beginTransaction();
    try {
      Entity entity = datastore.get(key);
      entity.setProperty(ACCESS_SIGNATURE, accessSignature);
      datastore.put(entity);
      txn.commit();
    } catch (Exception e) {
      throw new Exception();

    }
  }

  public static boolean insertUserWithoutPassWord(long userId,
      Map<Object, Object> properties) throws Exception {
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(USER, userId);

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {

      entity.setProperty((String) entry.getKey(), entry.getValue());

    }

    List<Entity> sameEmailList = queryUserByProperty(EMAIL,
        (String) properties.get(EMAIL));

    // Either there is nobody else with the email, or the one with the same
    // email is me
    if (sameEmailList.isEmpty()
        || sameEmailList.get(0).getKey().getId() == userId) {
      datastore.put(entity);
      txn.commit();
      return true;
    } else {
      txn.rollback();
      return false;
    }
  }

  public static long insertUser(Map<Object, Object> properties)
      throws Exception {
    long key;
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(USER);

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      if (((String) entry.getKey()).equals(PASSWORD)) {
        String hashedPw = AccessSignatureUtil.getHashedPassword((String) entry
            .getValue());
        entity.setProperty((String) entry.getKey(), hashedPw);
      } else {
        entity.setProperty((String) entry.getKey(), entry.getValue());
      }

    }

    if (queryUserByProperty(EMAIL, (String) properties.get(EMAIL)).isEmpty()) {
      key = datastore.put(entity).getId();
      txn.commit();
    } else {
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
    } catch (Exception e) {
      throw new EntityNotFoundException(null);
    }
  }

  public static List<Entity> queryUserByProperty(String property, Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(USER).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());
    return result;
  }

  public static boolean updateUser(long userId, Map<Object, Object> properties)
      throws Exception {
    return insertUser(userId, properties);
  }

  public static boolean updateUserWithoutPassWord(long userId,
      Map<Object, Object> properties) throws Exception {
    return insertUserWithoutPassWord(userId, properties);
  }

  public static boolean deleteUser(long userId) {
    Transaction txn = datastore.beginTransaction();
    try {
      Key key = KeyFactory.createKey(USER, userId);
      datastore.delete(key);
      txn.commit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

}
