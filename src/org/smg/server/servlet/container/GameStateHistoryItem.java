
package org.smg.server.servlet.container;

import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.GameApi.GameState;

/**
 * This is a model for Game State Item in database. This is an exactly mapped
 * item for Game History Item.
 */
public class GameStateHistoryItem {
    /*
     * TODO Change this to List<Operations>
     */
    private List<Map<String, Object>> lastMove;

    private GameState gameState;

    public final List<Map<String, Object>> getLastMove() {
        return lastMove;
    }

    public final void setLastMove(List<Map<String, Object>> lastMove) {
        this.lastMove = lastMove;
    }

    public final GameState getGameState() {
        return gameState;
    }

    public final void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}
