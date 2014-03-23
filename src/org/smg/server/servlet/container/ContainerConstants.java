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
 
 public static final String GAME_STATE = "gameState";
 public static final String LAST_MOVE = "lastMove";
 public static final String OPERATIONS = "operations";
 public static final String STATE = "state";
 public static final String VISIBLE_TO = "visibleTo";
 public static final String PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT = "playerIdToNumberOfTokensInPot";
 
 
 // Various properties in the Player
 public static final String PLAYER_ID = "playerId";
 public static final String ACCESS_SIGNATURE = "accessSignature";
 
 // Error messages
 public static final String ERROR = "error";
 public static final String NO_DATA_RECEIVED = "NO_DATA_RECEIVED";
 public static final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
 public static final String WRONG_GAME_ID = "WRONG_GAME_ID";
 public static final String WRONG_MATCH_ID = "WROING_MATCH_ID";
 public static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
 
}
