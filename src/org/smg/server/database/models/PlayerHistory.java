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

  /**
   * Get rank info
   * @return
   */
  public long getRank() {
    return rank;
  }

  /**
   * Set rank info 
   * @param rank after the game
   */
  public void setRank(long rank) {
    this.rank = rank;
  }

  /**
   * Constructor
   * @param playerId playerId/userId
   * @param gameId
   * @param matchId
   */
  public PlayerHistory(long playerId, long gameId, long matchId){
    date = new Date();
    this.playerId = playerId;
    this.gameId = gameId;
    this.matchId = matchId;
    this.opponentIds = new ArrayList<Long>();
    this.rank = -1;
  }

  /**
   * Get date of the match
   * @return
   */
  public Date getDate() {
    return date;
  }

  /**
   * Set date of the match
   * @param date
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Get token change
   * @return
   */
  public long getTokenChange() {
    return tokenChange;
  }

  /**
   * Set token change
   * @param tokenChange
   */
  public void setTokenChange(long tokenChange) {
    this.tokenChange = tokenChange;
  }

  /**
   * Get score
   * @return
   */
  public long getScore() {
    return score;
  }

  /**
   * Set score
   * @param scoreChange
   */
  public void setScore(long scoreChange) {
    this.score = scoreChange;
  }

  /**
   * Get a list of opponents' id
   * @return
   */
  public List<Long> getOpponentIds() {
    return Collections.unmodifiableList(opponentIds);
  }

  /**
   * Add a opponent
   * @param opponentIds
   */
  public void addOpponentIds(List<Long> opponentIds) {
    for (Long id : opponentIds) {
      this.opponentIds.add(id);
    }
  }

  /**
   * Add a list of opponent
   * @param opponentId
   * @return
   */
  public boolean addOpponentId(Long opponentId) {
    return this.opponentIds.add(opponentId);
  }

  /**
   * Remove an opponent
   * @param opponentId
   * @return
   */
  public boolean removeOpponentIds(Long opponentId) {
    return this.opponentIds.remove(opponentId);
  }

  /**
   * Remove a list of opponent
   * @param opponentIds
   */
  public void removeOpponentIds(List<Long> opponentIds) {
    for (Long id : opponentIds) {
      this.opponentIds.remove(id);
    }
  }

  public long getPlayerId() {
    return playerId;
  }

  /**
   * get GameId
   * @return
   */
  public long getGameId() {
    return gameId;
  }

  /**
   * get MatchId
   * @return
   */
  public long getMatchId() {
    return matchId;
  }

  /**
   * Get matchResult
   * @return
   */
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
  

  /**
   * {@inheritDoc}
   */
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
