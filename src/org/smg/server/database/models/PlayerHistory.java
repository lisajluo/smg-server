package org.smg.server.database.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Model of game history for a player in a game. Fields are defined in {@code HistoryProperty}
 * @author Archer
 *
 */
public class PlayerHistory {
  public enum MatchResult {
    WIN,LOST,DRAW;
  }
  private final long playerId;
  private final long gameId;
  private final long matchId;
  private Date date;
  private int tokenChange = 0;
  private int scoreChange = 0;
  private List<Long> opponentIds = new ArrayList<Long>();
  private MatchResult matchResult;

  public PlayerHistory(long playerId, long gameId, long matchId){
    date = new Date();
    this.playerId = playerId;
    this.gameId = gameId;
    this.matchId = matchId;
    this.opponentIds = new ArrayList<Long>();
  }
  
  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }


  public int getTokenChange() {
    return tokenChange;
  }

  public void setTokenChange(int tokenChange) {
    this.tokenChange = tokenChange;
  }

  public int getScoreChange() {
    return scoreChange;
  }

  public void setScoreChange(int scoreChange) {
    this.scoreChange = scoreChange;
  }

  public List<Long> getOpponentIds() {
    return Collections.unmodifiableList(opponentIds);
  }

  public void addOpponentIds(List<Long> opponentIds) {
    for (Long id : opponentIds) {
      this.opponentIds.add(id);
    }
  }
  
  public boolean addOpponentId(Long opponentId) {
    return this.opponentIds.add(opponentId);
  }
  
  public boolean removeOpponentIds(Long opponentId) {
    return this.opponentIds.remove(opponentId);
  }
  
  public void removeOpponentIds(List<Long> opponentIds) {
    for (Long id : opponentIds) {
      this.opponentIds.remove(id);
    }
  }

  public long getPlayerId() {
    return playerId;
  }

  public long getGameId() {
    return gameId;
  }

  public long getMatchId() {
    return matchId;
  }
  
  public MatchResult getMatchResult() {
    return matchResult;
  }
  /**
   * Set matchResult
   * @param matchResult cannot be null or IllegalArugmentException throw indicate external error.
   */
  public void setMatchResult(MatchResult matchResult) {
    if (matchResult == null) {
      throw new IllegalArgumentException();
    }
    this.matchResult = matchResult;
  }
}
