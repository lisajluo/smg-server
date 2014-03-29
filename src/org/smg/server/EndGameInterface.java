package org.smg.server;

import java.util.Map;

/**
 * When a match ended, update winInfo.
 *
 */
public interface EndGameInterface {
  public abstract void updateStats(Map<String, Object> winInfo);
}
