package org.smg.server.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smg.server.database.models.PlayerHistory;
import org.smg.server.database.models.PlayerStatistic;
import org.smg.server.database.models.PlayerStatistic.StatisticProperty;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;

public class DatabaseDriverPlayerStatistic {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final String PLAYERSTATISTIC = "PLAYERSTATISTIC";
  
  /**
   * save player's statistic information to database (insert or update)
   * if update success, return "SUCCESS"
   * if input player history is illegal, return:
   *    no game result: "NO_RESULT"
   * @param player
   * @return
   */
  public static String savePlayerStatisticFromHistory(PlayerHistory ph) {
    if (ph.getMatchResult() == null) {
      return "NO_RESULT";
    }
    long playerId = ph.getPlayerId();
    long gameId = ph.getGameId();
    Transaction txn = datastore.beginTransaction();
    try {
      Key psKey = KeyFactory.createKey(PLAYERSTATISTIC, 
          String.valueOf(playerId)+"@"+String.valueOf(gameId));
      Entity psDB;
      try {
        psDB = datastore.get(psKey);
        int newScore = Integer.parseInt(
            (String)(psDB.getProperty(StatisticProperty.SCORE.toString())));
        newScore += ph.getScoreChange();
        psDB.setProperty(StatisticProperty.SCORE.toString(), String.valueOf(newScore));
        int newToken = Integer.parseInt(
            (String)(psDB.getProperty(StatisticProperty.TOKEN.toString())));
        newToken += ph.getTokenChange();
        psDB.setProperty(StatisticProperty.TOKEN.toString(), String.valueOf(newToken));
        int newResult = Integer.parseInt(
            (String)(psDB.getProperty(ph.getMatchResult().toString()))) + 1;
        psDB.setProperty(ph.getMatchResult().toString(), String.valueOf(newResult));
        datastore.put(psDB);
      } catch (EntityNotFoundException e) {
        psDB = new Entity(PLAYERSTATISTIC,
            String.valueOf(playerId)+"@"+String.valueOf(gameId));
        psDB.setProperty(StatisticProperty.PLAYERID.toString(),
            String.valueOf(playerId));
        psDB.setProperty(StatisticProperty.GAMEID.toString(), String.valueOf(gameId));
        psDB.setProperty(StatisticProperty.WIN.toString(), "0");
        psDB.setProperty(StatisticProperty.LOST.toString(), "0");
        psDB.setProperty(StatisticProperty.DRAW.toString(), "0");
        psDB.setProperty(ph.getMatchResult().toString(), "1");
        psDB.setProperty(StatisticProperty.SCORE.toString(), ph.getScoreChange());
        psDB.setProperty(StatisticProperty.TOKEN.toString(), ph.getTokenChange());
        datastore.put(psDB);
      }
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
    return "SUCCESS";
  }
  
  /**
   * Get a played by its id and the game id. If the history is not exist, return an initial 
   * statistic object
   * @param playerId
   * @param gameId
   * @return
   */
  public static PlayerStatistic getPlayerStatistic(long playerId, long gameId) {
    PlayerStatistic ps;
    Key psKey = KeyFactory.createKey(PLAYERSTATISTIC, 
        String.valueOf(playerId)+"@"+String.valueOf(gameId));
    Entity playerStatisticDB;
    try {
      playerStatisticDB = datastore.get(psKey);
    } catch (EntityNotFoundException e) {
      ps = new PlayerStatistic();
      // Handle no result;
      ps.setProperty(StatisticProperty.PLAYERID, String.valueOf(playerId));
      ps.setProperty(StatisticProperty.GAMEID, String.valueOf(gameId));
      ps.setProperty(StatisticProperty.WIN, "0");
      ps.setProperty(StatisticProperty.LOST, "0");
      ps.setProperty(StatisticProperty.DRAW, "0");
      ps.setProperty(StatisticProperty.SCORE, "0");
      ps.setProperty(StatisticProperty.TOKEN, "0");
      return ps;
    }
    ps = fromEntitytoStatistic(playerStatisticDB);
    return ps;
  }
  
  /**
   * set player's token for a game. If the player did not play this game before, generate new
   * record
   * @param playerId
   * @param gameId
   * @param newValue
   * @return true when update success
   */
  public static boolean setPlayerToken(long playerId, long gameId, int newValue) {
    Transaction txn = datastore.beginTransaction();
    try {
      Key psKey = KeyFactory.createKey(PLAYERSTATISTIC, 
          String.valueOf(playerId)+"@"+String.valueOf(gameId));
      Entity psDB;
      try {
        psDB = datastore.get(psKey);
        psDB.setProperty(StatisticProperty.TOKEN.toString(), String.valueOf(newValue));
        datastore.put(psDB);
      } catch (EntityNotFoundException e) {
        psDB = new Entity(PLAYERSTATISTIC,
            String.valueOf(playerId)+"@"+String.valueOf(gameId));
        psDB.setProperty(StatisticProperty.PLAYERID.toString(),
            String.valueOf(playerId));
        psDB.setProperty(StatisticProperty.GAMEID.toString(), String.valueOf(gameId));
        psDB.setProperty(StatisticProperty.WIN.toString(), "0");
        psDB.setProperty(StatisticProperty.LOST.toString(), "0");
        psDB.setProperty(StatisticProperty.DRAW.toString(), "0");
        psDB.setProperty(StatisticProperty.SCORE.toString(), "0");
        psDB.setProperty(StatisticProperty.TOKEN.toString(), String.valueOf(newValue));
        datastore.put(psDB);
      }
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
    return true;
  }
  //Getter methods
  /**
   * get players token
   * @param playerId
   * @param gameId
   * @return
   */
  public static int getPlayerToken(long playerId, long gameId) {
    int result = 0;
    PlayerStatistic ps = getPlayerStatistic(playerId, gameId); 
    result = Integer.parseInt(ps.getProperty(StatisticProperty.TOKEN));
    return result;
  }
  
  /**
   * Get players statistics
   *
   * @param playerId
   * @return
   */
  public static List<PlayerStatistic> getPlayerStatistics(long playerId) {
    //TODO test
    List<PlayerStatistic> result = new ArrayList<PlayerStatistic>();
    Filter playerIdFilter =
        new FilterPredicate(StatisticProperty.PLAYERID.toString(),
                            FilterOperator.EQUAL,
                            String.valueOf(playerId));
    Query q = new Query(PLAYERSTATISTIC).setFilter(playerIdFilter);
    PreparedQuery pq = datastore.prepare(q);
    for (Entity e: pq.asIterable()) {
      result.add(fromEntitytoStatistic(e));
    }
    return result;
  }
  
  private static PlayerStatistic fromEntitytoStatistic(Entity e) {
    PlayerStatistic ps = new PlayerStatistic();
    Map<String, Object> properties = e.getProperties();
    for (String key : properties.keySet()) {
      StatisticProperty sp = StatisticProperty.findByValue(key);
      if (sp != null) {
        ps.setProperty(sp, (String) properties.get(key));
      }
    }
    return ps;
  }
}
