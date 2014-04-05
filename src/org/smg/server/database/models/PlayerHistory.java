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
  private long tokenChange = 0;
  private long score = 0;
  private List<Long> opponentIds = new ArrayList<Long>();
  private MatchResult matchResult;
  private long rank = -1;

  public long getRank() {
    return rank;
  }

  public void setRank(long rank) {
    this.rank = rank;
  }

  public PlayerHistory(long playerId, long gameId, long matchId){
    date = new Date();
    this.playerId = playerId;
    this.gameId = gameId;
    this.matchId = matchId;
    this.opponentIds = new ArrayList<Long>();
    this.rank = -1;
  }
  
  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }


  public long getTokenChange() {
    return tokenChange;
  }

  public void setTokenChange(long tokenChange) {
    this.tokenChange = tokenChange;
  }

  public long getScore() {
    return score;
  }

  public void setScore(long scoreChange) {
    this.score = scoreChange;
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
  

  @Override
  public boolean equals(Object p) {
    if (!(p instanceof PlayerHistory)){
      return false;
    }
    PlayerHistory temp = (PlayerHistory)p;
    if (!this.date.equals(temp.date)){
      return false;
    }
    if (this.gameId != temp.gameId) {
      return false;
    }
    if (this.matchId != temp.matchId) {
      return false;
    }
    if (this.playerId != temp.playerId) {
      return false;
    }
    if (!this.matchResult.equals(temp.matchResult)) {
      return false;
    }
    if (!this.opponentIds.equals(temp.opponentIds)) {
      return false;
    }
    if (this.score != temp.score) {
      return false;
    }
    if (this.tokenChange != temp.tokenChange) {
      return false;
    }
    return true;
  }
}
