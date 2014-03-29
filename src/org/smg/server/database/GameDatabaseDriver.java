package org.smg.server.database;


import static org.smg.server.servlet.game.GameConstants.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


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
  

  private static List<JSONObject> getDeveloperListInfo (List<String> developerIdList)
  {
	  List<JSONObject> result =  new ArrayList<JSONObject> ();
	  for (int i=0;i<developerIdList.size();i++)
	  {
		  JSONObject currentDeveloper = null;
		  try
		  {
		    Map developerInfo = DeveloperDatabaseDriver.getDeveloperMap(Long.parseLong(developerIdList.get(i)));
		    currentDeveloper = new JSONObject(developerInfo);
		    result.add(currentDeveloper);
		  }
		  catch (Exception e)
		  {
			  return null;
		  }

	  }
	  return result;
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

    return null;
  }
  
  // lisa
  Map<String, Object> getStats(long gameId) {
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
  double updateRatings(long gameId, double newRating) {
    // TODO implement this (updates table) return newAverage
    return 0.00;
  }
}
