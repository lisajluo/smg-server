package org.smg.server.database;


import static org.smg.server.servlet.game.GameConstants.*;
import org.smg.server.servlet.developer.DeveloperConstants;
import org.smg.server.servlet.container.ContainerConstants;
import org.smg.server.util.JSONUtil;
import org.smg.server.util.AccessSignatureUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

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
import com.google.appengine.api.datastore.PreparedQuery;


import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONException;


import javax.servlet.http.HttpServletRequest;

import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;





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
  
  static public long  saveGameMetaInfo(Map<Object,Object> parameterMap) throws IOException
  {
    
      Date date=new Date();

      Entity game=new Entity(GAME_META_INFO);
      game.setProperty(POST_DATE, date);
      for (Object key : parameterMap.keySet())
      {
    	  String keyStr=(String) key;
    	  if (keyStr.equals(ACCESS_SIGNATURE)==false)
    	  game.setProperty((String)key, parameterMap.get(key));
      }
      

      List<String> developerList=new ArrayList<String> ();
      developerList.add((String)parameterMap.get(DEVELOPER_ID));
      game.setProperty(DEVELOPER_ID, developerList);

      datastore.put(game);
      long gameId = game.getKey().getId();
      //Key queryKey = KeyFactory.createKey(versionKey,"gameMetaInfo",gameId);
      
      return gameId;
    
    
  }
  static public Entity getEntity(String gameId)
  {
    try 
    {
    //  Key versionKey=KeyFactory.createKey("Version", versionNum);
      long ID = Long.parseLong(gameId);
     // Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
      Key gameKey=KeyFactory.createKey( GAME_META_INFO, ID);
      return datastore.get(gameKey);
    }
    catch (Exception e)
    {
      return null;
    }
  }
  static public boolean checkGameNameDuplicate(Map<Object,Object> parameterMap)
  {
    try
    {

      //String versionNum="versionOne";
      //Key versionKey=KeyFactory.createKey("Version", versionNum);
      Filter nameFilter =new FilterPredicate(GAME_NAME,FilterOperator.EQUAL,parameterMap.get(GAME_NAME));
      Query q = new Query(GAME_META_INFO).setFilter(nameFilter);
      PreparedQuery pq = datastore.prepare(q);
      if (pq.countEntities()>0)
        return true;
      else
        return false;
    }
    catch (Exception e)
    {
      return false;
    }
      
  }
  static public boolean checkGameNameDuplicate(long gameId,Map<Object,Object> parameterMap)
  {
    Key gameKey=KeyFactory.createKey( GAME_META_INFO, gameId);
    try
    {
       Entity game = datastore.get(gameKey);

       if (game.getProperty(GAME_NAME).equals(parameterMap.get(GAME_NAME)))

         return false;
       else
         return checkGameNameDuplicate(parameterMap);
    }
    catch (Exception e)
    {
      return false;
    }
  }
 /*static public boolean checkGameNameDuplicate(String gameName,HttpServletRequest req)
  {
    try
    {
      String versionNum=VERSION_ONE;
      Key versionKey=KeyFactory.createKey("Version", versionNum);
      Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo",gameName);
      try
      {
        datastore.get(gameKey);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    }
    catch (Exception e)
    {
      return false;
    }
      
  }*/
  static public boolean checkIdExists(String gameId)
  {
    try
    {
    //  String versionNum="versionOne";
   //   Key versionKey=KeyFactory.createKey("Version");
      long ID = Long.parseLong(gameId);
      //System.out.println(ID);
      Key idKey= KeyFactory.createKey(GAME_META_INFO, ID);
      try 
      {
        datastore.get(idKey);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    }
    catch (Exception e)
    {
      return false;
    }
  }
  
  static public void delete(String gameId)
  {
   // Key versionKey=KeyFactory.createKey("Version", versionNum);
    long ID = Long.parseLong(gameId);
   // Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
    Key gameKey=KeyFactory.createKey( GAME_META_INFO, ID);
    datastore.delete(gameKey);
  }
  static public void update(String gameId,Map<Object,Object> parameterMap) throws EntityNotFoundException, IOException
  {
   // Key versionKey=KeyFactory.createKey("Version", "versionOne");
    long ID = Long.parseLong(gameId);
 //   Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
    Key gameKey=KeyFactory.createKey( GAME_META_INFO, ID);
    Entity target = datastore.get(gameKey);
    for (Object key : parameterMap.keySet())
    {
      String keyStr = (String) key;

      if (keyStr.equals(ACCESS_SIGNATURE)==false&&keyStr.equals(DEVELOPER_ID)==false&&keyStr.equals(GAME_ID)==false)

  	  target.setProperty((String)key, parameterMap.get(key));
    }
    /*
    if (req.getParameter("gameName")!=null)
      target.setProperty("gameName", req.getParameter("gameName"));
    if (req.getParameter("description")!=null)
      target.setProperty("description", req.getParameter("description"));
    if (req.getParameter("url")!=null)
      target.setProperty("url", req.getParameter("url"));
    if (req.getParameter("width")!=null)
      target.setProperty("width", req.getParameter("width"));
    if (req.getParameter("height")!=null)
      target.setProperty("height", req.getParameter("height"));
    if (req.getParameter("pic")!=null)
    {
      Map<String, Object> jObj=JSONUtil.parse(req.getParameter("pic"));
      if (jObj.get("icon")!=null)
          target.setProperty("icon",jObj.get("icon"));
      if (jObj.get("screenshots")!=null)
      {
        ArrayList<String> screenshot =(ArrayList<String>) (jObj.get("screenshots"));
          target.setProperty("screenshots",screenshot);
          
      }
        
    }*/
      
    datastore.put(target);
    
  }
}
