package org.smg.server.servlet.container;


public final class ContainerConstants {
 private ContainerConstants() {}
  
 //entity Name
 public static final String MATCH = "Match";
 public static final String PLAYER = "Player";
 public static final String GAME = "Game";
 
 // Various properties in the Match
 public static final String MATCH_ID = "matchId";
 public static final String PLAYER_IDS = "playerIds";
 public static final String GAME_ID = "gameId";
 public static final String PLAYER_THAT_HAS_TURN = "playerThatHasTurn";
 public static final String GAME_OVER_SCORES = "gameOverScores";
 public static final String GAME_OVER_REASON = "gameOverReason";
 public static final String HISTORY = "history";
 public static final String ACCESS_SIGNATURE = "accessSignature";
 
 // Various properties in the Player
 public static final String PLAYER_ID = "playerId";
 
 // Error messages
 public static final String ERROR = "error";
 public static final String NO_DATA_RECEIVED = "NO_DATA_RECEIVED";
 public static final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
 public static final String WRONG_GAME_ID = "WRONG_GAME_ID";
 public static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
 
}

