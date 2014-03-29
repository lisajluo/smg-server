package org.smg.server.servlet.game;


import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.game.GameUtil.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;
import org.smg.server.database.DatabaseDriver;
import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.GameDatabaseDriver;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class GameInfoServlet extends HttpServlet {
  private List<Map<String,Object>> parseUnfinished(List<Entity> unFinishedMatch)
  {
	  //TODO: implement how to parse
	  List<Map<String,Object>> parseResult = new ArrayList<Map<String,Object>> ();
	  for (int i=0;i<unFinishedMatch.size();i++)
	  {
		  Map<String,Object> currentRecord = new HashMap<String,Object> ();
		  Entity match = unFinishedMatch.get(i);
		  Map<String,Object> matchProperties = match.getProperties();
		  for (String key: matchProperties.keySet())
		  {
			  currentRecord.put(key, currentRecord.get(key));
		  }
		  parseResult.add(currentRecord);			  		  
	  }
	  return parseResult;
  }
  private Map<String,Object> parseStats(Map<String,Object> statsInfo,List<Entity> unFinishedMatch)
  {
	  Map<String,Object> parsedStats = new HashMap<String,Object> ();
	  for (String key: statsInfo.keySet())
		  parsedStats.put(key, statsInfo.get(key));
	  List<Map<String,Object>> unfinished = parseUnfinished(unFinishedMatch);
	  parsedStats.put(CURRENT_GAMES,unfinished);
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
			 List <Entity> unFinishedMatch = getAllUnfinishedMatchesByGameID(Long.parseLong(gameIdStr)) ;

			Map<String,Object> parsedInfo = parseStats(statsInfo,unFinishedMatch);
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
			if (DatabaseDriver.checkIdExists(gameIdStr) == false) {
				put(jObj, ERROR, WRONG_GAME_ID, resp);
				return;

			}

			try {
				DatabaseDriver.getPlayerById(Long
						.parseLong((String) parameterMap.get(PLAYER_ID)));
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
	  
   



