
package org.smg.server.servlet.container;

import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.GameApi.GameState;

public class GameStateHistoryItem {
  /*
   * TODO Change this to List<Operations>
   */
  private List<Map<String, Object>> lastMove;

  private GameState currentState;

  public final List<Map<String, Object>> getLastMove() {
    return lastMove;
  }

  public final void setLastMove(List<Map<String, Object>> lastMove) {
    this.lastMove = lastMove;
  }

  public final GameState getCurrentState() {
    return currentState;
  }

  public final void setCurrentState(GameState currentState) {
    this.currentState = currentState;
  }
}
