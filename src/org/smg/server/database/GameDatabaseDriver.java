package org.smg.server.database;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_IDS;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID;
import static org.smg.server.servlet.container.ContainerConstants.GAME_OVER_SCORES;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER;

import org.smg.server.database.models.Player;
import org.smg.server.servlet.developer.DeveloperUtil;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

// TODO should implement that interface when Container team is done
public class GameDatabaseDriver /* implements EndGameInterface */{
  static final DatastoreService datastore = DatastoreServiceFactory
      .getDatastoreService();

  private static List<JSONObject> getDeveloperListInfo(List<String> developerIdList) {
    List<JSONObject> result = new ArrayList<JSONObject>();
    for (int i = 0; i < developerIdList.size(); i++) {
      JSONObject currentDeveloper = null;
      try {
        Map developerInfo = DeveloperDatabaseDriver.getDeveloperMap(
            Long.parseLong(developerIdList.get(i)));
        currentDeveloper = new JSONObject(developerInfo);
        result.add(currentDeveloper);
      } catch (Exception e) {
        return null;
      }

    }
    return result;
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
  public Entity getGame(long gameId) throws EntityNotFoundException {
    // TODO implement this method
    Key gameKey = KeyFactory.createKey(GAME_META_INFO, gameId);
    try {
      return datastore.get(gameKey);
    } catch (Exception e) {
      throw new EntityNotFoundException(gameKey);
    }

  }

  // Huan
  public List<Long> getAllPlayableGameIds(long playerId) {

    // TODO implement this method How does each playerId store all the game he
    // played??

    return null;
  }

  // lisa
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Map<String, Object> getStats(long gameId) {
    try {
      Key key = KeyFactory.createKey(STATISTICS, gameId);
      Entity statistics = datastore.get(key);
      
      Map highScore = JSONUtil.parse((String) statistics.getProperty(HIGH_SCORE));
      String rating = (String) ((Map) JSONUtil.parse((String) statistics.getProperty(RATING))).get(AVERAGE_RATING);
      Map finishedGames = JSONUtil.parse((String) statistics.getProperty(FINISHED_GAMES));
      
      Map returnMap = new HashMap();
      returnMap.put(HIGH_SCORE, highScore);
      returnMap.put(RATING, rating);
      returnMap.put(FINISHED_GAMES, finishedGames);
      return returnMap;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns Map of statistics properties (for a game) or throws exception if not found.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static Map getStatisticsMap(long gameId) throws EntityNotFoundException {
    try {
      Key key = KeyFactory.createKey(STATISTICS, gameId);
      Entity entity = datastore.get(key);
      return new HashMap(entity.getProperties());
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new EntityNotFoundException(null);
    }
  }
  
  // @Override
  // TODO override when Container is finished
  // lisa
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void updateStats(Map<String, Object> winInfo) {
    long gameId = (long) winInfo.get(GAME_ID);
    int highScore;
    JSONObject highScoreJSON = new JSONObject();
    JSONObject ratingJSON;
    List finishedGames, players = new ArrayList();
    Map player;
    String firstName, nickname;
    int score;
    long tokens;
    
  /*  “playerIds”: [ ] (long)
    “gameOverScores”: Map<String playerId, int score>
    “gameId”: long
    “playerIdToNumberOfTokensInPot”:Map<long playerId, long tokenNumber> // if not token game, does not contain this field
    } */
    
 // TODO write this method
    // highScore: Map<String, int> playerId, score
    // rating: Map<int totalRatings, double averageRating>
    // finishedGames: List of Map<String, Object>:
    /*
     * “firstName”: “Bob”,
           “nickName”: “Ninja”,
           “score”: 43543, //int
           “tokens:” 2394384 // (for games that have tokens)
     */
    
    Transaction txn = datastore.beginTransaction();
    Entity statistics = new Entity(STATISTICS, gameId);
    
    try {
      Map oldStatistics = getStatisticsMap(gameId);
      highScore = (int) ((Map) JSONUtil.parse((String) oldStatistics.get(HIGH_SCORE))).get(SCORE);
      ratingJSON = new JSONObject((String) oldStatistics.get(RATING));
      finishedGames = (List) JSONUtil.parse((String) oldStatistics.get(FINISHED_GAMES));
    }
    catch (Exception e) {
      highScore = 0;
      ratingJSON = new JSONObject();
      DeveloperUtil.jsonPut(ratingJSON, AVERAGE_RATING, ZERO_STRING);
      DeveloperUtil.jsonPut(ratingJSON, RATING_COUNT, ZERO_STRING);
      finishedGames = new ArrayList();
    }
    
    Map<Object, Object> gameOverScores = (Map<Object, Object>) winInfo.get(GAME_OVER_SCORES);
    
    for (Map.Entry<Object, Object> entry : gameOverScores.entrySet()) {
      long playerId = Long.parseLong((String) entry.getKey());
      player = new HashMap();
      if ((int) entry.getValue() > highScore) {
        DeveloperUtil.jsonPut(highScoreJSON, PLAYER_ID, playerId);
        DeveloperUtil.jsonPut(highScoreJSON, HIGH_SCORE, (String) entry.getValue());
      }
      
      try {
        Map playerNames = new HashMap();
        //Map playerNames = Player.getPlayerNames(Long.parseLong((String) entry.getKey()));
        firstName = (String) playerNames.get(FIRST_NAME);
        nickname = (String) playerNames.get(NICKNAME);
        score = Integer.parseInt((String) entry.getValue());
        
        player.put(FIRST_NAME, firstName);
        player.put(NICKNAME, nickname);
        player.put(SCORE, score);
        
        if (winInfo.containsKey(PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT)) {
          tokens = Long.parseLong(
              (String) ((Map) winInfo.get(PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT)).get(playerId));
          player.put(TOKENS, tokens);
        }
        
        players.add(player);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    finishedGames.add(players);
    
    statistics.setProperty(HIGH_SCORE, highScoreJSON.toString());
    statistics.setProperty(RATING, ratingJSON.toString());
    statistics.setProperty(FINISHED_GAMES, finishedGames.toString());
    
    datastore.put(statistics);
    txn.commit(); 
  }

  // lisa
  @SuppressWarnings("unchecked")
  double updateRatings(long gameId, double newRating) throws EntityNotFoundException {
    try {
      Key key = KeyFactory.createKey(STATISTICS, gameId);
      Entity statistics = datastore.get(key);
      
      Map ratingMap = JSONUtil.parse((String) statistics.getProperty(RATING));
      double averageRating = Double.parseDouble((String) ratingMap.get(AVERAGE_RATING));
      int ratingCount = Integer.parseInt((String) ratingMap.get(RATING_COUNT));
      
      averageRating = ((averageRating * ratingCount) + newRating) / (ratingCount + 1);
      ratingCount++;

      ratingMap.put(AVERAGE_RATING, averageRating);
      ratingMap.put(RATING_COUNT, ratingCount);
      
      statistics.setProperty(RATING, ratingMap.toString());
      return averageRating;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new EntityNotFoundException(null);
    }
  }
}
