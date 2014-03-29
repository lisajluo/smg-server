package org.smg.server.database;

import java.util.Map;

/**
 * When a match ended, update winInfo.
 */
public interface EndGameInterface {
  public void updateStats(Map<String, Object> winInfo);
}