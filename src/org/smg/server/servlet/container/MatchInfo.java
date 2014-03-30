
package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class MatchInfo {
  private long matchId;
  private long gameId;
  private List<Long> playerIds;
  private long playerThatHasTurn;
  private Map<String, Integer> gameOverScores;
  private String gameOverReason;
  private List<GameStateHistoryItem> history;
  private Map<Long, Long> playerIdToNumberOfTokensInPot;

  public static MatchInfo getMatchInfoFromEntity(Entity e) throws JSONException,
      JsonParseException, JsonMappingException, IOException {
    Map<String, Object> propsMap = e.getProperties();
    ObjectMapper mapper = new ObjectMapper();
    JSONObject jsnObj = new JSONObject(propsMap);

    // Makeup for "history"
    // TODO Delete this!
    String hstryStr = ((Text) jsnObj.get(ContainerConstants.HISTORY)).getValue();
    jsnObj.put(ContainerConstants.HISTORY, new JSONArray(hstryStr));
    /**
     * "[{\"gameState\":{\"state\":{\"key1\":\"val1\",\"key2\":\"val2\"},\"visibleTo\":{\"key3\":\"val3\",\"key4\":\"val4\"},\"playerIdToNumberOfTokensInPot\":{\"1\":111,\"2\":222}}},{\"gameState\":{\"state\":{\"key1\":\"val1\",\"key2\":\"val2\"},\"visibleTo\":{\"key3\":\"val3\",\"key4\":\"val4\"},\"playerIdToNumberOfTokensInPot\":{\"1\":111,\"2\":222}}}]"
     */

    // Makeup for "playerIds"
    // TODO Delete this!
    String plrsStr = (String) jsnObj.get(ContainerConstants.PLAYER_IDS);
    jsnObj.put(ContainerConstants.PLAYER_IDS, new JSONArray(plrsStr));

    // Makeup for "gameOverScores"
    // TODO Delete this!
    String gvsStr = (String) jsnObj.get(ContainerConstants.GAME_OVER_SCORES);
    jsnObj.put(ContainerConstants.GAME_OVER_SCORES, new JSONObject(gvsStr));

    MatchInfo mi = mapper.readValue(jsnObj.toString(), MatchInfo.class);
    return mi;
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

  public final long getPlayerThatHasTurn() {
    return playerThatHasTurn;
  }

  public final void setPlayerThatHasTurn(long playerThatHasTurn) {
    this.playerThatHasTurn = playerThatHasTurn;
  }

  public final Map<String, Integer> getGameOverScores() {
    return gameOverScores;
  }

  public final void setGameOverScores(Map<String, Integer> gameOverScores) {
    this.gameOverScores = gameOverScores;
  }

  public final String getGameOverReason() {
    return gameOverReason;
  }

  public final void setGameOverReason(String gameOverReason) {
    this.gameOverReason = gameOverReason;
  }

  public final List<GameStateHistoryItem> getHistory() {
    return history;
  }

  public final void setHistory(List<GameStateHistoryItem> history) {
    this.history = history;
  }

  public final Map<Long, Long> getPlayerIdToNumberOfTokensInPot() {
    return playerIdToNumberOfTokensInPot;
  }

  public final void setPlayerIdToNumberOfTokensInPot(Map<Long, Long> playerIdToNumberOfTokensInPot) {
    this.playerIdToNumberOfTokensInPot = playerIdToNumberOfTokensInPot;
  }
}
