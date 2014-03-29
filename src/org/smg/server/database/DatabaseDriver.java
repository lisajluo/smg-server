package org.smg.server.database;

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
  
  static public long  saveGameMetaInfo(HttpServletRequest req) throws IOException
  {
    
      Key versionKey=KeyFactory.createKey("Version", "versionOne");
      Date date=new Date();
      Entity game=new Entity("gameMetaInfo",versionKey);
      game.setProperty("version","versionOne");
      game.setProperty("postDate", date);
      game.setProperty("gameName", req.getParameter("gameName"));
      game.setProperty("description", req.getParameter("description"));
      game.setProperty("url", req.getParameter("url"));
      game.setProperty("width", req.getParameter("width"));
      game.setProperty("height", req.getParameter("height"));
      String picInfo=req.getParameter("pic");
      if (picInfo!=null)
      {
      Map<String, Object> jObj = JSONUtil.parse(picInfo);
        game.setProperty("icon",jObj.get("icon"));
        ArrayList<String> screenshot=(ArrayList<String>) (jObj.get("screenshots"));
      
        game.setProperty("screenshots", screenshot);
      }

      List<String> developerList=new ArrayList<String> ();
      developerList.add(req.getParameter("developerId"));
      game.setProperty("developerId", developerList);
    //  game.setProperty("gameId", gameId);
      datastore.put(game);
      long gameId = game.getKey().getId();
      Key queryKey = KeyFactory.createKey(versionKey,"gameMetaInfo",gameId);
      
      return gameId;
    
    
  }
  static public Entity getEntity(String gameId,String versionNum)
  {
    try 
    {
      Key versionKey=KeyFactory.createKey("Version", versionNum);
      long ID = Long.parseLong(gameId);
      Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
      return datastore.get(gameKey);
    }
    catch (Exception e)
    {
      return null;
    }
  }
  static public boolean checkGameNameDuplicate(HttpServletRequest req)
  {
    try
    {
      String versionNum="versionOne";
      Key versionKey=KeyFactory.createKey("Version", versionNum);
      Filter nameFilter =new FilterPredicate("gameName",FilterOperator.EQUAL,req.getParameter("gameName"));
      Query q = new Query("gameMetaInfo").setFilter(nameFilter);
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
  static public boolean checkGameNameDuplicate(long gameId,HttpServletRequest req)
  {
    Key versionKey=KeyFactory.createKey("Version", "versionOne");
    Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", gameId);
    try
    {
       Entity game = datastore.get(gameKey);
       if (game.getProperty("gameName").equals(req.getParameter("gameName")))
         return false;
       else
         return checkGameNameDuplicate(req);
    }
    catch (Exception e)
    {
      return false;
    }
  }
  static public boolean checkGameNameDuplicate(String gameName,HttpServletRequest req)
  {
    try
    {
      String versionNum="versionOne";
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
      
  }
  static public boolean checkIdExists(String gameId)
  {
    try
    {
      String versionNum="versionOne";
      Key versionKey=KeyFactory.createKey("Version", versionNum);
      long ID = Long.parseLong(gameId);
      //System.out.println(ID);
      Key idKey= KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
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
  
  static public void delete(String gameId,String versionNum)
  {
    Key versionKey=KeyFactory.createKey("Version", versionNum);
    long ID = Long.parseLong(gameId);
    Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
    datastore.delete(gameKey);
  }
  static public void update(String gameId,HttpServletRequest req) throws EntityNotFoundException, IOException
  {
    Key versionKey=KeyFactory.createKey("Version", "versionOne");
    long ID = Long.parseLong(gameId);
    Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
    Entity target = datastore.get(gameKey);
    
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
        
    }
      
    datastore.put(target);
    
  }
}
