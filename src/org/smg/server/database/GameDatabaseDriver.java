package org.smg.server.database;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID;
import static org.smg.server.servlet.container.ContainerConstants.GAME_OVER_SCORES;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.FIRST_NAME;
import static org.smg.server.servlet.developer.DeveloperConstants.NICKNAME;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import org.smg.server.util.JSONUtil;

public class GameDatabaseDriver implements EndGameInterface {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  //Huan
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
      if (pq.countEntities() > 0)
        return true;
      else
        return false;
    } catch (Exception e) {
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
        game.setProperty((String) key, parameterMap.get(key));
    }

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

      if (keyStr.equals(ACCESS_SIGNATURE) == false
          && keyStr.equals(DEVELOPER_ID) == false
          && keyStr.equals(GAME_ID) == false)

        target.setProperty((String) key, parameterMap.get(key));
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

  private static List<JSONObject> getDeveloperListInfo(
      List<String> developerIdList) {
    List<JSONObject> result = new ArrayList<JSONObject>();
    try {
      for (int i = 0; i < developerIdList.size(); i++) {
        JSONObject currentDeveloper = new JSONObject();
        //currentDeveloper.put(DEVELOPER_ID, developerIdList.get(i));
        //result.add(currentDeveloper);
        // TODO: ADD THE MAP INFORMATION FOR DEVELOPER!

				try {
					Map developerInfo = DeveloperDatabaseDriver
							.getDeveloperMap(Long.parseLong(developerIdList
									.get(i)));
					Map<String,String> filteredDeveloperInfo = new HashMap<String,String>();
					if (developerInfo.get(FIRST_NAME)!=null)
						filteredDeveloperInfo.put(FIRST_NAME, (String)developerInfo.get(FIRST_NAME));
					if (developerInfo.get(NICKNAME)!=null)
						filteredDeveloperInfo.put(NICKNAME, (String)developerInfo.get(NICKNAME));
					currentDeveloper = new JSONObject(filteredDeveloperInfo);
					System.out.println(currentDeveloper);
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

  public static List<JSONObject> getGameInfo(boolean developerQuery, long developerId) {
    try {
      String developerIdStr = null;
      if (developerQuery == true)
        developerIdStr = String.valueOf(developerId);
      Query gameQuery = new Query(GAME_META_INFO);
      PreparedQuery pq = datastore.prepare(gameQuery);
      List<JSONObject> queryResult = new ArrayList<JSONObject>();
      for (Entity result : pq.asIterable()) {
        JSONObject currentQueryResult = new JSONObject();
        List<String> developerIdList = (List<String>) (result.getProperty(DEVELOPER_ID));
        if (developerQuery == true
            && developerIdList.contains(developerIdStr) == false)
        {
          continue;
        }
        List<JSONObject> developerListInfo = getDeveloperListInfo(developerIdList);
        currentQueryResult.put(DEVELOPER, developerListInfo);
        Map<String, Object> gameInfo = new HashMap<String, Object>(
            result.getProperties());
				for (String key : gameInfo.keySet()) {
					if (key.equals(DEVELOPER_ID) == false) {
						currentQueryResult.put(key, gameInfo.get(key));
					}
				}
        queryResult.add(currentQueryResult);
      }
      return queryResult;
    } catch (Exception e) {
      return null;
    }
  }

  // Huan
  public static List<Long> getAllPlayableGameIds(long playerId) {
    // TODO implement this method How does each playerId store all the game he
    // played??
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
        System.out.println("HERE");
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
   * Returns a Map of statistics properties (for a game) or throws exception if not found.  For
   * internal use in GameDatabaseDriver.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static Map getStatisticsMap(long gameId) throws EntityNotFoundException {
    try {
      Key key = KeyFactory.createKey(GAME_STATISTICS, gameId);
      Entity entity = datastore.get(key);
      return new HashMap(entity.getProperties());
    } 
    catch (Exception e) {
      throw new EntityNotFoundException(null);
    }
  }

  /**
   * Updates the statistics for that game (or creates a new entry if no statistics exist) upon
   * an EndGame.
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void updateStats(Map<String, Object> winInfo) {
    long gameId = (long) winInfo.get(GAME_ID);
    int highScore;
    Map highScoreMap = new HashMap();
    List finishedGames;
    List players = new ArrayList();
    Map player;
    String firstName, nickname;
    int score;
    long tokens;

    TransactionOptions options = TransactionOptions.Builder.withXG(true);
    Transaction txn = datastore.beginTransaction(options);
    Entity statistics = new Entity(GAME_STATISTICS, gameId);

    try {
      Map oldStatistics = getStatisticsMap(gameId);
      // If there is a high score we also have finished games.
      if (oldStatistics.containsKey(HIGH_SCORE)) {  
        highScore = (int) JSONUtil.parse((String) oldStatistics.get(HIGH_SCORE)).get(SCORE);
        finishedGames = (List) JSONUtil.parseList(
            ((Text) oldStatistics.get(FINISHED_GAMES)).getValue());
      }
      else {
        // No finished games (but had rating)
        highScore = 0;
        finishedGames = new ArrayList();
      }
      
      if (oldStatistics.containsKey(RATING)) {
        statistics.setProperty(RATING, oldStatistics.get(RATING));
      }
    } 
    catch (Exception e) { // No statistics
      highScore = 0;
      finishedGames = new ArrayList();
    }

    Map<Object, Object> gameOverScores = (Map<Object, Object>) winInfo.get(GAME_OVER_SCORES);

    for (Map.Entry<Object, Object> entry : gameOverScores.entrySet()) {
      long playerId = (Long) entry.getKey();
      player = new HashMap();
      if ((int) entry.getValue() >= highScore) {
        highScoreMap.put(PLAYER_ID, Long.toString(playerId));
        highScoreMap.put(SCORE, (Integer) entry.getValue());
      }

      try {
        Map playerNames = DatabaseDriverPlayer.getPlayerNames(playerId);
        firstName = (String) playerNames.get(FIRST_NAME);
        nickname = (String) playerNames.get(NICKNAME);
        score = (int) entry.getValue(); 

        player.put(FIRST_NAME, firstName);
        player.put(NICKNAME, nickname);
        player.put(SCORE, score);

        if (winInfo.containsKey(PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT)) {
          tokens = (long) ((Map) winInfo.get(PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT)).get(playerId);
          player.put(TOKENS, tokens);
        }

        players.add(player);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    Map playerMap = new HashMap();
    playerMap.put(PLAYERS, players);

    finishedGames.add(playerMap);

    statistics.setProperty(HIGH_SCORE, new JSONObject(highScoreMap).toString());
    statistics.setProperty(FINISHED_GAMES, new Text(new JSONArray(finishedGames).toString()));

    datastore.put(statistics);
    txn.commit();
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
