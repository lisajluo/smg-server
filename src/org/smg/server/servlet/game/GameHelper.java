package org.smg.server.servlet.game;

import static org.smg.server.servlet.container.ContainerConstants.PLAYER_IDS;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.game.GameConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.container.ContainerConstants;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class GameHelper {
	/** The valid keys that we allow in the JSON input when the client is doing a
	 *POST request
	 *Keys not contained in the validParams will be filtered
	 */
	public static final String[] validParams = { HAS_TOKENS, PICS,
			DEVELOPER_ID, GAME_NAME, DESCRIPTION, URL, ACCESS_SIGNATURE };

	/**<p>
	 * Filter out all the keys that are not contained in the validParams in the
	 * JSON input when the client is doing a POST request
	 * 
	 * @param params a Map of the json input
	 * @param validParams an array of the desired keywords
	 * @return a map with all its keywords coming from the validParams
	 */
	public static Map<Object, Object> deleteInvalid(Map<Object, Object> params,
			String[] validParams) {
		Map<Object, Object> returnMap = new HashMap<Object, Object>();
		for (Map.Entry<Object, Object> entry : params.entrySet()) {
			if (Arrays.asList(validParams).contains(entry.getKey())) {
				if (entry.getKey() instanceof String) {
					returnMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		if (returnMap.containsKey(HAS_TOKENS) == false)
			returnMap.put(HAS_TOKENS, false);
		return returnMap;
	}

	/** <p> 
	 * Put the json-formatted data into response
	 * 
	 * @param jObj
	 * @param key
	 * @param value
	 * @param resp
	 * @return 
	 */
	public static void put(JSONObject jObj, String key, String value,
			HttpServletResponse resp) {
		try {
			jObj.put(key, value);
			resp.setContentType("text/plain");
			jObj.write(resp.getWriter());
		} catch (Exception e) {
			return;
		}
	}

	/**<p>
	 *  Check whether the URL the client is requesting is correct
	 * @param pathInfo indicating the subPath after this servlet URL
	 * @return a boolean indicating whether the URL path is valid
	 */
	public static boolean parsePathForPost(String pathInfo) {
		if (pathInfo == null)
			return true;
		if (pathInfo.length() > 0) {
			if (pathInfo.length() == 1) {
				if (pathInfo.charAt(0) != '/') {
					return false;
				} else
					return true;
			}
			return false;

		}
		return true;
	}

	/**
	 *  Read data from the datastore to check whether the userId exists or not
	 * @param idAsStr the string format of the userId
	 * @return whether the userId exists in the datastore
	 */
	@SuppressWarnings("rawtypes")
	public static boolean userIdExists(String idAsStr) {
		try {
			long userId = Long.parseLong(idAsStr);
			Map user = UserDatabaseDriver.getUserMap(userId);
			if (user == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 *  Read from data store to check whether the game name is
	 *  duplicated except for the designated gameId (usually the game itself)
	 *  This method is usually called when the client wants to update a game
	 *    
	 * @param gameId the designated gameId 
	 * @param parameterMap the map of the json input
	 * @return whether the gameName is duplicated
	 */

	public static boolean gameNameDuplicate(long gameId,
			Map<Object, Object> parameterMap) {
		return GameDatabaseDriver.checkGameNameDuplicate(gameId, parameterMap);
	}

	/**
	 *  Read from the data store to check whether the gameName in the
	 *  parameterMap coincides with any other game name in the datastore
	 *  
	 *  @param parameterMap the map of the input json content
	 *  @return whether the gameName is duplicated in the datastore
	*/ 
	public static boolean gameNameDuplicate(Map<Object, Object> parameterMap) {
		return GameDatabaseDriver.checkGameNameDuplicate(parameterMap);
	}

	/**
	 *  Check whether the required field(developerId) is present when the client
	 *   is doing a game update
	 * @param parameterMap the map of the input json content
	 * @return return whether the map contains all the required infomation
	 */
	
	public static boolean requiredFieldForUpdate(Map<Object, Object> parameterMap) {
		if (parameterMap.get(DEVELOPER_ID) == null)
			return false;
		return true;
	}

	/**
	 *  Check whether the required fields are present when client is 
	 *  submitting a game
	 *  @param parameterMap the map of the input json content
	 *  @return whether all the required fields are contained in the json input
	 */
	
	public static boolean requiredFieldsComplete(Map<Object, Object> parameterMap) {
		if (parameterMap.get(DEVELOPER_ID) == null) {
			return false;
		}

		if (parameterMap.get(GAME_NAME) == null) {
			return false;
		}

		if (parameterMap.get(DESCRIPTION) == null) {
			return false;
		}

		if (parameterMap.get(URL) == null) {
			return false;
		}

		if (parameterMap.get(ACCESS_SIGNATURE) == null) {
			return false;
		}
		return true;

	}
  
  /**
   * Check from the datastore to see whether the gameId exists
   * @param gameId the long value of the gameId
   * @return whether the gameId exists
   */
  public static boolean gameIdExist(long gameId) {
    try {
      return GameDatabaseDriver.checkGameIdExists(gameId);
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }
  /**
   * Read from the datastore to put the meta info of the game 
   * into the response
   * @param gameId  
   * @param resp 
   * @throws IOException
   * @throws JSONException
   * 
   */
  
	public static void returnMetaInfo(long gameId, HttpServletResponse resp)
			throws IOException, JSONException {
		JSONObject metainfo = new JSONObject();

		Entity targetEntity;
		try {

			targetEntity = GameDatabaseDriver.getGame(gameId);
			Map<String, Object> statsInfo = GameDatabaseDriver
					.getStatsHelper(targetEntity.getKey().getId());
			if (statsInfo != null && statsInfo.containsKey(RATING) == true)
				metainfo.put(RATING, statsInfo.get(RATING));
			metainfo.put(GAME_NAME, targetEntity.getProperty(GAME_NAME));
			metainfo.put(HAS_TOKENS, targetEntity.getProperty(HAS_TOKENS));
			metainfo.put(URL, targetEntity.getProperty(URL));
			metainfo.put(DESCRIPTION, targetEntity.getProperty(DESCRIPTION));
			metainfo.put(POST_DATE, targetEntity.getProperty(POST_DATE));
			if (targetEntity.hasProperty(PICS)) {
				Text picText = (Text) targetEntity.getProperty(PICS);
				JSONObject picMap = new JSONObject(picText.getValue());
				metainfo.put(PICS, picMap);
			}
			metainfo.put(DEVELOPER_ID, targetEntity.getProperty(DEVELOPER_ID));

			metainfo.write(resp.getWriter());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	   * Send error message to the client with human readable details, and json
	   * string received from client. Check {@link GameConstants} to find all
	   * the error type and msg.
	   * 
	   * @param resp
	   * @param returnValue
	   * @param errorMSG
	   * @param details
	   * @param json
	   */
	
	public static void sendErrorMessageForJson(HttpServletResponse resp,
		      JSONObject returnValue, String errorMSG, String details, String json) {
		    try {
		      returnValue.put(ERROR, errorMSG);
		      returnValue.put(DETAILS, details);
		      returnValue.put(JSON_RECEIVED, new JSONObject(json));
		      returnValue.write(resp.getWriter());
		    } catch (JSONException | IOException e) {
		      e.printStackTrace();
		    }
		  }
	/**
	   * Send error message to the client with human readable details, and the
	   * absolute URL that the client is making request to. Check {@link GameConstants} 
	   * to find all the error type and msg.
	   * 
	   * @param resp
	   * @param returnValue
	   * @param errorMSG
	   * @param details
	   * @param json
	   */
	public static void sendErrorMessageForUrl(HttpServletResponse resp,
		      JSONObject returnValue, String errorMSG, String details, String json) {
		    try {
		      returnValue.put(ERROR, errorMSG);
		      returnValue.put(DETAILS, details);
		      returnValue.put(URL, json);
		      returnValue.write(resp.getWriter());
		    } catch (JSONException | IOException e) {
		      e.printStackTrace();
		    }
		  }
	/**
	 * Parse the list of Entities into a list of JSON object
	 * 
	 * @param unFinishedMatch a List of unFinishedMatch entities
	 * @return
	 * @throws Exception
	 */
	public static List<JSONObject> parseUnfinished(List<Entity> unFinishedMatch)
			throws Exception {
		List<JSONObject> parseResult = new ArrayList<JSONObject>();
		for (int i = 0; i < unFinishedMatch.size(); i++) {
			Map<String, Object> currentRecord = new HashMap<String, Object>();
			Entity match = unFinishedMatch.get(i);
			String playerIdListStr = (String) match.getProperty(PLAYER_IDS);
			JSONArray playerIdJson = new JSONArray(playerIdListStr);
			List<JSONObject> parsedPlayerIdInfo = new ArrayList<JSONObject>();
			for (int j = 0; j < playerIdJson.length(); j++) {
				String currentId = (String) playerIdJson.get(j);
				Map<String, String> playerInfo = DatabaseDriverPlayer
						.getPlayerNames(Long.parseLong(currentId));
				JSONObject nameInfo = new JSONObject(playerInfo);
				parsedPlayerIdInfo.add(nameInfo);
			}
			currentRecord.put(PLAYER_ID, parsedPlayerIdInfo);
			parseResult.add(new JSONObject(currentRecord));
		}
		return parseResult;
	}
    /**
     * Merge all the info into a single map
     * @param statsInfo the mapping of statsInfo
     * @param unFinishedMatch the list of unFinishedMatch entities
     * @param finishedMatch the list of FinishedMatch entities
     * @return the merged map
     * @throws Exception
     */
	@SuppressWarnings("rawtypes")
	public static  Map<String, Object> parseStats(Map<String, Object> statsInfo,
			List<Entity> unFinishedMatch, List finishedMatch) throws Exception {
		Map<String, Object> parsedStats = new HashMap<String, Object>();
		parsedStats.put(HIGH_SCORE, statsInfo.get(HIGH_SCORE));
		parsedStats.put(RATING, statsInfo.get(RATING));
		List<JSONObject> unfinished = parseUnfinished(unFinishedMatch);
		parsedStats.put(CURRENT_GAMES, unfinished);
		parsedStats.put(FINISHED_GAMES, finishedMatch);

		return parsedStats;

	}
    /**
     * Return a list of JSONObject of all games metaInfo from the dataStore
     * @return
     */
	public static  List<JSONObject> returnAllGameInfo() {
		return GameDatabaseDriver.getGameInfo(false, -1);
	}
    /**
     * Return a list of game metaInfo from the dataStore within a single developerId
     * @param developerId  The developerId given by the client
     * @return  
     */
	public static  List<JSONObject> returnGameInfoByDeveloper(long developerId) {
		return GameDatabaseDriver.getGameInfo(true, developerId);
	}

}
