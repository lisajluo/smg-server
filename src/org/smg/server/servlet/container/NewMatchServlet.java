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
import org.smg.util.CORSUtil;
import org.smg.util.JSONUtil;

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
      ArrayList<Long> playerIds = (ArrayList<Long>)jsonMap.get(Constants.PLAYER_IDS);
      String accessSignature = (String)jsonMap.get(Constants.ACCESS_SIGNATURE);
      boolean foundAS = false;
      for (Long playerId : playerIds){
      //  List<Entity> result = DatabaseDriver.queryByProperty(
      //      Constants.PLAYER, "ID/Name", playerId);
        Entity result = DatabaseDriver.queryById(Constants.PLAYER, playerId);
        if (result == null) {
          try {
            returnValue.put(Constants.ERROR, Constants.WRONG_PLAYER_ID);
            returnValue.write(resp.getWriter());
            return;
          } catch (JSONException e) {
            e.printStackTrace();
          }
        } else {
          if (result.getProperty(Constants.ACCESS_SIGNATURE).equals(accessSignature)) {
            foundAS = true;
            break;
          }
        }
      }
      if (!foundAS){
        try {
          returnValue.put(Constants.ERROR, Constants.WRONG_ACCESS_SIGNATURE);
          returnValue.write(resp.getWriter());
          return;
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } 
      // verify gameId existed
      Long gameId = (Long)jsonMap.get(Constants.GAME_ID);
//      List<Entity> result = DatabaseDriver.queryByProperty(
//          Constants.GAME, Constants.GAME_ID, gameId);
      Entity result = DatabaseDriver.queryById(Constants.GAME, gameId);
      if (result == null) {
        try {
          returnValue.put(Constants.ERROR, Constants.WRONG_GAME_ID);
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
        match.put(Constants.GAME_ID, gameId);
        JSONArray jaPlayerIds = new JSONArray(playerIds);
        match.put(Constants.PLAYER_IDS, jaPlayerIds);
        match.put(Constants.PLAYER_THAT_HAS_TURN, -1);        
        match.put(Constants.GAME_OVER_SCORES, new JSONObject());
        match.put(Constants.GAME_OVER_REASON, "");
        match.put(Constants.HISTORY, new JSONArray());
        matchId = DatabaseDriver.insertMatchEntity(match);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
      
      // put result in returnValue
      try {
        returnValue.put(Constants.MATCH_ID, matchId);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }else {
      try {
        returnValue.put(Constants.ERROR, Constants.NO_DATA_RECEIVED);
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
