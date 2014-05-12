package org.smg.server.servlet.game;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.MISSING_INFO;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.game.GameUtil.*;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.container.ContainerConstants;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet {
	

	/**
	 * doPost is called when the client sends out a POST request to submit a new
	 * game POST localhost:8888/games 
	 * JSON input from client : {
	 * “developerId”:”12312323”, //Current API key is developerId, may switch to
	 * userId in later Versions 
	 * “accessSignature”: “secretAccessSignature”,
	 * “gameName”: “Cheat”, 
	 * "description": "Game description.", 
	 * “url”:“http://www.cheatgame.com”,
	 *  “hasTokens”: false, // optional boolean(defaults to false) 
	 * "pics":  { // optional, the value of pics should be a json string
	 * “icon”: "http://www.foo.com/bar1.gif", //value of icon is a single string 
	 * “screenshots”: [ //the value of screenshot should be a json array 
	 *  “http://www.foo.com/bar2.gif”,
	 *  “http://www.foo.com/bar3.gif” ] } }
	 *  Successful JSON output: {
	 * “gameId”: “134543543523” //gameId specifies the gameId that our data
	 * store created for this game 
	 * }
	 */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		String pathInfo = req.getPathInfo();
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		String line = null;
        
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			Map<Object, Object> parameterMap = GameHelper.deleteInvalid(
					(Map) JSONUtil.parse(buffer.toString()), GameHelper.validParams);
			if (GameHelper.parsePathForPost(pathInfo) == false) {
				String details = "The URL for your post in invalid, the correct URL PATH format is :localhost:8888/games";
				GameHelper.sendErrorMessageForJson(resp, jObj, URL_ERROR, details,
						buffer.toString());
				return;
			}

			if (GameHelper.requiredFieldsComplete(parameterMap) == false) {
				String details = "Required fields for submitting a game is not complete, please refer to our API for more details";
				GameHelper.sendErrorMessageForJson(resp, jObj, MISSING_INFO, details,
								buffer.toString());
				return;
			}
			if (GameHelper.userIdExists((String) parameterMap.get(DEVELOPER_ID)) == false) {
				String details = "User verification fails, please provide a correct userId";
				GameHelper.sendErrorMessageForJson(resp, jObj, WRONG_DEVELOPER_ID, details,
						buffer.toString());

				return;
			}
			if (signatureRight(parameterMap) == false) {
				String details = "Your accessSignature is not correct";
				GameHelper.sendErrorMessageForJson(resp, jObj, WRONG_ACCESS_SIGNATURE, details,
						buffer.toString());
				return;
			}
			if (GameHelper.gameNameDuplicate(parameterMap) == true) {
				String details ="Your game name exists, please change your game name";
				GameHelper.sendErrorMessageForJson(resp, jObj, GAME_EXISTS, details,
						buffer.toString());
				return;
			} else {
				long gameId = GameDatabaseDriver.saveGameMetaInfo(parameterMap);
				jObj.put(GAME_ID, Long.toString(gameId));
				jObj.write(resp.getWriter());

			}
		} catch (Exception e) {
			String details = "Your json format is invalid for us to parse,please check";
			GameHelper.sendErrorMessageForJson(resp, jObj, INVALID_JSON, details,
					buffer.toString());
			
			return;
		}
	}

	/**
	 * doGet is called when the client sends out a GET request to get all the
	 * metaInfo of a game 
	 * GET /games/{gameId} 
	 * the successful json response
	 *  {“developerId”: “120480234”, 
	 *  “gameName”: “Cheat”, 
	 *  "description": "Game description.", 
	 *  “url”: “http://www.cheatgame.com”, 
	 *  “hasTokens”: false, // optional 
	 *  "pics": { // optional 
	 * “icon”: "http://www.foo.com/bar1.gif ", 
	 * “screenshots”: [ “http://www.foo.com/bar2.gif ”] }
	 * }
	 */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String targetIdStr = null;
    JSONObject jObj = new JSONObject();
    long targetId = 0;
    CORSUtil.addCORSHeader(resp);
    try {
      targetIdStr = req.getPathInfo().substring(1);
      targetId = Long.parseLong(targetIdStr);
      
      if (GameHelper.gameIdExist(targetId) == false) {
    	String json = GameUtil.getFullURL(req);
    	String details = "The game Id you are querying does not exist in the datastore";
    	GameHelper.sendErrorMessageForUrl(resp, jObj,
        		  WRONG_GAME_ID, details, json);        
    	return;
      } else {
        try {
        	GameHelper.returnMetaInfo(targetId, resp);
        } catch (JSONException e) {
          String json = GameUtil.getFullURL(req);
          String details = "JSON ERROR";
          GameHelper.sendErrorMessageForUrl(resp, jObj,
        		  INVALID_JSON, details, json); 
          return;
        }
      }
    } catch (Exception e) {
			String json = GameUtil.getFullURL(req);
			String details = "The url you are requesting is not correct, the correct path: localhost:8888/games/{gameId}";
			GameHelper.sendErrorMessageForUrl(resp, jObj, URL_ERROR, details, json);
			return;
    }

  }

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

	/**
	 * doDelete is called when a client wants to delete certain game in the datastore
	 * DELETE /games/{gameId}?developerId=...&accessSignature=... 
	 * Successful JSON output: { “success”: “DELETED_GAME” }
	 */
  @SuppressWarnings("unchecked")
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String targetId = null;
    JSONObject jObj = new JSONObject();
    CORSUtil.addCORSHeader(resp);
    try {
      targetId = req.getPathInfo().substring(1);
      long gameId = Long.parseLong(targetId);
      String developerId = req.getParameter(DEVELOPER_ID);
      if (GameHelper.userIdExists(developerId) == false) {
    	String json = GameUtil.getFullURL(req);
        String details = "The developer Id you provide does not exist";
        GameHelper.sendErrorMessageForUrl(resp, jObj,
        		  WRONG_DEVELOPER_ID, details, json);   
        return;
      }
      if (signatureRight(req) == false) {
        String json = GameUtil.getFullURL(req);
        String details = "Your access signature is not correct";
        GameHelper.sendErrorMessageForUrl(resp, jObj,
        		WRONG_ACCESS_SIGNATURE, details, json);  
        return;
      }
      if (GameHelper.gameIdExist(gameId) == false) {
    	String json = GameUtil.getFullURL(req);
    	String details = "Your game Id does not exist";
    	GameHelper.sendErrorMessageForUrl(resp, jObj,
    			  WRONG_GAME_ID, details, json);
        return;
      }
      Entity targetEntity = GameDatabaseDriver.getGame(gameId);
      List<String> developerList = (List<String>) targetEntity
          .getProperty(DEVELOPER_ID);
      if (developerList.contains(developerId) == false) {
    	String json = GameUtil.getFullURL(req);
      	String details = "The game you are trying to delete does not belong to you";
      	GameHelper.sendErrorMessageForUrl(resp, jObj,
      			WRONG_DEVELOPER_ID, details, json);
        return;
      }

      GameDatabaseDriver.deleteGame(targetId);
      put(jObj, SUCCESS, DELETED_GAME, resp);
      return;
    } catch (Exception e) {
    	String json = GameUtil.getFullURL(req);
      	String details = "The url you are trying to make a delete request in incorrect, the correct format:DELETE localhost:8888/games/{gameId}?developerId=123&accessSignature=123";
      	GameHelper.sendErrorMessageForUrl(resp, jObj,
      			URL_ERROR, details, json);
      return;
    }

  }
  /**
	 * doPut is called when the client sends out a PUT request to update a game
	 * PUT localhost:8888/games 
	 * JSON input from client : //The optional field won't get updated 
	 * if the client doesn't specify that field 
	 * {
	 * “developerId”:”12312323”, //Current API key is developerId, may switch to
	 * userId in later Versions 
	 * “accessSignature”: “secretAccessSignature”,
	 * “gameName”: “Cheat”, //optional
	 * "description": "Game description.", //optional 
	 * “url”:“http://www.cheatgame.com”, //optional
	 *  “hasTokens”: false, // optional boolean(defaults to false) //optional
	 * "pics":  { // optional, the value of pics should be a json string
	 * “icon”: "http://www.foo.com/bar1.gif", //value of icon is a single string 
	 * “screenshots”: [ //the value of screenshot should be a json array 
	 *  “http://www.foo.com/bar2.gif”,
	 *  “http://www.foo.com/bar3.gif” ] } }
	 *  Successful JSON output: {
	 * “gameId”: “134543543523” //gameId specifies the gameId that our data
	 * store created for this game 
	 * }
	 * Successful JSON output:
	 * { “success”: “UPDATED_GAME” }
	 */

	 
     
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    JSONObject jObj = new JSONObject();
    CORSUtil.addCORSHeader(resp);

    String gameId = null;
    StringBuffer buffer = new StringBuffer();
    String line = null;
    try {
      gameId = req.getPathInfo().substring(1);
      long longId = Long.parseLong(gameId);
      try {
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
          buffer.append(line);
        }
        Map<Object, Object> parameterMap = GameHelper.deleteInvalid(
            (Map) JSONUtil.parse(buffer.toString()), GameHelper.validParams);
        if (GameHelper.requiredFieldForUpdate(parameterMap) == false) {
          String details = "Please provide your userId/developerId";
		  GameHelper.sendErrorMessageForJson(resp, jObj, MISSING_INFO, details,
					buffer.toString());
          return;
        }
        if (GameHelper.userIdExists((String) parameterMap.get(DEVELOPER_ID)) == false) {
          String details = "Your developerId/userId does not exist";
          GameHelper.sendErrorMessageForJson(resp, jObj, WRONG_DEVELOPER_ID, details,
					buffer.toString());
          return;
        }
        if (signatureRight(parameterMap) == false) {
          String details = "Your access signature is incorrect";
          GameHelper.sendErrorMessageForJson(resp, jObj,WRONG_ACCESS_SIGNATURE, details,
					buffer.toString()); 
          return;
        }
        if (GameHelper.gameIdExist(Long.parseLong(gameId)) == false) {
          String details = "The game you are trying to update does not exist";
          GameHelper.sendErrorMessageForJson(resp, jObj,WRONG_GAME_ID, details,
					buffer.toString()); 
          return;
        }

        if (GameHelper.gameNameDuplicate(longId, parameterMap) == true) {
          String details ="There is another game with the same name, please update to another name";
          GameHelper.sendErrorMessageForJson(resp, jObj,GAME_EXISTS, details,
					buffer.toString()); 
          return;
        }
        Entity targetEntity = GameDatabaseDriver.getGame(longId);
        List<String> developerList = (List<String>) targetEntity
            .getProperty(DEVELOPER_ID);
        if (developerList.contains(parameterMap.get(DEVELOPER_ID)) == false) {
          String details = "The game you are trying to update does not belong to you";
          GameHelper.sendErrorMessageForJson(resp, jObj,WRONG_DEVELOPER_ID, details,
					buffer.toString()); 
          return;
        }
        parameterMap.put(UPDATED, true);
        GameDatabaseDriver.updateGame(longId, parameterMap);
        put(jObj, SUCCESS, UPDATED_GAME, resp);
        return;
      } catch (Exception e) {
    	  String details = "Your JSON input is invalid, please check again or refer to our API for more details";
    	  GameHelper.sendErrorMessageForJson(resp, jObj,INVALID_JSON, details,
					buffer.toString()); 

        return;
      }
    } catch (Exception e) {
      String json =  GameUtil.getFullURL(req);
      String details = "The url you are trying to make an update request in incorrect, the correct format:PUT localhost:8888/games/{gameId}";
      GameHelper.sendErrorMessageForUrl(resp, jObj,
    			URL_ERROR, details, json);
      return;
    }
  }
}