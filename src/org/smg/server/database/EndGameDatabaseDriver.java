package org.smg.server.database;

import static org.smg.server.servlet.container.ContainerConstants.GAME_OVER_SCORES;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT;

import org.smg.server.servlet.developer.DeveloperConstants;
import org.smg.server.servlet.game.GameConstants;

import static org.smg.server.servlet.developer.DeveloperConstants.NICKNAME;
import static org.smg.server.servlet.game.GameConstants.FINISHED_GAMES;
import static org.smg.server.servlet.game.GameConstants.GAME_ID;
import static org.smg.server.servlet.game.GameConstants.GAME_STATISTICS;
import static org.smg.server.servlet.game.GameConstants.HIGH_SCORE;
import static org.smg.server.servlet.game.GameConstants.PLAYERS;
import static org.smg.server.servlet.game.GameConstants.RATING;
import static org.smg.server.servlet.game.GameConstants.SCORE;
import static org.smg.server.servlet.game.GameConstants.TOKENS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smg.server.database.models.PlayerHistory;
import org.smg.server.database.models.PlayerHistory.MatchResult;
import org.smg.server.util.ELORankingUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class EndGameDatabaseDriver {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  /**
   * When a match ends, update the statistics for player and game.
   */
  public static void updateStats(Map<String, Object> winInfo) {
    updateGameStats(winInfo);
    updatePlayerStats(winInfo);
  }

  /**
   * Updates the statistics for that game (or creates a new entry if no statistics exist) upon
   * an EndGame.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static void updateGameStats(Map<String, Object> winInfo) {
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
        firstName = (String) playerNames.get(GameConstants.FIRST_NAME);
        nickname = (String) playerNames.get(NICKNAME);
        score = (int) entry.getValue(); 

        player.put(DeveloperConstants.FIRST_NAME, firstName);
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

  private static void updatePlayerStats(Map<String, Object> winInfo) {
    List<PlayerHistory> lph = winInfoToHistories(winInfo);
    for (PlayerHistory ph: lph) {
      DatabaseDriverPlayerHistory.savePlayerHistory(ph);
      DatabaseDriverPlayerStatistic.savePlayerStatisticFromHistory(ph);
    }
  }
  
  @SuppressWarnings("unchecked")
  private static List<PlayerHistory> winInfoToHistories(Map<String, Object>winInfo) {
    long gameId = (Long)winInfo.get("gameId");
    long matchId = (Long)winInfo.get("matchId");
    List<Long> playerIds = (List<Long>)winInfo.get("playerIds");
    List<PlayerHistory> result = new ArrayList<PlayerHistory>();
    Map<Long, Long> tokens = (Map<Long, Long>) winInfo.get("playerIdToNumberOfTokensInPot");
    Map<Long, Integer> scoreMap = (Map<Long,Integer>) winInfo.get(GAME_OVER_SCORES);
    Map<Long, Long> rankMap = ELORankingUtil.getRankingMap(scoreMap,gameId);
    Date date = new Date();
    List<Long> winnerIds = determineWinner(scoreMap);
    for (Long id: playerIds) {
      PlayerHistory temp = new PlayerHistory(id,gameId,matchId);
      temp.addOpponentIds(playerIds);
      temp.removeOpponentIds(id);
      temp.setDate(date);
      temp.setScore(scoreMap.get(id));
      temp.setRank(rankMap.get(id));
      if (tokens != null) {
        temp.setTokenChange(tokens.get(id));
      } else {
        temp.setTokenChange(0);
      }
      //TODO determine winner
      if (winnerIds.size() == 0) {
        temp.setMatchResult(MatchResult.DRAW);
      } else if (winnerIds.contains(id)) {
        temp.setMatchResult(MatchResult.WIN);
      } else {
        temp.setMatchResult(MatchResult.LOST);
      }
      result.add(temp);
    }
    return result;
  }
  
  /**
   * Determine winner: winner should be the one with highest score
   * if no winner, return empty list and all result should be DRAW
   */
  private static List<Long> determineWinner(Map<Long, Integer> scoreMap) {
    List<Long> result = new ArrayList<Long>();
    if (scoreMap.size() == 0){
      return result;
    }
    int maxScore = Integer.MIN_VALUE;
    int minScore = Integer.MAX_VALUE;
    for (Long key : scoreMap.keySet()){
      int temp = scoreMap.get(key);
      maxScore = Math.max(maxScore, temp);
      minScore = Math.min(minScore, temp);
    }
    if (maxScore == Integer.MIN_VALUE && minScore == Integer.MAX_VALUE) {
      return result;
    }
    if (maxScore == minScore){
      return result;
    }
    for (Long key : scoreMap.keySet()){
      if (scoreMap.get(key).equals(maxScore)){
        result.add(key);
      }
    }
    return result;
  }
  
  /**
   * Returns a Map of statistics properties (for a game) or throws exception if not found.
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
}