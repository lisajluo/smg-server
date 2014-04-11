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
 * Servlet used to insert a new match
 * 
 * Json Object got from the client   
 * {"accessSignature": ...,
 * "playerIds": ["1234","5679"], 
 * "gameId}: "12312"}
 * 
 * Json Object returned to the client:
 * {"matchId": "1234567"}
 *
 */
@SuppressWarnings("serial")
public class NewMatchServlet extends HttpServlet {
  
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  @SuppressWarnings("unchecked")
  @Override 
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {  
    
    // adding CORS Header
    CORSUtil.addCORSHeader(resp);
    
    BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
    String json = "";
    if (br != null) {
      json = br.readLine();
    }
    JSONObject returnValue = new JSONObject();
    if (json != null ) {
      //parse json message to jsonMap
      Map<String, Object> jsonMap = null;
      try {
        jsonMap = JSONUtil.parse(json);
      } catch (IOException e) {
        ContainerVerification.sendErrorMessage(
           resp, returnValue, ContainerConstants.JSON_PARSE_ERROR);
        return;
      }
      // check if missing info
      if ( !jsonMap.containsKey(ContainerConstants.PLAYER_IDS) 
          || !jsonMap.containsKey(ContainerConstants.ACCESS_SIGNATURE)
          || !jsonMap.containsKey(ContainerConstants.GAME_ID)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.MISSING_INFO);
        return;
      }
      // verify playerIds      
      ArrayList<String> ids = 
          (ArrayList<String>) jsonMap.get(ContainerConstants.PLAYER_IDS);
      List<Long> playerIds = new ArrayList<Long>();
      try {
        playerIds = IDUtil.stringListToLongList(ids);
      } catch (Exception e) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
        return;
      }
      if (!ContainerVerification.playerIdsVerify(playerIds)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
        return;
      }
      if (!ContainerVerification.insertMatchVerify(playerIds)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
        return;
      }
      // verify accessSignature
      String accessSignature = String.valueOf(jsonMap.get(ContainerConstants.ACCESS_SIGNATURE));
      if (!ContainerVerification.accessSignatureVerify(accessSignature, playerIds)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_ACCESS_SIGNATURE);
        return;
      }
      // parse gameID and verify gameId existed
      String gId = String.valueOf(jsonMap.get(ContainerConstants.GAME_ID));
      long gameId = 0;
      try {
        gameId = IDUtil.stringToLong(gId);
      } catch (Exception e) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_GAME_ID);
        return;
      }
      if (!ContainerVerification.gameIdVerify(gameId)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_GAME_ID);
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
        match.put(ContainerConstants.GAME_OVER_REASON, ContainerConstants.NOT_OVER);
        match.put(ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT, new JSONObject());
        match.put(ContainerConstants.HISTORY, new JSONArray());
        matchId = ContainerDatabaseDriver.insertMatchEntity(match);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
      
      // put result in returnValue
      try {
        returnValue.put(ContainerConstants.MATCH_ID, IDUtil.longToString(matchId));
        returnValue.put(ContainerConstants.PLAYER_IDS, ids);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      
      // push matchId to opponents
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      for (long playerId: playerIds) {
        if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
          String token = String.valueOf(playerId);
          channelService.sendMessage(new ChannelMessage(token, returnValue.toString()));
        }
      }
    }else {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.NO_DATA_RECEIVED);
      return;           
    }
    
    try {
      // return value to client
      returnValue.write(resp.getWriter());
      
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    // verify playerId
    if (req.getPathInfo().length() < 2) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
      return;
    }
    String pId = req.getPathInfo().substring(1);
    long playerId = 0;
    try {
      playerId = IDUtil.stringToLong(pId);
    } catch (Exception e) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
      return;
    }
    if (!ContainerVerification.playerIdVerify(playerId)) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
      return;
    }
    // verify accessSignature
    if (!req.getParameterMap().containsKey(ContainerConstants.ACCESS_SIGNATURE)) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_ACCESS_SIGNATURE);
      return;
    }
    String accessSignature = req.getParameter(ContainerConstants.ACCESS_SIGNATURE);
    if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_ACCESS_SIGNATURE);
      return;
    }
    
    Entity match = ContainerDatabaseDriver.getUnfinishedMatchByPlayerId(playerId);
    if (match == null) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.NO_MATCH_FOUND);
      return;
    }    
    //add playerIds and matchId
    List<Long> playerIds = JSONUtil.parseDSPlayerIds(
        (String)match.getProperty(ContainerConstants.PLAYER_IDS));
    List<String> pIds = IDUtil.longListToStringList(playerIds);
    try {
      returnValue.put(ContainerConstants.MATCH_ID, 
          IDUtil.longToString((Long)match.getKey().getId()));
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
