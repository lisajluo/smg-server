import java.util.Map;

import com.google.common.collect.Maps;

public class GameStateeee {

  private Map<String, Object> state = Maps.newHashMap();
  private Map<String, Object> visibleTo = Maps.newHashMap();
  private Map<Integer, Integer> playerIdToNumberOfTokensInPot = Maps.newHashMap();

  public final Map<String, Object> getState() {
    return state;
  }

  public final void setState(Map<String, Object> state) {
    this.state = state;
  }

  public final Map<String, Object> getVisibleTo() {
    return visibleTo;
  }

  public final void setVisibleTo(Map<String, Object> visibleTo) {
    this.visibleTo = visibleTo;
  }

  public final Map<Integer, Integer> getPlayerIdToNumberOfTokensInPot() {
    return playerIdToNumberOfTokensInPot;
  }

  public final void setPlayerIdToNumberOfTokensInPot(
      Map<Integer, Integer> playerIdToNumberOfTokensInPot) {
    this.playerIdToNumberOfTokensInPot = playerIdToNumberOfTokensInPot;
  }

}
