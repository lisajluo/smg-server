package org.smg.server.database;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.FIRST_NAME;
import static org.smg.server.servlet.developer.DeveloperConstants.NICK_NAME;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import org.smg.server.util.JSONUtil;

public class GameDatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  public static Entity getGame(long gameId) throws EntityNotFoundException {
    try {
      Key gameKey = KeyFactory.createKey(GAME_META_INFO, gameId);
      return datastore.get(gameKey);
    } catch (Exception e) {
      throw new EntityNotFoundException(null);
    }
  }

  static public boolean checkGameNameDuplicate(Map<Object, Object> parameterMap) {
    try {
      Filter nameFilter = new FilterPredicate(GAME_NAME, FilterOperator.EQUAL,
          parameterMap.get(GAME_NAME));
      Query q = new Query(GAME_META_INFO).setFilter(nameFilter);
      PreparedQuery pq = datastore.prepare(q);
      if (pq.countEntities(FetchOptions.Builder.withDefaults()) > 0) {
        return true;
      }
      return false;
    }
    catch (Exception e) {
      return false;
    }
  }

  public static boolean checkGameIdExists(long gameId) throws EntityNotFoundException {
    try {
      Key idKey = KeyFactory.createKey(GAME_META_INFO, gameId);
      try {
        datastore.get(idKey);
        return true;
      } catch (Exception e) {
        throw new EntityNotFoundException(idKey);
      }
    } catch (Exception e) {
      return false;
    }
  }

  static public long saveGameMetaInfo(Map<Object, Object> parameterMap) throws IOException {
    Date date = new Date();

    Entity game = new Entity(GAME_META_INFO);
    game.setProperty(POST_DATE, date);
    for (Object key : parameterMap.keySet()) {
      String keyStr = (String) key;
      if (keyStr.equals(ACCESS_SIGNATURE) == false)
      {
    	if (keyStr.equals(PICS)==false)
          game.setProperty((String) key, parameterMap.get(key));
    	else
    	{
    		Map<Object,Object> picObj = (Map<Object,Object>)(parameterMap.get(key));
    		Text picText = new Text(picObj.toString());
    		game.setProperty((String) key,picText);

    	}
      }
    }
    game.setProperty(PASS_CENSOR, false);
    game.setProperty(UPDATED, true);
    List<String> developerList = new ArrayList<String>();
    developerList.add((String) parameterMap.get(DEVELOPER_ID));
    game.setProperty(DEVELOPER_ID, developerList);

    datastore.put(game);
    long gameId = game.getKey().getId();
    return gameId;
  }

  public static void deleteGame(String gameId) {
    long ID = Long.parseLong(gameId);
    Key gameKey = KeyFactory.createKey(GAME_META_INFO, ID);
    datastore.delete(gameKey);
  }

  public static void updateGame(long gameId, Map<Object, Object> parameterMap)
      throws EntityNotFoundException, IOException {
    Key gameKey = KeyFactory.createKey(GAME_META_INFO, gameId);
    Entity target = datastore.get(gameKey);
    for (Object key : parameterMap.keySet()) {
      String keyStr = (String) key;
      if (keyStr.equals(PICS)==true)
      {
    	Map<Object,Object> picObj = (Map<Object,Object>)(parameterMap.get(key));
  		Text picText = new Text(picObj.toString());
  		target.setProperty((String) key,picText);
      }
      else
      {
      if (keyStr.equals(ACCESS_SIGNATURE) == false
          && keyStr.equals(DEVELOPER_ID) == false
          && keyStr.equals(GAME_ID) == false)

        target.setProperty((String) key, parameterMap.get(key));
      }
    }

    datastore.put(target);
  }

  static public boolean checkGameNameDuplicate(long gameId, Map<Object, Object> parameterMap) {
    Key gameKey = KeyFactory.createKey(GAME_META_INFO, gameId);
    try {
      Entity game = datastore.get(gameKey);

      if (game.getProperty(GAME_NAME).equals(parameterMap.get(GAME_NAME)))

        return false;
      else
        return checkGameNameDuplicate(parameterMap);
    } catch (Exception e) {
      return false;
    }
  }

  @SuppressWarnings("rawtypes")
  private static List<JSONObject> getDeveloperListInfo(List<String> developerIdList) {
    List<JSONObject> result = new ArrayList<JSONObject>();
    try {
      for (int i = 0; i < developerIdList.size(); i++) {
        JSONObject currentDeveloper = new JSONObject();

				try {
					Map developerInfo = DeveloperDatabaseDriver.getDeveloperMap(
					    Long.parseLong(developerIdList.get(i)));
					Map<String,String> filteredDeveloperInfo = new HashMap<String,String>();
					if (developerInfo.get(FIRST_NAME)!=null)
						filteredDeveloperInfo.put(FIRST_NAME, (String)developerInfo.get(FIRST_NAME));
					if (developerInfo.get(NICKNAME)!=null)
						filteredDeveloperInfo.put(NICKNAME, (String)developerInfo.get(NICKNAME));
					currentDeveloper = new JSONObject(filteredDeveloperInfo);
					
					result.add(currentDeveloper);
				} catch (Exception e) {
					return null;
				}

      }
      return result;
    } catch (Exception e) {
      return null;
    }
  }
  @SuppressWarnings("unchecked")
  private static List<Entity> getGameInfoAsList(boolean censored) {
      
		try {
			Filter censoredFilter = new FilterPredicate(PASS_CENSOR,
					FilterOperator.EQUAL, censored);
			Query q = new Query(GAME_META_INFO).setFilter(censoredFilter);
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
  
  public static List<JSONObject> getGameInfoAsJSON(boolean censored)
  {
	 List<Entity> resultAsEntity= getGameInfoAsList(censored);
	 List<JSONObject> jsonList = parseEntityToJSON(resultAsEntity);
	 return jsonList;	 
	 
  }
  public static List<String> getDeveloperList (long gameId) throws Exception
  {
	  Key gameKey =  KeyFactory.createKey(GAME_META_INFO, gameId);
	  try
	  {
	    Entity game = datastore.get(gameKey);
	    List<String> DeveloperList = (List<String>)game.getProperty(DEVELOPER_ID);
	    return DeveloperList;
	  }
	  catch(Exception e)
	  {
		  throw new Exception();
	  }
	  
  }
  public static String getGameName (long gameId) throws Exception
  {
	  Key gameKey =  KeyFactory.createKey(GAME_META_INFO, gameId);
	  try
	  {
	    Entity game = datastore.get(gameKey);
	    return (String)game.getProperty(GAME_NAME);
	  }
	  catch(Exception e)
	  {
		  throw new Exception();
	  }
  }
  public static Map<String, Object> getStatsHelper(long gameId) throws EntityNotFoundException {
     try
     {
    	 Map<String,Object> target= getStats(gameId);
    	 return target;
     }
     catch (Exception e)
     {
    	 return null;
     }
  }
  private static List<JSONObject> parseEntityToJSON(List<Entity> entityList)
  {
	  List<JSONObject> queryResult = new ArrayList<JSONObject>();
		try {
			for (Entity result : entityList) {
				JSONObject currentQueryResult = new JSONObject();
				long gameId = result.getKey().getId();
				//Map
				Map<String,Object> statsInfo = getStatsHelper(gameId);
				if (statsInfo!=null&&statsInfo.containsKey(RATING)==true)
					currentQueryResult.put(RATING, statsInfo.get(RATING));
				
				List<String> developerIdList = (List<String>) (result
						.getProperty(DEVELOPER_ID));
				currentQueryResult.put(GAME_ID,
						Long.toString(result.getKey().getId()));

				List<JSONObject> developerListInfo = getDeveloperListInfo(developerIdList);
				currentQueryResult.put(DEVELOPER_LOWER, developerListInfo);
				Map<String, Object> gameInfo = new HashMap<String, Object>(
						result.getProperties());
				for (String key : gameInfo.keySet()) {
					if (key.equals(DEVELOPER_ID) == false) {
						if (key.equals(PICS)) {

							Text picText = (Text) result.getProperty(PICS);
							JSONObject picMap = new JSONObject(
									picText.getValue());
							currentQueryResult.put(PICS, picMap);
						} else {
							currentQueryResult.put(key, gameInfo.get(key));
						}
					}
				}
				queryResult.add(currentQueryResult);
			}
			return queryResult;
		}
	  catch (Exception e)
	  {
		  return null;
	  }
  }
  @SuppressWarnings("unchecked")
  public static List<JSONObject> getGameInfo(boolean developerQuery, long developerId) {
    try {
      String developerIdStr = null;
      if (developerQuery == true) {
        developerIdStr = String.valueOf(developerId);
      }
      Query gameQuery = new Query(GAME_META_INFO);
      PreparedQuery pq = datastore.prepare(gameQuery);
      List<Entity> entityList = new ArrayList<Entity>();
      for (Entity result: pq.asIterable())
      {
    	  entityList.add(result);
      }
      //List<JSONObject> queryResult = parseEntityToJSON(entityList);
      List<JSONObject> queryResult = new ArrayList<JSONObject>();
      for (Entity result : pq.asIterable()) {
        JSONObject currentQueryResult = new JSONObject();
        long gameId = result.getKey().getId();
		Map<String,Object> statsInfo = getStatsHelper(gameId);
		if (statsInfo!=null&&statsInfo.containsKey(RATING)==true)
			currentQueryResult.put(RATING, statsInfo.get(RATING));
		
        List<String> developerIdList = (List<String>) (result.getProperty(DEVELOPER_ID));
        currentQueryResult.put(GAME_ID, Long.toString(result.getKey().getId()));
        if (developerQuery == true && !developerIdList.contains(developerIdStr)) {
          continue;
        }
        List<JSONObject> developerListInfo = getDeveloperListInfo(developerIdList);
        currentQueryResult.put(DEVELOPER_LOWER, developerListInfo);
        Map<String, Object> gameInfo = new HashMap<String, Object>(result.getProperties());
				for (String key : gameInfo.keySet()) {
					if (key.equals(DEVELOPER_ID) == false) {
						if (key.equals(PICS)) {

							Text picText = (Text) result.getProperty(PICS);
							JSONObject picMap = new JSONObject(
									picText.getValue());
							currentQueryResult.put(PICS, picMap);
						} else {
							currentQueryResult.put(key, gameInfo.get(key));
						}
					}
				}
        queryResult.add(currentQueryResult);
      }
      return queryResult;
    } catch (Exception e) {
      return null;
    }
  }

  public static List<Long> getAllPlayableGameIds(long playerId) {
    List<Entity> matches = ContainerDatabaseDriver.getAllMatchesByPlayerId(playerId);
    Set<Long> gameIdCollection = new HashSet<Long>();
    List<Long> result = new ArrayList<Long>();
    for (Entity entity : matches) {
      long currentGameId = (long) entity.getProperty(GAME_ID);
      if (gameIdCollection.contains(currentGameId) == false) {
        gameIdCollection.add(currentGameId);
      }
    }
    for (Long gameId : gameIdCollection)
      result.add(gameId);
    return result;
  }
  
  /**
   * Returns a Map of statistics that can be directly parsed to JSON, or throws 
   * EntityNotFoundException if no statistics available for that game.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Map<String, Object> getStats(long gameId) throws EntityNotFoundException {
    Map returnMap = new HashMap();
    
    try {
      Key key = KeyFactory.createKey(GAME_STATISTICS, gameId);
      Entity statistics = datastore.get(key);
      
      if (statistics.hasProperty(HIGH_SCORE)) {
        
        Map highScore = JSONUtil.parse((String) statistics.getProperty(HIGH_SCORE));
        List finishedGames = JSONUtil.parseList(
            ((Text)statistics.getProperty(FINISHED_GAMES)).getValue());
        returnMap.put(HIGH_SCORE, highScore);
        returnMap.put(FINISHED_GAMES, finishedGames);
      }
      
      if (statistics.hasProperty(RATING)) {
        double averageRating = Double.parseDouble((String) JSONUtil.parse(
            (String) statistics.getProperty(RATING)).get(AVERAGE_RATING));
        returnMap.put(RATING, averageRating);
      }
    }
    catch (EntityNotFoundException e) {
      throw e;
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return returnMap;
  }

  /**
   * Updates the rating for that game (or creates a new statistics entry if no statistics exist)
   * in the database, and also returns the new averageRating.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static double updateRatings(long gameId, double newRating) {
    Transaction txn = datastore.beginTransaction();
    double averageRating;
    int ratingCount;
    Map ratingMap = new HashMap();
    Entity statistics = new Entity(GAME_STATISTICS, gameId);
    
    try {
      Key key = KeyFactory.createKey(GAME_STATISTICS, gameId);
      Entity oldStatistics = datastore.get(key);
      
      if (oldStatistics.hasProperty(RATING)) {
        ratingMap = JSONUtil.parse((String) oldStatistics.getProperty(RATING));
        averageRating = Double.parseDouble((String) ratingMap.get(AVERAGE_RATING));
        ratingCount = (int) ratingMap.get(RATING_COUNT);

        averageRating = ((averageRating * ratingCount) + newRating) / (ratingCount + 1);
        ratingCount++;
      }
      else {
        averageRating = newRating;
        ratingCount = 1;
      }

      statistics.setProperty(HIGH_SCORE, oldStatistics.getProperty(HIGH_SCORE));
      statistics.setProperty(FINISHED_GAMES, oldStatistics.getProperty(FINISHED_GAMES));
    } 
    catch (Exception e) {
      averageRating = newRating;
      ratingCount = 1;
    }
    
    // Save as a String to avoid Integer/Double confusion
    ratingMap.put(AVERAGE_RATING, Double.toString(averageRating));
    ratingMap.put(RATING_COUNT, ratingCount);
    statistics.setProperty(RATING, new JSONObject(ratingMap).toString());
    datastore.put(statistics);
    txn.commit();
    return averageRating;
  }
}
