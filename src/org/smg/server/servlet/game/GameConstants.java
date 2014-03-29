package org.smg.server.servlet.game;

public final class GameConstants {
	  private GameConstants() { }  // Prevent instantiation/subclassing
	
	  // Game entity
	  public static final String DEVELOPER = "Developer";
	  public static final String SCREENSHOT = "screenshot";
	  public static final String ICON = "icon";
	  public static final String DEVELOPER_ID = "developerId";
	  public static final String GAME_NAME = "gameName";
	  public static final String DESCRIPTION = "description";
	  public static final String URL = "url";
	  public static final String WIDTH = "width";
	  public static final String HEIGHT = "height";
	  public static final String GAME_ID = "gamdId" ;
	  public static final String ACCESS_SIGNATURE = "accessSignature";
	  public static final String HAS_TOKENS = "hasTokens";
	  public static final String POST_DATE = "postDate";
	  
	  
	  // Error messages
	  static final String ERROR = "error";
	  static final String MISSING_INFO = "MISSING_INFO";
	  static final String GAME_EXISTS = "GAME_EXISTS";
	  static final String WRONG_GAME_ID = "WRONG_GAME_ID";
	  static final String WRONG_DEVELOPER_ID = "WRONG_DEVELOPER_ID";
	  static final String WRONG_PASSWORD = "WRONG_PASSWORD";
	  static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
	  static final String INVALID_JSON = "INVALID_JSON";
	  static final String URL_ERROR = "INVALID_URL_PATH_ERROR";
	  
	  // Success messages
	  
	  static final String SUCCESS = "success";
	  static final String DELETED_DEVELOPER = "DELETED_DEVELOPER";
	  static final String DELETED_GAME = "DELETED_GAME";
	  static final String UPDATED_GAME = "UPDATED_GAME";
	  
	  // Invalid indicator
	  static final int INVALID = -1;
	  
	  
	  public static final String GAME_META_INFO = "gameMetaInfo";
	  public static final String VERSION_ONE = "versionOne" ; 
	  
	  // Statistics table
	  public static final String STATISTICS = "STATISTICS";
	  // Statistics properties
	  public static final String HIGH_SCORE = "highScore";
	  public static final String SCORE = "score";
	  public static final String RATING = "rating";
	  public static final String RATING_COUNT = "ratingCount";
	  public static final String AVERAGE_RATING = "averageRating";
	  public static final String ZERO_STRING = "0";
	  public static final String FINISHED_GAMES = "finishedGames";
	  public static final String PLAYERS = "players";
	  public static final String FIRST_NAME = "firstname";
	  public static final String NICKNAME = "nickname";
	  public static final String TOKENS = "tokens";
	}
