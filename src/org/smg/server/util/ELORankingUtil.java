package org.smg.server.util;

import java.util.HashMap;
import java.util.Map;

import org.smg.server.database.DatabaseDriverPlayerStatistic;

/**
 * ELO Ranking 
 * 
 * http://en.wikipedia.org/wiki/Elo_rating_system
 * 
 * @author Archer
 *
 */
public class ELORankingUtil {
  private static final double K = 32.0;
  private static final double DRAWSCORE = 0.5;
  private static final double WINSCORE = 1.0;
  
  /**
   * Generate expect Score
   * @param rankOp previous rank of opponent
   * @param rankSelf previous rank of player
   * @return
   */
  public static double expectScore(long rankOp, long rankSelf) {
    double result = 1.0;
    double exp = 1.0 * (rankOp-rankSelf)/400;
    result = 1.0 / (1 + Math.pow(10.0, exp));
    return result;
  }
  
  /**
   * Generate ranking map contains (playerId, ranking) for a match
   * 
   * @param scoreMap a map contains (playerId, score) pair
   * @param gameId
   * @return
   */
  public static Map<Long, Long> getRankingMap(Map<Long, Integer> scoreMap, long gameId) {
    Map<Long, Long> rankingMap = DatabaseDriverPlayerStatistic
        .getPlayersRanking(scoreMap.keySet(), gameId);
    Map<Long, Double> winMap = getWinMap(scoreMap);
    Map<Long, Double> expectMap = getExpectMap(rankingMap);
    //TODO determine opSize
    Integer normFactor = rankingMap.size()-1;
    for (Long id: rankingMap.keySet()) {
      Double newRank = rankingMap.get(id).doubleValue();
      newRank += K / normFactor * (winMap.get(id) - expectMap.get(id));
      rankingMap.put(id, Math.round(newRank));
    }
    return rankingMap;
  }
  
  /**
   * Get a map have (playerId, expected rank) pair
   * @param rankingMap
   * @return
   */
  private static Map<Long, Double> getExpectMap(Map<Long, Long> rankingMap) {
    Map<Long, Double> expectMap = new HashMap<Long, Double>();
    for (Long idSelf: rankingMap.keySet()) {
      double es = 0.0;
      for (Long idOp: rankingMap.keySet()) {
        if (idSelf.equals(idOp)) {
          continue;
        }
        es += expectScore(rankingMap.get(idOp),rankingMap.get(idSelf));
        
      }
      expectMap.put(idSelf, es);
    }
    return expectMap;
  }

  /**
   * Transform score map to a new kind of score map: where a player win for 1 point, 
   * draw for 0.5 points and lost for 0 point. 
   * @param scoreMap
   * @return
   */
  private static Map<Long, Double> getWinMap(Map<Long, Integer> scoreMap) {
    Map<Long, Double> winMap = new HashMap<Long, Double>();
    for (Long idSelf: scoreMap.keySet()) {
      double win = 0.0;
      for(Long idOp: scoreMap.keySet()) {
        if (idSelf.equals(idOp)) {
          continue;
        }
        Integer scoreSelf = scoreMap.get(idSelf);
        Integer scoreOp = scoreMap.get(idOp);
        if (scoreSelf == scoreOp){
          win += DRAWSCORE;
        } else if (scoreSelf > scoreOp) {
          win += WINSCORE;
        }
      }
      winMap.put(idSelf, win);
    } 
    return winMap;
  }
}
