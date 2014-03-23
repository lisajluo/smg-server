
package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class MatchInfo {
  private long matchId;
  private long gameId;
  private List<Long> playerIds;
  private long playerIdThatHasTurn;
  private Map<String, Long> gameOverScores;
  private String gameOverReason;
  private List<GameStateHistoryItem> gameStateHistory;

  @SuppressWarnings("unchecked")
  public static MatchInfo getMatchInfoFromEntity(Entity e) throws JSONException,
      JsonParseException, JsonMappingException, IOException {
    Map<String, Object> propsMap = e.getProperties();
    ObjectMapper mapper = new ObjectMapper();
    JSONObject jsnObj = new JSONObject(propsMap);
    MatchInfo mi = mapper.readValue(jsnObj.toString(), MatchInfo.class);
    return mi;

    // long matchId = e.getKey().getId();
    //
    // long gameId = (Long) propsMap.get(ContainerConstants.GAME_ID);
    // List<Object> playerIdsObj = Utils.toList((JSONArray) (propsMap
    // .get(ContainerConstants.PLAYER_IDS)));
    //
    // List<Long> playerIds = Lists.newArrayList();
    // for (Object obj : playerIdsObj) {
    // if (obj instanceof Long) {
    // playerIds.add((Long) obj);
    // }
    // }
    //
    // long playerHasTurn = (Long)
    // propsMap.get(ContainerConstants.PLAYER_THAT_HAS_TURN);
    //
    // Map<String, Object> gameOverScoreObj = Utils.toMap((JSONObject) propsMap
    // .get(ContainerConstants.GAME_OVER_SCORES));
    //
    // Map<String, Long> gameOverScore = Maps.newHashMap();
    // for (String key : gameOverScoreObj.keySet()) {
    // gameOverScore.put(key, (Long) gameOverScoreObj.get(key));
    // }
    //
    // // TODO Why GAME_OVER_REASON is a String??
    // String gameOverReason = (String)
    // propsMap.get(ContainerConstants.GAME_OVER_REASON);
    //
    // List<Object> hstryJsnAry = Utils.toList((JSONArray)
    // propsMap.get(ContainerConstants.HISTORY));
    //
    // List<Map<String, Object>> hstryAry = Lists.newArrayList();
    //
    // for (Object obj : hstryJsnAry) {
    // Map<String, Object> map = (Map<String, Object>) obj;
    // hstryAry.add(map);
    // }
    //
    // return new MatchInfo(matchId, gameId, playerIds, playerHasTurn,
    // gameOverScore,
    // gameOverReason, hstryAry);
  }

  public MatchInfo(long matchId, long gameId, List<Long> playerIds, long playerIdThatHasTurn,
      Map<String, Long> gameOverScores, String gameOverReason,
      List<GameStateHistoryItem> gameStateHistory) {
    super();
    this.matchId = matchId;
    this.gameId = gameId;
    this.playerIds = playerIds;
    this.playerIdThatHasTurn = playerIdThatHasTurn;
    this.gameOverScores = gameOverScores;
    this.gameOverReason = gameOverReason;
    this.gameStateHistory = gameStateHistory;
  }

  public final long getMatchId() {
    return matchId;
  }

  public final void setMatchId(long matchId) {
    this.matchId = matchId;
  }

  public final long getGameId() {
    return gameId;
  }

  public final void setGameId(long gameId) {
    this.gameId = gameId;
  }

  public final List<Long> getPlayerIds() {
    return playerIds;
  }

  public final void setPlayerIds(List<Long> playerIds) {
    this.playerIds = playerIds;
  }

  public final long getPlayerIdThatHasTurn() {
    return playerIdThatHasTurn;
  }

  public final void setPlayerIdThatHasTurn(long playerIdThatHasTurn) {
    this.playerIdThatHasTurn = playerIdThatHasTurn;
  }

  public final Map<String, Long> getGameOverScores() {
    return gameOverScores;
  }

  public final void setGameOverScores(Map<String, Long> gameOverScores) {
    this.gameOverScores = gameOverScores;
  }

  public final String getGameOverReason() {
    return gameOverReason;
  }

  public final void setGameOverReason(String gameOverReason) {
    this.gameOverReason = gameOverReason;
  }

  public final List<GameStateHistoryItem> getGameStateHistory() {
    return gameStateHistory;
  }

  public final void setGameStateHistory(List<GameStateHistoryItem> gameStateHistory) {
    this.gameStateHistory = gameStateHistory;
  }

  // public Map<String, Object> toMap() {
  // Map<String, Object> res = Maps.newHashMap();
  // res.put("matchId", matchId);
  // res.put("gameId", gameId);
  // res.put("playerIds", Lists.newArrayList(playerIds));
  // res.put("playerIdThatHasTurn", playerIdThatHasTurn);
  // res.put("gameOverScores", gameOverScores);
  // res.put("gameOverReason", gameOverReason);
  // return res;
  // }
}
