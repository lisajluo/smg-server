package org.smg.server.database;


import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.game.GameUtil.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


// TODO should implement that interface when Container team is done
public class GameDatabaseDriver /*implements EndGameInterface*/ {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  

	private static List<JSONObject> getDeveloperListInfo(
			List<String> developerIdList) {
		List<JSONObject> result = new ArrayList<JSONObject>();
		try {
			for (int i = 0; i < developerIdList.size(); i++) {
				JSONObject currentDeveloper = new JSONObject();
				currentDeveloper.put(DEVELOPER_ID, developerIdList.get(i));
                result.add(currentDeveloper);
                //TODO: ADD THE MAP INFORMATION FOR DEVELOPER!
				/*
				 * try { Map developerInfo =
				 * DeveloperDatabaseDriver.getDeveloperMap
				 * (Long.parseLong(developerIdList.get(i))); currentDeveloper =
				 * new JSONObject(developerInfo); result.add(currentDeveloper);
				 * } catch (Exception e) { return null; }
				 */

			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static List<JSONObject> getGameInfo(boolean developerQuery , long developerId) {
		try {
			String developerIdStr = null;
			if (developerQuery==true)
				developerIdStr = String.valueOf(developerId);
			Query gameQuery = new Query(GAME_META_INFO);
			PreparedQuery pq = datastore.prepare(gameQuery);
			List<JSONObject> queryResult = new ArrayList<JSONObject>();
			for (Entity result : pq.asIterable()) {
				JSONObject currentQueryResult = new JSONObject();
				List<String> developerIdList = (List<String>) (result.getProperty(DEVELOPER_ID));
				if (developerQuery==true&&developerIdList.contains(developerIdStr)==false)
					continue;
				List<JSONObject> developerListInfo = getDeveloperListInfo(developerIdList);
				currentQueryResult.put(DEVELOPER, developerListInfo);
				Map<String, Object> gameInfo = new HashMap<String, Object>(
						result.getProperties());
				for (String key : gameInfo.keySet())
					currentQueryResult.put(key, gameInfo.get(key));
				queryResult.add(currentQueryResult);
			}
			return queryResult;
		} catch (Exception e) {
			return null;
		}
	}
  // Huan
  Entity getGame(long gameId) throws EntityNotFoundException {
    // TODO implement this method
	Key gameKey=KeyFactory.createKey( GAME_META_INFO, gameId);
	try
	{
      return datastore.get(gameKey);
	}
	catch (Exception e)
	{
		throw new EntityNotFoundException(gameKey);
	}

  }
  
  // Huan
  List<Long> getAllPlayableGameIds(long playerId) {

    // TODO implement this method How does each playerId store all the game he played??
	List<Entity> matches = getAllMatchesByPlayerId(playerId);
	Set<Long> gameIdCollection = new HashSet<Long> ();
	List<Long> result = new ArrayList<Long> ();
	for(Entity entity : matches)
	{
		long currentGameId = Long.parseLong((String)entity.getProperty(GAME_ID));
		if (gameIdCollection.contains(currentGameId)==false)
		{
			gameIdCollection.add(currentGameId);
		}
	}
	for (Long gameId : gameIdCollection)
		result.add(gameId);
    return result;
  }
  
  // lisa
  public static Map<String, Object> getStats(long gameId) {
    // TODO write this method
    // highScore: Map<String, Object> playerId, score
    // rating: Map<int totalRatings, double averageRating>
    // matches: List of Map<String, Object>:
    /*
     * �firstName�: �Bob�,
           �nickName�: �Ninja�,
           �winner�: true, // boolean
           �score�: 43543, //int
           �tokens:� 2394384 // (for games that have tokens)
     */
    return null;
  }
  
  //@Override
  // TODO override when Container is finished
  // lisa
  void updateStats(Map<String, Object> winInfo) {
    // (iin the beginning there are no stats for a game)
    /*
     * winInfo:
      PlayerIds: [ ] (long)
      Score: Map<long playerId, int score>
      GameId: long
      WinnerIds: [ ]
      Tokens: Optional.of(Map<long playerId, int tokenNumber>)
     */
    //
  }
  
  // lisa
  public static double updateRatings(long gameId, double newRating) {
    // TODO implement this (updates table) return newAverage
    return 0.00;
  }
}
