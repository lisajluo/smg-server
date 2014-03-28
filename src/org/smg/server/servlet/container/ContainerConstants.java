package org.smg.server.servlet.container;

public final class ContainerConstants {
 private ContainerConstants() {}
  
 //entity Name
 public static final String MATCH = "Match";
 public static final String QUEUE = "Queue";
 public static final String PLAYER = "PLAYER";
 public static final String GAME = "gameMetaInfo";

 // Various properties in the Match
 public static final String MATCH_ID = "matchId";
 public static final String PLAYER_IDS = "playerIds";
 public static final String GAME_ID = "gameId";
 public static final String PLAYER_THAT_HAS_TURN = "playerThatHasTurn";
 public static final String GAME_OVER_SCORES = "gameOverScores";
 public static final String GAME_OVER_REASON = "gameOverReason";
 public static final String PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT = "playerIdToNumberOfTokensInPot";
 public static final String HISTORY = "history";
 
 // GameOverReason
 public static final String NOT_OVER = "Not Over";
 public static final String OVER = "Over";
 public static final String TERMINATED = "Terminated";
 
 // Various properties in Queue: queueId, gameId, playerId, channelToken, enqueueTime
 public static final String QUEUE_ID = "queueId";
 public static final String PLAYER_ID = "playerId";
 public static final String CHANNEL_TOKEN = "channelToken";
 public static final String ENQUEUE_TIME = "enqueueTime";
 
 public static final String GAME_STATE = "gameState";
 public static final String LAST_MOVE = "lastMove";
 public static final String VERIFIED_BY = "verifiedBy";
 public static final String OPERATIONS = "operations";
 public static final String STATE = "state";
 public static final String VISIBLE_TO = "visibleTo";
 
 // Various properties in the Player
 public static final String ACCESS_SIGNATURE = "accessSignature";
 public static final String DS_ACCESS_SIGNATURE = "ACCESSSIGNATURE";
 
 // Error messages
 public static final String ERROR = "error";
 public static final String NO_DATA_RECEIVED = "NO_DATA_RECEIVED";
 public static final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
 public static final String WRONG_GAME_ID = "WRONG_GAME_ID";
 public static final String WRONG_MATCH_ID = "WROING_MATCH_ID";
 public static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
 public static final String BUILD_CHANNEL_FIALED = "BUILD_CHANNEL_FAILED";
 public static final String JSON_PARSE_ERROR = "JSON_PARSE_ERROR";
 public static final String ENQUEUE_FAILED = "ENQUEUE_FAILED";
 
 // Success message
 public static final String SUCCESS = "success";
 public static final String BUILD_CHANNEL_SUCCESS = "BUILD_CHANNEL_SUCCESS";
 
}
