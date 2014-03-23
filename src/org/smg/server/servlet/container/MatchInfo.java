
package org.smg.server.servlet.container;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MatchInfo {
  private long matchId;
  private long gameId;
  private List<Long> playerIds;
  private long playerIdThatHasTurn;
  private Map<String, Long> gameOverScores;
  private String gameOverReason;

  /*
   * Replace the data type with: private List<GameStateHistoryItem>
   * gameStateHistory;
   */
  private List<Map<String, Object>> gameStateHistory;

  public MatchInfo(long matchId, long gameId, List<Long> playerIds, long playerHasTurn,
      Map<String, Long> gameOverScore, String gameOverReason,
      List<Map<String, Object>> hstryAry) {
    super();
    this.matchId = matchId;
    this.gameId = gameId;
    this.playerIds = playerIds;
    this.playerIdThatHasTurn = playerHasTurn;
    this.gameOverScores = gameOverScore;
    this.gameOverReason = gameOverReason;
    this.gameStateHistory = hstryAry;
  }

  @SuppressWarnings("unchecked")
  public static MatchInfo getMatchInfoFromEntity(Entity e) throws JSONException {
    Map<String, Object> propsMap = e.getProperties();
    
    long matchId = e.getKey().getId();
    
    long gameId = (Long) propsMap.get(ContainerConstants.GAME_ID);
    List<Object> playerIdsObj = Utils.toList((JSONArray) (propsMap
        .get(ContainerConstants.PLAYER_IDS)));

    List<Long> playerIds = Lists.newArrayList();
    for (Object obj : playerIdsObj) {
      if (obj instanceof Long) {
        playerIds.add((Long) obj);
      }
    }

    long playerHasTurn = (Long) propsMap.get(ContainerConstants.PLAYER_THAT_HAS_TURN);

    Map<String, Object> gameOverScoreObj = Utils.toMap((JSONObject) propsMap
        .get(ContainerConstants.GAME_OVER_SCORES));

    Map<String, Long> gameOverScore = Maps.newHashMap();
    for (String key : gameOverScoreObj.keySet()) {
      gameOverScore.put(key, (Long) gameOverScoreObj.get(key));
    }

    // TODO Why GAME_OVER_REASON is a String??
    String gameOverReason = (String) propsMap.get(ContainerConstants.GAME_OVER_REASON);

    List<Object> hstryJsnAry = Utils.toList((JSONArray) propsMap.get(ContainerConstants.HISTORY));

    List<Map<String, Object>> hstryAry = Lists.newArrayList();

    for (Object obj : hstryJsnAry) {
      Map<String, Object> map = (Map<String, Object>) obj;
      hstryAry.add(map);
    }

    return new MatchInfo(matchId, gameId, playerIds, playerHasTurn, gameOverScore,
        gameOverReason, hstryAry);
  }

  public Map<String, Object> toMap() {
    Map<String, Object> res = Maps.newHashMap();
    res.put("matchId", matchId);
    res.put("gameId", gameId);
    res.put("playerIds", Lists.newArrayList(playerIds));
    res.put("playerIdThatHasTurn", playerIdThatHasTurn);
    res.put("gameOverScores", gameOverScores);
    res.put("gameOverReason", gameOverReason);
    return res;
  }
}
