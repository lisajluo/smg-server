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

import org.smg.server.database.DatabaseDriver;
import org.smg.server.database.DummyDataGen;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * Servlet used to insert a new match
 * 
 * Json Object got from the client   
 * {"accessSignature": ...,
 * "playerIds": [1234,5679], 
 * "gameId}: 12312}
 * 
 * Json Object returned to the client:
 * {"matchId": 1234567}
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
//    json = "{\"email\":\"foo@nyu.edu\"}";
    if (json != null ) {
      Map<String,Object> jsonMap = JSONUtil.parse(json);

      // TODO: put test data in DS
      //DummyDataGen.addPlayer();
      //DummyDataGen.addGame();
      
      // verify accessSignature and playerIds
      ArrayList<Long> playerIds = (ArrayList<Long>)jsonMap.get(ContainerConstants.PLAYER_IDS);
      String accessSignature = (String)jsonMap.get(ContainerConstants.ACCESS_SIGNATURE);
      boolean foundAS = false;
      for (Long playerId : playerIds){
      //  List<Entity> result = DatabaseDriver.queryByProperty(
      //      Constants.PLAYER, "ID/Name", playerId);
        Entity result = DatabaseDriver.getEntityByKey(ContainerConstants.PLAYER, playerId);
        if (result == null) {
          try {
            returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_PLAYER_ID);
            returnValue.write(resp.getWriter());
            return;
          } catch (JSONException e) {
            e.printStackTrace();
          }
        } else {
          //TODO
          if (result.getProperty(ContainerConstants.DS_ACCESS_SIGNATURE).equals(accessSignature)) {
            foundAS = true;
            break;
          }
        }
      }
      if (!foundAS){
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_ACCESS_SIGNATURE);
          returnValue.write(resp.getWriter());
          return;
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } 
      // verify gameId existed
      Long gameId = (Long)jsonMap.get(ContainerConstants.GAME_ID);
//      List<Entity> result = DatabaseDriver.queryByProperty(
//          Constants.GAME, Constants.GAME_ID, gameId);
      Entity result = DatabaseDriver.getEntityByKey(ContainerConstants.GAME, gameId);
      if (result == null) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_GAME_ID);
          returnValue.write(resp.getWriter());
          return;
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } 
      // insert new match
      // TODO constant match ID
      long matchId = 0;
      JSONObject match = new JSONObject();
      
      try {
        //match.put(Constants.MATCH_ID, matchId);
        match.put(ContainerConstants.GAME_ID, gameId);
        JSONArray jaPlayerIds = new JSONArray(playerIds);
        match.put(ContainerConstants.PLAYER_IDS, jaPlayerIds);
        match.put(ContainerConstants.PLAYER_THAT_HAS_TURN, -1);        
        match.put(ContainerConstants.GAME_OVER_SCORES, new JSONObject());
        match.put(ContainerConstants.GAME_OVER_REASON, "");
        match.put(ContainerConstants.HISTORY, new JSONArray());
        matchId = DatabaseDriver.insertMatchEntity(match);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
      
      // put result in returnValue
      try {
        returnValue.put(ContainerConstants.MATCH_ID, matchId);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }else {
      try {
        returnValue.put(ContainerConstants.ERROR, ContainerConstants.NO_DATA_RECEIVED);
      } catch (JSONException e) {
        e.printStackTrace();
      }           
    }
    
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
      e.printStackTrace();
    }
//    System.out.println("here");
  }
  
}
