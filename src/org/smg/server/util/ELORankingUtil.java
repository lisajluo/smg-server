package org.smg.server.util;

import java.util.HashMap;
import java.util.Map;

import org.smg.server.database.DatabaseDriverPlayerStatistic;

public class ELORankingUtil {
  private static final double K = 32.0;
  private static final double DRAWSCORE = 0.5;
  private static final double WINSCORE = 1.0;
  
  public static double expectScore(long rankOp, long rankSelf) {
    double result = 1.0;
    double exp = 1.0 * (rankSelf-rankOp)/400;
    result /= (1 + Math.pow(10.0, exp));
    return result;
  }
  
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
