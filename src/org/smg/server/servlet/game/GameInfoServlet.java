package org.smg.server.servlet.game;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.game.GameUtil.*;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_IDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;
import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.GameDatabaseDriver;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class GameInfoServlet extends HttpServlet {
	@Override
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
	}

	
    /**
     * 
     * @param req  the HttpRequest for getting metainfo of multiple games
     * @param resp the HttpResponse 
     */
	private void getGameQuery(HttpServletRequest req, HttpServletResponse resp) {
		String developerIdStr = req.getParameter(DEVELOPER_ID);
		JSONObject jObj = new JSONObject();
		if (developerIdStr == null) {

			List<JSONObject> queryResult = GameDatabaseDriver
					.getGameInfoAsJSON(true);
			for (JSONObject game : queryResult) {
				if (game.has(AUTHORIZED) == true)
					game.remove(AUTHORIZED);
				if (game.has(UPDATED) == true)
					game.remove(UPDATED);
			}
			put(queryResult, resp);
			return;
		} else {
			long developerId = Long.parseLong(developerIdStr);
			String accessSignature = req.getParameter(ACCESS_SIGNATURE);
			try {
				boolean verify = DeveloperDatabaseDriver.verifyDeveloperAccess(
						developerId, accessSignature);
				if (verify == false) {
					 String json = GameUtil.getFullURL(req);
					 String details = "Your access signature is incorrect";
			         GameHelper.sendErrorMessageForUrl(resp, jObj,WRONG_ACCESS_SIGNATURE, details,
								json); 
					return;
				}
				List<JSONObject> queryResult = GameHelper.returnGameInfoByDeveloper(developerId);
				for (JSONObject game : queryResult) {
					if (game.has(UPDATED) == true)
						game.remove(UPDATED);
				}
				put(queryResult, resp);
				return;
			} catch (Exception e) {
				 String json = GameUtil.getFullURL(req);
				 String details = "The developerId/userId does not exist";
				 GameHelper.sendErrorMessageForUrl(resp, jObj,WRONG_DEVELOPER_ID, details,
							json); 
			}

		}
	}
    /**
     * 
     * @param req the HttpRequest for getting stats of a particular game
     * @param resp
     * @throws Exception
     */
	@SuppressWarnings("rawtypes")
	private void getStatsQuery(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		String gameIdStr = req.getParameter(GAME_ID);
		JSONObject jObj = new JSONObject();
		try {
			Map<String, Object> statsInfo = GameDatabaseDriver.getStats(Long
					.parseLong(gameIdStr));
			if (statsInfo == null)
				throw new Exception();
			List<Entity> unFinishedMatch = ContainerDatabaseDriver
					.getAllUnfinishedMatchesByGameID(Long.parseLong(gameIdStr));
			List finishedMatch = (List) statsInfo.get(FINISHED_GAMES);
			Map<String, Object> parsedInfo = GameHelper.parseStats(statsInfo,
					unFinishedMatch, finishedMatch);
			jObj = new JSONObject(parsedInfo);
			put(jObj, resp);
			return;

		} catch (Exception e) {
			String json = GameUtil.getFullURL(req);
			String details = "The game you are looking for does not exist";
			GameHelper.sendErrorMessageForUrl(resp, jObj, WRONG_GAME_ID,
					details, json);
			return;
		}
	}
    /**
     * doPost is called when a client wants to update the rating of a game
     * A successful response:
     * { “rating”: 4.5653948 // number: the average rating }
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		String line = new String();
		StringBuffer buffer = new StringBuffer();
		Map<Object, Object> parameterMap = new HashMap<Object, Object>();
		try {
			if (req.getPathInfo().substring(1).equals(RATING) == false)
				throw new IOException();
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			parameterMap = (Map) JSONUtil.parse(buffer.toString());
			String gameIdStr = (String) parameterMap.get(GAME_ID);
			if (GameDatabaseDriver.checkGameIdExists(Long.parseLong(gameIdStr)) == false) {
		    	String details = "The game Id you are posting to does not exist in the datastore";
		    	GameHelper.sendErrorMessageForJson(resp, jObj,
		        		  WRONG_GAME_ID, details, buffer.toString());   
				return;

			}

			try {
				DatabaseDriverPlayer.getPlayerById(Long
						.parseLong((String) parameterMap.get(PLAYER_ID)));
			} catch (Exception e) {
				String details = "The playerId/userId does not exist in the datastore";
		    	GameHelper.sendErrorMessageForJson(resp, jObj,
		        		  WRONG_PLAYER_ID, details, buffer.toString());   
		    	return;
			}
			if (signatureRightForPlayer(parameterMap) == false) {
				String details = "Your access signature is incorrect";
				GameHelper.sendErrorMessageForJson(resp, jObj,
						WRONG_ACCESS_SIGNATURE, details, buffer.toString());
				return;

			}
			double rating = 0;
			try {
				rating = Double.parseDouble((String) parameterMap.get(RATING));
				if (rating >= 0 && rating <= 5) {
					double updatedRating = GameDatabaseDriver.updateRatings(
							Long.parseLong(gameIdStr), rating);
					jObj.put(RATING, String.valueOf(updatedRating));
					put(jObj, resp);
					return;
				} else {
					throw new IOException();
				}
			} catch (Exception e) {
				String details = "The rating value is invalid, should be within range of 0-5";
				GameHelper.sendErrorMessageForJson(resp, jObj, WRONG_RATING, details,
						buffer.toString());
				return;
			}

		} catch (Exception e) {
			String json = GameUtil.getFullURL(req);
			String details = "The url you are posting is incorrect, try:POST localhost:8888/gameinfo/rating";
			GameHelper.sendErrorMessageForUrl(resp, jObj,
	        		  URL_ERROR, details, json);  
		}

	}

	/**
	 * DoGet is called when the client wants to get the meta info of multiple
	 * games or get all the match stats of a particular game
	 * 
	 * GET /gameinfo/all GET /gameinfo/all?developerId=....&accessSignature=....
	 * NOTE : If called by developer, then only returns games for that
	 * developerId, Get all games will only return games which are authorized by
	 * our admin, however, if we do query by developerId, we will return all the
	 * games (including both authorized and unauthorized ) by the developer The
	 * successful JSON response is wrapped in a json array
	 * 
	 * [ { “developer”: {// might be empty (if developer did not specify name)
	 * firstName: “Bob”, nickname: “Ninja” }, “gameId”: “345984508”,//string
	 * “gameName”: “Cheat”, "description": "Game description.", “url”:
	 * “http://www.cheatgame.com”, “hasTokens”: false, //boolean “authorized” :
	 * true //This field will only appear when we query by developerId, which
	 * indicates whether or not your game is authorized “rating” : 100 //If
	 * there’s no rating record, then this field will not exist "pics": { //
	 * optional “icon”: "http://www.foo.com/bar1.gif", “screenshots”:
	 * [“http://www.foo.com/bar2.gif”] } }, {..another game... } ,{...} ]
	 * 
	 * GET /gameinfo/stats?gameId=... 
	 * Can be called by either player or
	 * developer. The successful response of getting stats info: 
	 * { 
	 * “highScore”:   { “playerId”: "yoav", // string 
	 * “score”: 100 // number } //null if there is no high score 
	 * “rating”: 3.5965896, // number (double in the range [0-5]),  null if no rating 
	 * “currentGames”: [ { “players”: [ { “firstName”: “Bob”,
	 * “nickname”: “Ninja” }, { “firstName”: “Jane”, “nickname”: “Ninja2” } ] },
	 * { …. } ], 
	 * “finishedGames”: [ { “players”: [ { “firstName”: “Bob”,
	 * “nickname”: “Ninja”, “score”: 43543,  “tokens:” 2394384 // (for
	 * games that have tokens) }, { “firstName”: “Jane”, “nickname”: “Ninja”,
	 * “score”: 9845,  “tokens”: 39843  } ] } ....{}] }
	 */
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		try {
			if (req.getPathInfo().substring(1).equals(ALL)) {
				getGameQuery(req, resp);
				return;
			}
			if (req.getPathInfo().substring(1).equals(STATS)) {
				getStatsQuery(req, resp);
				return;
			}
			throw new IOException();
		} catch (Exception e) {
			String json = GameUtil.getFullURL(req);
			String details = "The url you are requesting is not correct, please refer to server API for correct URL path";
			GameHelper.sendErrorMessageForUrl(resp, jObj,
	        		  URL_ERROR, details, json);  
			return;
		}

	}
}
