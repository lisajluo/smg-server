package org.smg.server;

import java.util.Map;

/**
 * When a match ended, update stats.
 * @author piper
 *
 */
public interface EndGameInterface {
  public abstract void updateStats(Map<String, Object> winInfo);
}
