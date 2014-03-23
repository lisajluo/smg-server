
package org.smg.server.servlet.container;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MatchInfo {
    private int matchId;
    private int gameId;
    private List<Integer> playerIds;
    private int playerIdThatHasTurn;
    private Map<String, Integer> gameOverScores;
    private GameOverReason gameOverReason;

    public static enum GameOverReason {
        NOT_OVER
    }

    public MatchInfo(int matchId, int gameId, List<Integer> playerIds, int playerIdThatHasTurn,
            Map<String,Integer> gameOverScores, GameOverReason gameOverReason) {
        super();
        this.matchId = matchId;
        this.gameId = gameId;
        this.playerIds = playerIds;
        this.playerIdThatHasTurn = playerIdThatHasTurn;
        this.gameOverScores = gameOverScores;
        this.gameOverReason = gameOverReason;
    }

    public final int getMatchId() {
        return matchId;
    }

    public final void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public final int getGameId() {
        return gameId;
    }

    public final void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public final List<Integer> getPlayerIds() {
        return playerIds;
    }

    public final void setPlayerIds(List<Integer> playerIds) {
        this.playerIds = playerIds;
    }

    public final int getPlayerIdThatHasTurn() {
        return playerIdThatHasTurn;
    }

    public final void setPlayerIdThatHasTurn(int playerIdThatHasTurn) {
        this.playerIdThatHasTurn = playerIdThatHasTurn;
    }

    public final Map<String,Integer> getGameOverScores() {
        return gameOverScores;
    }

    public final void setGameOverScores(Map<String,Integer> gameOverScores) {
        this.gameOverScores = gameOverScores;
    }

    public final GameOverReason getGameOverReason() {
        return gameOverReason;
    }

    public final void setGameOverReason(GameOverReason gameOverReason) {
        this.gameOverReason = gameOverReason;
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
