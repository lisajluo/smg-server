package org.smg.server.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.ContainerConstants;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * Container manage two Entities: 
 * 1) Match, properties other than Key and Id:
 * gameId: long
 * playerIds: unindexed String ("[123,345]")
 * playerThatHasTurn: long
 * gameOverScores: unindexed String ("{"123":1,"345":0}")
 * gameOverReason: String
 * playerIdToNumberOfTokensInPot: unindexed String ("{"123":500,"345":0}")
 * history: unindexed String (
 * "[{"gameState":{
 *       "state":{...},
 *       "visibleTo":{...},
 *       "playerIdToNumberOfTokensInPot":{...}},
 *    "lastMove":[...]}]")
 *    
 * Each player can only automatch for one game,
 * 2) Queue, properties other than Key and Id(playerId):
 * gameId: long
 * channelToken: String
 * enqueueTime: Date
 * 
 * @author piper
 *
 */

public class ContainerDatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  /**
   * Get match entity by matchId
   * @param kind
   * @param keyId
   * @return
   */
  public static Entity getEntityByKey(String kind, long keyId) {
    Key key = KeyFactory.createKey(kind, keyId);
    Entity entity = null;    
    try {
      entity = datastore.get(key);
    } catch (EntityNotFoundException e) { 
      return entity; 
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
   * Returns a list of entities given a query by kind ("Queue"), property ("gameId"),
   * query (gameId:long), sortBy ("enqueuTime"), in ascending order
   * @param kind
   * @param property
   * @param query
   * @param sortBy
   * @return
   */
  public static List<Entity> queryByPropertySorted(String kind, String property,
      Object query, String sortBy) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter).addSort(sortBy);
    List<Entity> result = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());
    return result;
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
   * Insert a new match
   * match = { 
   *  "gameId": ...,
   *  "playerIds": [...],
   *  "playerThatHasTurn":[],
   *  "gameOverScores": { },
   *  "gameOverReason": "...",
   *  "numberOfTokensInPot": {...}
   *  "history": []        
   * @param match
   * @return
   * @throws JSONException
   */
  public static long insertMatchEntity(JSONObject match) throws JSONException {
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(ContainerConstants.MATCH);
    entity.setProperty(ContainerConstants.GAME_ID, match.getLong(ContainerConstants.GAME_ID));
    entity.setUnindexedProperty(ContainerConstants.PLAYER_IDS,
        match.getJSONArray(ContainerConstants.PLAYER_IDS).toString());
    entity.setProperty(ContainerConstants.PLAYER_THAT_HAS_TURN, 
        match.getLong(ContainerConstants.PLAYER_THAT_HAS_TURN));
    entity.setUnindexedProperty(ContainerConstants.GAME_OVER_SCORES, 
        match.getJSONObject(ContainerConstants.GAME_OVER_SCORES).toString());
    entity.setProperty(ContainerConstants.GAME_OVER_REASON, 
        match.getString(ContainerConstants.GAME_OVER_REASON));
    Text history = new Text(match.getJSONArray(ContainerConstants.HISTORY).toString());
    entity.setUnindexedProperty(ContainerConstants.HISTORY, history);
    datastore.put(entity);
    txn.commit();
    return entity.getKey().getId();
  }
  
  /**
   * Update Match entity after making a move, 
   * require matchId:long and a map<String,Object> contains following field:
   * {"playerThatHasTurn":long,
   * "history": ArrayList<Object>,
   * "gameOverReason": String // only when game end
   * "gameOverScores": Map<String,Integer> // only when game end
   * "playerIdToNumberOfTokensInPot": Map<String,Integer>
   * }
   * @param matchId
   * @param match
   * @return true: update, false: exception happened
   * @throws IOException 
   */
  public static boolean updateMatchEntity(long matchId, Map<String, Object> match) throws IOException {
    Transaction txn = datastore.beginTransaction();
    // TODO playerIdToNumberOfTokensInPot field optional or not
    if (!match.containsKey(ContainerConstants.HISTORY)
        || !match.containsKey(ContainerConstants.PLAYER_THAT_HAS_TURN)) {
      return false;
    }
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
      Text history = new Text(jsonArray.toString());
      entity.setUnindexedProperty(ContainerConstants.HISTORY, history);
    } catch (JSONException e) {
      return false;
    }
    
    if (match.containsKey(ContainerConstants.GAME_OVER_REASON)) {
      try {
        entity.setProperty(ContainerConstants.GAME_OVER_REASON,
            jsonObj.getString(ContainerConstants.GAME_OVER_REASON));
      } catch (JSONException e) {
        return false;
      }
    }
    
    if (match.containsKey(ContainerConstants.GAME_OVER_SCORES)) {
      try {
        @SuppressWarnings("unchecked")
        Map<String,Object> map = (Map<String,Object>)jsonObj.get(ContainerConstants.GAME_OVER_SCORES);
        JSONObject jsonScore = new JSONObject(map);
        entity.setProperty(ContainerConstants.GAME_OVER_SCORES, jsonScore.toString());
      } catch (JSONException e) {
        return false;
      }
    }
    
    if (match.containsKey(ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT)) {
      try {
        if(match.get(
            ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT) != null) {
          @SuppressWarnings("unchecked")
          Map<String,Object> map = (Map<String,Object>)jsonObj.get(
              ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT);
          JSONObject jsonToken = new JSONObject(map);
          entity.setProperty(ContainerConstants.GAME_OVER_SCORES, jsonToken.toString());
        }        
      } catch (JSONException e) {
        return false;
      }
    }
    datastore.put(entity);
    txn.commit();
    return true;
  }
  
  /**
   * Get all unfinished match entity list by gameId
   * If gameId <= 0 or not found, return empty list
   * @param gameId
   * @return
   */
  public static List<Entity> getAllUnfinishedMatchesByGameID (long gameId) {
    if (gameId <= 0) {
      return new ArrayList<Entity>();
    }
    Filter filter = new FilterPredicate(ContainerConstants.GAME_ID, FilterOperator.EQUAL, gameId);
    Filter filter2 = new FilterPredicate(
        ContainerConstants.GAME_OVER_REASON, FilterOperator.EQUAL, ContainerConstants.NOT_OVER);
    Query q = new Query(ContainerConstants.MATCH).setFilter(filter).setFilter(filter2);
    List<Entity> result = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());
    return result;
  }
  
  /**
   * Get all match entity by playerId, return empty list if not found or invalid id.
   * @param playerId
   * @return
   */
  public static List<Entity> getAllMatchesByPlayerId(long playerId) {
    List<Entity> result = new ArrayList<Entity>();
    if (playerId <= 0) {
      return result;
    }
    Query q = new Query(ContainerConstants.MATCH);
    List<Entity> raw = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());
    for (Entity entity: raw) {
      List<String> playerIds = new LinkedList<String>();
      try {
        playerIds = JSONUtil.parsePlayerIds(
            (String)entity.getProperty(ContainerConstants.PLAYER_IDS));
      } catch (Exception e) {
        return result;
      }
      for (String id : playerIds) {
        if (id.equals(String.valueOf(playerId))) {
          result.add(entity);
          break;
        }
      }
    }
    return result;
  }
  /**
   * Get unfinished match by playerId, only allow one player has one unfinished game
   * @param playerId
   * @return entity or null if not found
   */
  public static Entity getUnfinishedMatchByPlayerId(long playerId) {
    Filter filter = new FilterPredicate(
        ContainerConstants.GAME_OVER_REASON, FilterOperator.EQUAL, ContainerConstants.NOT_OVER);
    Query q = new Query(ContainerConstants.MATCH).setFilter(filter);
    List<Entity> raw = datastore.prepare(q).asList(
        FetchOptions.Builder.withDefaults());
    for (Entity entity: raw) {
      List<Long> playerIds = new ArrayList<Long>();
      try {
        playerIds = JSONUtil.parseDSPlayerIds(
            (String)entity.getProperty(ContainerConstants.PLAYER_IDS));
      } catch (Exception e) {
      }
      for (long id : playerIds) {
        if (id == playerId) {
          return entity;
        }
      }
    }
    return null;
  }
  
  /**
   * Insert a player to queue: need a map<String,Object> contains following fields:
   * {"gameId":long, "playerId":long, "channelToken": String}
   * @param queueEntity
   * @return true:insert succeed, false: insert fail
   * if insert failed, return "ENQUEUE_FAILED" error to the client
   */
  public static boolean insertQueueEntity(Map<String,Object> queueEntity) {
    if ( !queueEntity.containsKey(ContainerConstants.GAME_ID) 
        || !queueEntity.containsKey(ContainerConstants.PLAYER_ID)
        || !queueEntity.containsKey(ContainerConstants.CHANNEL_TOKEN)) {
      return false;
    }
    // if a playerId is already in a game waiting queue
    long playerId = (long)queueEntity.get(ContainerConstants.PLAYER_ID);
    if ( getEntityByKey(ContainerConstants.QUEUE,playerId) != null) {
      return false;
    }
    Transaction txn = datastore.beginTransaction();
    Entity entity = new Entity(ContainerConstants.QUEUE,playerId);
//    entity.setProperty(ContainerConstants.PLAYER_ID,
//        queueEntity.get(ContainerConstants.PLAYER_ID));
    entity.setProperty(ContainerConstants.GAME_ID,
        queueEntity.get(ContainerConstants.GAME_ID));
    entity.setProperty(ContainerConstants.CHANNEL_TOKEN,
        queueEntity.get(ContainerConstants.CHANNEL_TOKEN));
    entity.setProperty(ContainerConstants.ENQUEUE_TIME, new Date());
    datastore.put(entity);
    txn.commit();
    return true;
  }
  
  /**
   * Get numbers in waiting queue for one game
   * @param gameId
   * @return
   */
  public static int getPlayerNumberInQueue (long gameId) {
    return queryByProperty(ContainerConstants.QUEUE,ContainerConstants.GAME_ID,gameId).size();
  }
  
  /**
   * Get a list of entities of the queue by
   * @param gameId
   * @param playersNeeded
   * @return empty list if playersNeeded is more than players in queue
   */
  public static List<Entity> getPlayersInQueue (long gameId, int playersNeeded) {
    List<Entity> entities = queryByPropertySorted(ContainerConstants.QUEUE,
        ContainerConstants.GAME_ID,gameId,ContainerConstants.ENQUEUE_TIME);
    if (entities.isEmpty() || entities.size() < playersNeeded) {
      return new ArrayList<Entity>();
    }
    return entities.subList(0, playersNeeded);
  }
  
  /**
   * Get a player out of waiting queue for one game
   * @param playerId
   * @return
   */
  public static boolean deleteQueueEntity (long playerId) {
    deleteEntity(ContainerConstants.QUEUE,playerId);
    return true;
  }
}
