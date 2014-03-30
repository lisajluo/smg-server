package org.smg.server.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.smg.server.database.models.PlayerHistory;
import org.smg.server.database.models.PlayerHistory.MatchResult;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class DatabaseDriverPlayerHistory {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final String PLAYERHISTORY = "PLAYERHISTORY";
  private static final String PLAYERID = "PLAYERID";
  private static final String GAMEID = "GAMEID";
  private static final String MATCHID = "MATCHID";
  private static final String DATE = "DATE";
  private static final String TOKEN = "TOKEN";
  private static final String SCORE = "SCORE";
  private static final String OPPONENTIDS = "OPPONENTIDS";
  private static final String MATCHRESULT = "MATCHRESULT";
  private static int HISTORYLIMITPERGAME = 20;
  private static int HISTORYLIMITALL = 100;
  
  
  /**
   * save a player history.
   * matchResult should not be null, or "NO_RESULT" will be returned 
   * @param ph
   * @return "SUCCEED" if save succeed.
   */
  public static String savePlayerHistory(PlayerHistory ph) {
    if (ph.getMatchResult() == null) {
      return "NO_RESULT";
    }
    long playerId = ph.getPlayerId();
    long gameId = ph.getGameId();
    long matchId = ph.getMatchId();
    Entity psDB = new Entity(PLAYERHISTORY,String.valueOf(playerId)+"@"+String.valueOf(gameId)
        +"@"+String.valueOf(matchId));
    psDB.setProperty(PLAYERID, ph.getPlayerId());
    psDB.setProperty(GAMEID, ph.getGameId());
    psDB.setProperty(MATCHID, ph.getMatchId());
    psDB.setProperty(DATE, ph.getDate());
    psDB.setProperty(TOKEN, ph.getToken());
    psDB.setProperty(SCORE, ph.getScore());
    psDB.setProperty(OPPONENTIDS, ph.getOpponentIds());
    psDB.setProperty(MATCHRESULT, ph.getMatchResult().toString());
    datastore.put(psDB);
    return "SUCCEED";
  }
  
  /**
   * This method convert player history entity in database to PlayerHistory class
   * This method should only used for this purpose and cannot convert other record, or 
   * IllegalArgumentException returned.
   * @param e
   * @return
   */
  @SuppressWarnings("unchecked")
  private static PlayerHistory fromEntityToPlayerHistory(Entity e) {
    
    PlayerHistory ph;
    try {
      long playerId = (long)e.getProperty(PLAYERID);
      long gameId = (long)e.getProperty(GAMEID);
      long matchId = (long)e.getProperty(MATCHID);
      ph = new PlayerHistory(playerId,gameId,matchId);
    } catch (Exception e1) {
      throw new IllegalArgumentException();
    }
    Date date = e.getProperty(DATE) == null?null:(Date)(e.getProperty(DATE));
    ph.setDate(date);
    long token = e.getProperty(TOKEN) == null? 0: (long)(e.getProperty(TOKEN));
    ph.setToken(token);
    long score = e.getProperty(SCORE) == null? 0: (long)(e.getProperty(SCORE));
    ph.setScore(score);
    MatchResult matchResult = e.getProperty(MATCHRESULT) == null? 
        null: MatchResult.valueOf((String)(e.getProperty(MATCHRESULT)));
    ph.setMatchResult(matchResult);
    List<Long> opponentIds = e.getProperty(OPPONENTIDS) == null ? 
        new ArrayList<Long>():(List<Long>)(e.getProperty(OPPONENTIDS));
    ph.addOpponentIds(opponentIds);
    return ph;
  }
  
  /**
   * get player history for a certain game
   * return size are limited.
   * @param playerId
   * @param gameId
   * @return
   */
  public static List<PlayerHistory> getPlayerHistory(long playerId, long gameId) {
    List<PlayerHistory> lph = new ArrayList<PlayerHistory>();
    Filter playerIdFilter = new FilterPredicate(PLAYERID, FilterOperator.EQUAL, playerId);
    Filter gameIdFilter = new FilterPredicate(GAMEID, FilterOperator.EQUAL, gameId);
    Filter idFilter = CompositeFilterOperator.and(playerIdFilter,gameIdFilter);
    Query q = new Query(PLAYERHISTORY).setFilter(idFilter).addSort(DATE,SortDirection.DESCENDING);
    PreparedQuery pq = datastore.prepare(q);
    int i = 0;
    for (Entity e: pq.asIterable()) {
      try {
        PlayerHistory temp = fromEntityToPlayerHistory(e);
        lph.add(temp);
        i ++;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      if (i >= HISTORYLIMITPERGAME) {
        break;
      }
    }
    return lph;
  }
  
  /**
   * get player history for all games
   * return size are limited.
   * @param playerId
   * @param gameId
   * @return
   */
  public static List<PlayerHistory> getPlayerAllHistory(long playerId) {
    List<PlayerHistory> lph = new ArrayList<PlayerHistory>();
    Filter playerIdFilter = new FilterPredicate(PLAYERID, FilterOperator.EQUAL, playerId);
    Query q = new Query(PLAYERHISTORY).setFilter(playerIdFilter).addSort(DATE,SortDirection.DESCENDING);
    PreparedQuery pq = datastore.prepare(q);
    int i = 0;
    for (Entity e: pq.asIterable()) {
      try {
        PlayerHistory temp = fromEntityToPlayerHistory(e);
        lph.add(temp);
        i ++;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      if (i >= HISTORYLIMITALL) {
        break;
      }
    }
    return lph;
  }
}
