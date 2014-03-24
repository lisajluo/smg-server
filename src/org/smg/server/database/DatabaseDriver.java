package org.smg.server.database;


import org.smg.server.servlet.developer.DeveloperConstants;
import org.smg.server.servlet.container.ContainerConstants;
import org.smg.server.util.JSONUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import java.util.Arrays;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.util.AccessSignatureUtil;

import com.google.appengine.api.datastore.PreparedQuery;

public class DatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
//MODEL NAME DEFINE
 private static final String PLAYER = "PLAYER";

 // PLAYER MODEL MODIFY BLACKLIST
 private static List<String> playerPropertyBlackList = Arrays.asList(
     PlayerProperty.EMAIL.toString(), PlayerProperty.PLAYERID.toString());

 private static Object insertPlayerMutex = new Object();
 // TODO: Use Specific Mutex for each player login
 private static Object loginMutex = new Object();

 public static int test() {
   return 0;
 }

 /**
  * save player's information to database (insert or update) If it is to insert
  * a profile, playerId should not be given return
  * "SUCCESS:"+playerId+":"+accessSignature if insert success return
  * "EMAIL_EXITSTS" if email already existed
  * 
  * If it is to update profile, playerId should be given in {@code player}
  * return "UPDATED_PLAYER" if updated success return "WRONG_ACCESS_SIGNATURE"
  * if update fail due to accessSignature authentication fail. return
  * "WRONG_PLAYER_ID" if update fail due to no such id found.
  * 
  * @param player
  * @return
  */
 public static String savePlayer(Player player) {
   if (player.getAllProperties().containsKey(
       PlayerProperty.PLAYERID.toString())) {
     // Update profile
     return updatePlayer(player);
   } else {
     // Insert profile, should use a mutex
     return insertPlayer(player);
   }
 }

 private static String updatePlayer(Player player) {
   Map<String, String> playerInfo = player.getAllProperties();
   long playerId;
   try {
     // PlayerId should be convert to long
     playerId = Long.parseLong(playerInfo.get(PlayerProperty.PLAYERID
         .toString()));
   } catch (NumberFormatException e1) {
     return "WRONG_PLAYER_ID";
   }
   Key playerKey = KeyFactory.createKey(PLAYER, playerId);
   try {
     Entity playerDB = datastore.get(playerKey);
     String as = (String) playerDB.getProperty(PlayerProperty.ACCESSSIGNATURE
         .toString());
     String asLocal = playerInfo.containsKey(PlayerProperty.ACCESSSIGNATURE
         .toString()) ? playerInfo.get(PlayerProperty.ACCESSSIGNATURE
         .toString()) : "";
     if (asLocal != "" && as.equals(asLocal)) {
       for (String key : playerInfo.keySet()) {
         if (playerPropertyBlackList.contains(key)) {
           continue;
         }
         playerDB.setProperty(key, playerInfo.get(key));
       }
       datastore.put(playerDB);
       return "UPDATED_PLAYER";
     } else {
       return "WRONG_ACCESS_SIGNATURE";
     }
   } catch (EntityNotFoundException e) {
     return "WRONG_PLAYER_ID";
   }
 }

 private static String insertPlayer(Player player) {
   Map<String, String> playerInfo = player.getAllProperties();
   Entity playerDB = new Entity(PLAYER);
   for (String key : playerInfo.keySet()) {
     playerDB.setProperty(key, playerInfo.get(key));
   }
   long playerId;
   final String EMAIL = PlayerProperty.EMAIL.toString();
   Filter emailFilter = new FilterPredicate(EMAIL, Query.FilterOperator.EQUAL,
       playerInfo.get(EMAIL));
   Query q = new Query(PLAYER).setFilter(emailFilter);
   FetchOptions fo = FetchOptions.Builder.withChunkSize(1);
   synchronized (insertPlayerMutex) {
     PreparedQuery pq = datastore.prepare(q);
     if (pq.countEntities(fo) > 0) {
       return "EMAIL_EXITSTS";
     }
     Key playerKey = datastore.put(playerDB);
     playerId = playerKey.getId();

     String accessSignature = AccessSignatureUtil.getAccessSignature(String
         .valueOf(playerId));
     playerDB.setProperty(PlayerProperty.ACCESSSIGNATURE.toString(),
         accessSignature);
     datastore.put(playerDB);
     return "SUCCESS:" + playerId + ":" + accessSignature;
   }
 }

 /**
  * get a played by its id. If player is not exist, throw
  * EntityNotFoundException.
  * 
  * @param playerId
  * @return
  * @throws EntityNotFoundException
  */
 public static Player getPlayerById(long playerId)
     throws EntityNotFoundException {
   Player player = new Player();
   Key playerKey = KeyFactory.createKey(PLAYER, playerId);
   Entity playerDB = datastore.get(playerKey);
   Map<String, Object> properties = playerDB.getProperties();
   for (String key : properties.keySet()) {
     PlayerProperty p = PlayerProperty.findByValue(key);
     if (p != null && p != PlayerProperty.HASHEDPASSWORD) {
       player.setProperty(p, (String) properties.get(key));
     }
   }
   player.setProperty(PlayerProperty.PLAYERID, String.valueOf(playerId));
   return player;
 }

 /**
  * login a player by id and password If login success, update access signature
  * and return If login fail, throw EntityNotFoundException for WRONG_PLAYER_ID
  * 
  * @param playerId
  * @param originalPassword
  * @return {@code String [] where result[0]  = email and result[1] = accessSignature}
  *         if login success or "WRONG_PASSWORD" if password incorrect
  * @throws EntityNotFoundException
  *           if WRONG_PLAYER_ID
  */
 public static String[] loginPlayer(long playerId, String originalPassword)
     throws EntityNotFoundException {
   if (originalPassword == null || originalPassword.length() == 0) {
     return new String[] { "WRONG_PASSWORD" };
   }
   Key playerKey = KeyFactory.createKey(PLAYER, playerId);
   synchronized (loginMutex) {
     Entity playerDB = datastore.get(playerKey);
     String hashedPassword = AccessSignatureUtil
         .getHashedPassword(originalPassword);
     if (playerDB.getProperty(PlayerProperty.HASHEDPASSWORD.toString())
         .equals(hashedPassword)) {

       String accessSignature = AccessSignatureUtil.getAccessSignature(String
           .valueOf(playerId));
       String email = (String) playerDB.getProperty(PlayerProperty.EMAIL
           .toString());
       playerDB.setProperty(PlayerProperty.ACCESSSIGNATURE.toString(),
           accessSignature);
       datastore.put(playerDB);
       return new String[] { email, accessSignature };

     } else {
       return new String[] { "WRONG_PASSWORD" };
     }
   }
 }

 /**
  * Delete a player by id and access signature If delete success, return
  * DELETE_PLAYER If delete failed due to wrong access signature return
  * WRONG_ACCESS_SIGNATURE
  * 
  * @param playerId
  * @param accessSignature
  * @return
  * @throws EntityNotFoundException
  *           if WRONG_PLAYER_ID
  */
 public static String deletePlayer(long playerId, String accessSignature)
     throws EntityNotFoundException {
   Key playerKey = KeyFactory.createKey(PLAYER, playerId);
   synchronized (loginMutex) {
     Entity playerDB = datastore.get(playerKey);
     if (playerDB.getProperty(PlayerProperty.ACCESSSIGNATURE.toString())
         .equals(accessSignature)) {
       datastore.delete(playerKey);
       return "DELETED_PLAYER";
     } else {
       return "WRONG_ACCESS_SIGNATURE";
     }
   }
 }
  
  
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
   * Returns developer map keyed from developerId, in the form of a (copied) Map.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Map getDeveloperMapByKey(long keyId) {
    Entity entity = getEntityByKey(DeveloperConstants.DEVELOPER, keyId);
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
  public static void deleteEntity(String kind, long keyId) {
    Transaction txn = datastore.beginTransaction();
    Key key = KeyFactory.createKey(kind, keyId);
    datastore.delete(key);
    txn.commit();
  }

  /**
   * Insert a new match to datastore
   * @param match
   * @return matchId
   * @throws JSONException
   */
  public static long insertMatchEntity(JSONObject match) throws JSONException {
    Transaction txn = datastore.beginTransaction();
    //Key key = KeyFactory.createKey("Match", match.getInt("matchId"));
    Entity entity = new Entity(ContainerConstants.MATCH);
    //entity.setProperty("matchId", match.getInt("matchId"));
    entity.setProperty(ContainerConstants.GAME_ID, match.getLong(ContainerConstants.GAME_ID));
    entity.setUnindexedProperty(ContainerConstants.PLAYER_IDS,
        match.getJSONArray(ContainerConstants.PLAYER_IDS).toString());
    entity.setProperty(ContainerConstants.PLAYER_THAT_HAS_TURN, 
        match.getLong(ContainerConstants.PLAYER_THAT_HAS_TURN));
    entity.setUnindexedProperty(ContainerConstants.GAME_OVER_SCORES, 
        match.getJSONObject(ContainerConstants.GAME_OVER_SCORES).toString());
    entity.setProperty(ContainerConstants.GAME_OVER_REASON, 
        match.getString(ContainerConstants.GAME_OVER_REASON));
    entity.setUnindexedProperty(ContainerConstants.HISTORY, 
        match.getJSONArray(ContainerConstants.HISTORY).toString());
    datastore.put(entity);
    txn.commit();
    return entity.getKey().getId();
  }

  /**
   * Update Match entity after making a move
   * @param matchId
   * @param match
   * @return true: update, false: exception happened
   * @throws IOException 
   */
  public static boolean updateMatchEntity(long matchId, Map<String, Object> match) throws IOException {
    Transaction txn = datastore.beginTransaction();
    String json = new JSONObject(match).toString();
    Map<String,Object> jsonMap = JSONUtil.parse(json);
    JSONObject jsonObj = new JSONObject(jsonMap);
    
    Key key = KeyFactory.createKey(ContainerConstants.MATCH, matchId);
    Entity entity;
    try {
      entity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return false;
    }
    try {
      //Currently make move affects two properties change 
      entity.setProperty(ContainerConstants.PLAYER_THAT_HAS_TURN, 
          jsonObj.getLong(ContainerConstants.PLAYER_THAT_HAS_TURN));
      @SuppressWarnings("unchecked")
      ArrayList<Object> list = (ArrayList<Object>)jsonObj.get(ContainerConstants.HISTORY);
      JSONArray jsonArray = new JSONArray(list.toArray());      
      entity.setUnindexedProperty(ContainerConstants.HISTORY, 
          jsonArray.toString());
    } catch (JSONException e) {
      return false;
    }
    datastore.put(entity);
    txn.commit();
    return true;
  }
}

