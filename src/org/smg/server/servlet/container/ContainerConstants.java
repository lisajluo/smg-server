package org.smg.server.servlet.container;

import org.smg.server.util.NamespaceUtil;

/**
 * Constant used for container component including 1. Entity Name in datastore
 * 2. Error/Success Msg 3. JSON object field name
 * 
 * @author piper
 * 
 */
public final class ContainerConstants {
  private ContainerConstants() {
  }

  // entity Name
  public static final String MATCH = NamespaceUtil.VERSION + "Match";
  public static final String QUEUE = NamespaceUtil.VERSION + "Queue";

  // Various properties in the Match
  public static final String MATCH_ID = "matchId";
  public static final String PLAYER_IDS = "playerIds";
  public static final String GAME_ID = "gameId";
  public static final String PLAYER_THAT_HAS_TURN = "playerThatHasTurn";
  public static final String GAME_OVER_SCORES = "gameOverScores";
  public static final String GAME_OVER_REASON = "gameOverReason";
  public static final String PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT = "playerIdToNumberOfTokensInPot";
  public static final String HISTORY = "history";

  public static final String PLAYER_THAT_HAS_LAST_TURN = "playerThatHasLastTurn";
  // GameOverReason
  public static final String NOT_OVER = "Not Over";
  // Normal Over
  public static final String OVER = "Over";
  public static final String TIME_OUT = "Time Out";
  public static final String QUIT = "Quit";

  // Various properties in Queue: queueId, gameId, playerId, channelToken,
  // enqueueTime
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

  // json key for Player
  public static final String ACCESS_SIGNATURE = "accessSignature";

  // Error messages
  public static final String ERROR = "error";
  public static final String DETAILS = "details";
  public static final String JSON_RECEIVED = "json_received"; 
  public static final String NO_DATA_RECEIVED = "NO_DATA_RECEIVED";
  public static final String MISSING_INFO = "MISSING_INFO";
  public static final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
  public static final String WRONG_GAME_ID = "WRONG_GAME_ID";
  public static final String NO_MATCH_FOUND = "NO_MATCH_FOUND";
  public static final String WRONG_MATCH_ID = "WROING_MATCH_ID";
  public static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
  public static final String BUILD_CHANNEL_FIALED = "BUILD_CHANNEL_FAILED";
  public static final String JSON_PARSE_ERROR = "JSON_PARSE_ERROR";
  public static final String ENQUEUE_FAILED = "ENQUEUE_FAILED";
  public static final String MATCH_ENDED = "MATCH_ENDED";
  

  // Success message
  public static final String SUCCESS = "success";
  public static final String BUILD_CHANNEL_SUCCESS = "BUILD_CHANNEL_SUCCESS";

  // Message
  public static final String MESSAGE = "message";
  public static final String OPPONENTS_LOST_CONNECTION = "OPPONENTS_LOST_CONNECTION";
}
