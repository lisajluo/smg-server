package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.IDUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * Servlet used to insert a new match, and get new match info
 * 
 * @author piper
 * 
 */
@SuppressWarnings("serial")
public class NewMatchServlet extends HttpServlet {

  /**
   * Add CORS header
   */
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   * URL: /newMatch/
   * 
   * JSON from client: {"accessSignature": ..., "playerIds":
   * ["156456234","5345345679"], "gameId": "12313453452"}
   * 
   * JSON to client: {"matchId": "23232323232", "playerIds":
   * ["156456234","5345345679"]}
   * 
   */
  @SuppressWarnings("unchecked")
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    // adding CORS Header
    CORSUtil.addCORSHeader(resp);

    BufferedReader br = new BufferedReader(new InputStreamReader(
        req.getInputStream()));
    String json = "";
    if (br != null) {
      json = br.readLine();
    }
    JSONObject returnValue = new JSONObject();
    if (json != null) {
      // parse json message to jsonMap
      Map<String, Object> jsonMap = null;
      try {
        jsonMap = JSONUtil.parse(json);
      } catch (IOException e) {
        //TODO POST example
        String details = "Json string from client is not in the correct format.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.JSON_PARSE_ERROR, details, json);
        return;
      }
      // check if missing info
      if (!jsonMap.containsKey(ContainerConstants.PLAYER_IDS)
          || !jsonMap.containsKey(ContainerConstants.ACCESS_SIGNATURE)
          || !jsonMap.containsKey(ContainerConstants.GAME_ID)) {
        String details = "Json string from client does not contains all the required field."
            + "Requires: playerIds, accessSignature, gameId.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.MISSING_INFO, details, json);
        return;
      }
      // parse gameID and verify gameId existed
      String gId = String.valueOf(jsonMap.get(ContainerConstants.GAME_ID));
      long gameId = 0;
      try {
        gameId = IDUtil.stringToLong(gId);
      } catch (Exception e) {
        String details = "GameId is not in the correct format. Cannot be negative or zero.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_GAME_ID, details, json);
        return;
      }
      if (!ContainerVerification.gameIdVerify(gameId)) {
        String details = "GameId does not exist in our datastore.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_GAME_ID, details, json);
        return;
      }
      // verify playerIds
      ArrayList<String> ids = (ArrayList<String>) jsonMap
          .get(ContainerConstants.PLAYER_IDS);
      List<Long> playerIds = new ArrayList<Long>();
      try {
        playerIds = IDUtil.stringListToLongList(ids);
      } catch (Exception e) {
        String details = "PlayerIds is not in the correct format. Cannot be negative or zero";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_PLAYER_ID, details, json);
        return;
      }
      if (!ContainerVerification.playerIdsVerify(playerIds)) {
        String details = "PlayerIds do not exist in our datastore.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_PLAYER_ID, details, json);
        return;
      }
      if (!ContainerVerification.insertMatchVerify(playerIds, gameId)) {
        String details = "Player already has an unfinished match for this game.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_PLAYER_ID, details, json);
        return;
      }
      // verify accessSignature
      String accessSignature = String.valueOf(jsonMap
          .get(ContainerConstants.ACCESS_SIGNATURE));
      if (!ContainerVerification.accessSignatureVerify(accessSignature,
          playerIds)) {
        String details = "Access signature is not associated with any of playerId client provided.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_ACCESS_SIGNATURE, details, json);
        return;
      }
      // insert new match
      long matchId = 0;
      JSONObject match = new JSONObject();

      try {
        match.put(ContainerConstants.GAME_ID, gameId);
        JSONArray jaPlayerIds = new JSONArray(playerIds);
        match.put(ContainerConstants.PLAYER_IDS, jaPlayerIds);
        match.put(ContainerConstants.PLAYER_THAT_HAS_TURN, -1);
        match.put(ContainerConstants.PLAYER_THAT_HAS_LAST_TURN, -1);
        match.put(ContainerConstants.GAME_OVER_SCORES, new JSONObject());
        match.put(ContainerConstants.GAME_OVER_REASON,
            ContainerConstants.NOT_OVER);
        match.put(ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT,
            new JSONObject());
        match.put(ContainerConstants.HISTORY, new JSONArray());
        matchId = ContainerDatabaseDriver.insertMatchEntity(match);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }

      // put result in returnValue
      try {
        returnValue.put(ContainerConstants.MATCH_ID,
            IDUtil.longToString(matchId));
        returnValue.put(ContainerConstants.PLAYER_IDS, ids);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      // push matchId to opponents
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      for (long playerId : playerIds) {
        if (!ContainerVerification.accessSignatureVerify(accessSignature,
            playerId)) {
          String token = Utils.encodeToChannelId(String.valueOf(playerId),
              String.valueOf(gameId));
          channelService.sendMessage(new ChannelMessage(token, returnValue
              .toString()));
        }
      }
    } else {
      String details = "No json received";
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.NO_DATA_RECEIVED, details, json);
      return;
    }

    try {
      // return value to client
      returnValue.write(resp.getWriter());

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * URL: /newMatch/{playerId}?accessSignature=...
   * 
   * /newMatch/1232?accessSignature=JKD89DC
   * 
   * JSON to client: {"matchId": "2323232" "playerIds":["1232","1232"]}
   * 
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    // verify playerId
    if (req.getPathInfo().length() < 2) {
      String details = "PlayerId in url is not correct";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_PLAYER_ID, details, json);
      return;
    }
    String pId = req.getPathInfo().substring(1);
    long playerId = 0;
    try {
      playerId = IDUtil.stringToLong(pId);
    } catch (Exception e) {
      //TODO GET Example
      String details = "PlayerId in url is not int the correct format. "
          + "Cannot be negative or zero";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_PLAYER_ID, details, json);
      return;
    }
    if (!ContainerVerification.playerIdVerify(playerId)) {
      String details = "PlayerId does not exist in our datastore.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_PLAYER_ID, details, json);
      return;
    }
    // verify accessSignature
    if (!req.getParameterMap().containsKey(ContainerConstants.ACCESS_SIGNATURE)) {
      String details = "No access signature received.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_ACCESS_SIGNATURE, details, json);
      return;
    }
    String accessSignature = req
        .getParameter(ContainerConstants.ACCESS_SIGNATURE);
    if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
      String details = "Access signature is not associated with playerId client provided.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_ACCESS_SIGNATURE, details, json);
      return;
    }
    // verify gameId
    if (!req.getParameterMap().containsKey(ContainerConstants.GAME_ID)) {
      String details = "No gameId received.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_GAME_ID, details, json);
      return;
    }
    String gId = req.getParameter(ContainerConstants.GAME_ID);
    long gameId = 0;
    try {
      gameId = IDUtil.stringToLong(gId);
    } catch (Exception e2) {
      String details = "GameId is not in the correct format. Cannot be negative or zero.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_GAME_ID, details, json);
      return;
    }
    if (!ContainerVerification.gameIdVerify(gameId)) {
      String details = "GameId does not exist in our datastore.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_GAME_ID, details, json);
      return;
    }

    Entity match = ContainerDatabaseDriver.getUnfinishedMatchByPlayerIdGameId(
        playerId, gameId);
    if (match == null) {
      String details = "No match found by playerId and gameId";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.NO_MATCH_FOUND, details, json);
      return;
    }
    // add playerIds and matchId
    List<Long> playerIds = JSONUtil.parseDSPlayerIds((String) match
        .getProperty(ContainerConstants.PLAYER_IDS));
    List<String> pIds = IDUtil.longListToStringList(playerIds);
    try {
      returnValue.put(ContainerConstants.MATCH_ID,
          IDUtil.longToString((Long) match.getKey().getId()));
      returnValue.put(ContainerConstants.PLAYER_IDS, pIds);

    } catch (JSONException e1) {

    }

    try {
      // return value to client
      returnValue.write(resp.getWriter());

    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

}
