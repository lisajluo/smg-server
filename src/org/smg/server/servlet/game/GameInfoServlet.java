package org.smg.server.servlet.game;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.game.GameUtil.*;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_IDS;
import static org.smg.server.servlet.container.ContainerConstants.GAME_OVER_SCORES;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT;

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
  private List<JSONObject> parseUnfinished(List<Entity> unFinishedMatch) throws Exception
  {
	  //TODO: implement how to parse
	  List<JSONObject> parseResult = new ArrayList<JSONObject> ();
	  for (int i=0;i<unFinishedMatch.size();i++)
	  {
		  Map<String,Object> currentRecord = new HashMap<String,Object> ();
		  Entity match = unFinishedMatch.get(i);
		  //Map<String,Object> matchProperties = match.getProperties();
		  /*for (String key: matchProperties.keySet())
		  {
			  if (key.equals(PLAYER_IDS))
			  currentRecord.put(key, currentRecord.get(key));
		  }*/
		  String playerIdListStr = (String)match.getProperty(PLAYER_IDS);
		  JSONArray playerIdJson = new JSONArray(playerIdListStr);
		  List<JSONObject> parsedPlayerIdInfo = new ArrayList<JSONObject> ();
		  for (int j = 0;j<playerIdJson.length();j++)
		  {
			  String currentId = (String)playerIdJson.get(j);
			  Map<String,String> playerInfo = DatabaseDriverPlayer.getPlayerNames(Long.parseLong(currentId));
			  JSONObject nameInfo = new JSONObject (playerInfo);
			  parsedPlayerIdInfo.add(nameInfo);
		  }
		  currentRecord.put(PLAYER_ID, parsedPlayerIdInfo);
		  parseResult.add(new JSONObject(currentRecord));			  		  
	  }
	  return parseResult;
  }
  private List<JSONObject> parsefinished(List<Entity> FinishedMatch) throws Exception
  {
	  //TODO: implement how to parse
	  List<JSONObject> parseResult = new ArrayList<JSONObject> ();
	  for (int i=0;i<FinishedMatch.size();i++)
	  {
		  Map<String,Object> currentRecord = new HashMap<String,Object> ();
		  Entity match = FinishedMatch.get(i);
		  //Map<String,Object> matchProperties = match.getProperties();
		  /*for (String key: matchProperties.keySet())
		  {
			  if (key.equals(PLAYER_IDS))
			  currentRecord.put(key, currentRecord.get(key));
		  }*/
		  String playerIdListStr = (String)match.getProperty(PLAYER_IDS);
		  JSONArray playerIdJson = new JSONArray(playerIdListStr);
		  List<JSONObject> parsedPlayerIdInfo = new ArrayList<JSONObject> ();
		  for (int j = 0;j<playerIdJson.length();j++)
		  {
			  String currentId = (String)playerIdJson.get(j);
			  Map<String,String> playerInfo = new HashMap<String,String>(DatabaseDriverPlayer.getPlayerNames(Long.parseLong(currentId)));
			  JSONObject playerScores = new JSONObject((String)(match.getProperty(GAME_OVER_SCORES)));
			  JSONObject playerTokens = new JSONObject((String)(match.getProperty(PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT)));
			  playerInfo.put(SCORE, playerScores.getString(currentId));
			  playerInfo.put(TOKENS, playerTokens.getString(currentId));
			  JSONObject playerInfoJson = new JSONObject (playerInfo);
			  parsedPlayerIdInfo.add(playerInfoJson);
		  }
		  currentRecord.put(PLAYERS, parsedPlayerIdInfo);
		  parseResult.add(new JSONObject(currentRecord));			  		  
	  }
	  return parseResult;
  }
  private Map<String,Object> parseStats(Map<String,Object> statsInfo,List<Entity> unFinishedMatch,List finishedMatch)
    throws Exception
  {
	  Map<String,Object> parsedStats = new HashMap<String,Object> ();
	  parsedStats.put(HIGH_SCORE, statsInfo.get(HIGH_SCORE));
	  parsedStats.put(RATING, statsInfo.get(RATING));
	  List<JSONObject> unfinished = parseUnfinished(unFinishedMatch);
	  parsedStats.put(CURRENT_GAMES,unfinished);
	  parsedStats.put(FINISHED_GAMES,finishedMatch);
	  /*List<JSONObject> finished = parsefinished(finishedMatch);
	  parsedStats.put(FINISHED_GAMES, finished);*/
	  return parsedStats;
		  
  }
  private List<JSONObject>  returnAllGameInfo()
  {
	  //TODO:return all the game info
	  return GameDatabaseDriver.getGameInfo(false,-1);
  }
  private List<JSONObject> returnGameInfoByDeveloper(long developerId)
  {
	  //TODO : return gameinfo by developerId
	  
	  return GameDatabaseDriver.getGameInfo(true,developerId);
  }
  
	private void getGameQuery(HttpServletRequest req, HttpServletResponse resp) {
		String developerIdStr = req.getParameter(DEVELOPER_ID);
		JSONObject jObj = new JSONObject();
		if (developerIdStr == null) {
			List<JSONObject> queryResult = returnAllGameInfo();
			// JSONObject ResultAsJSON = new JSONObject(queryResult);
			put(queryResult, resp);
			return;
		} else {
			long developerId = Long.parseLong(developerIdStr);
			String accessSignature = req.getParameter(ACCESS_SIGNATURE);
			try {
				boolean verify = DeveloperDatabaseDriver.verifyDeveloperAccess(
						developerId, accessSignature);
				if (verify == false) {
					put(jObj, ERROR, WRONG_ACCESS_SIGNATURE, resp);
					return;
				}
				List<JSONObject> queryResult = returnGameInfoByDeveloper(developerId);
				// JSONObject ResultAsJSON = new JSONObject(queryResult);
				put(queryResult, resp);
			} catch (Exception e) {
				put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);
			}

		}
	}
	private void getStatsQuery(HttpServletRequest req, HttpServletResponse resp)
	 throws Exception
	{
		String gameIdStr = req.getParameter(GAME_ID);
		JSONObject jObj = new JSONObject();
		try
		{
			Map<String,Object> statsInfo = GameDatabaseDriver.getStats(Long.parseLong(gameIdStr));
			if (statsInfo==null)
				throw new Exception();
			 List <Entity> unFinishedMatch = 
			     ContainerDatabaseDriver.getAllUnfinishedMatchesByGameID(Long.parseLong(gameIdStr));
             List finishedMatch =  (List)statsInfo.get(FINISHED_GAMES);
			Map<String,Object> parsedInfo = parseStats(statsInfo,unFinishedMatch,finishedMatch);
			jObj = new JSONObject(parsedInfo);
			put(jObj,resp);
			return;
			
		}
		catch (Exception e)
		{
			
			put(jObj, ERROR, WRONG_GAME_ID, resp);
			return;
		}
	}
  
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		String line = new String();
		StringBuffer buffer = new StringBuffer();
		Map<Object, Object> parameterMap = new HashMap<Object, Object>();
		try {
			if (req.getPathInfo().equals(RATING)==false)
				throw new IOException();
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			parameterMap = (Map) JSONUtil.parse(buffer.toString());
			String gameIdStr = req.getParameter(GAME_ID);
			if (GameDatabaseDriver.checkGameIdExists(Long.parseLong(gameIdStr)) == false) {
				put(jObj, ERROR, WRONG_GAME_ID, resp);
				return;

			}

			try {
			  DatabaseDriverPlayer.getPlayerById(Long.parseLong((String) parameterMap.get(PLAYER_ID)));
			} catch (Exception e) {
				put(jObj, ERROR, WRONG_PLAYER_ID, resp);
			}
			if (signatureRightForPlayer(parameterMap) == false) {
				put(jObj, ERROR, WRONG_ACCESS_SIGNATURE, resp);
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
				put(jObj, ERROR, WRONG_RATING, resp);
				return;
			}

		} catch (Exception e) {
			put(jObj, ERROR, URL_ERROR, resp);
		}

	}

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
			put(jObj, ERROR, URL_ERROR, resp);
		}

	}
}
	  
   



