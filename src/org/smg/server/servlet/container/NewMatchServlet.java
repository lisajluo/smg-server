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
    if (json == null || json.length() != 0) {
      Map<String,Object> jsonMap = JSONUtil.parse(json);
      
      // TODO: put test data in DS
//      DummyDataGen.addPlayer();
//      DummyDataGen.addGame();
      
      // verify accessSignature and playerIds
      ArrayList<Integer> playerIds = (ArrayList<Integer>)jsonMap.get("playerIds");
      String accessSignature = (String)jsonMap.get("accessSignature");
      boolean foundAS = false;
      for (Integer playerId : playerIds){
        List<Entity> result = DatabaseDriver.queryByProperty("Player", "playerId", playerId);
        if (result.isEmpty()) {
          try {
            returnValue.put("error", "WRONG_PLAYER_ID");
            resp.getWriter().write(returnValue.toString());
            return;
          } catch (JSONException e) {
            e.printStackTrace();
          }
        } else {
          if (result.get(0).getProperty("accessSignature").equals(accessSignature)) {
            foundAS = true;
            break;
          }
        }
      }
      if (!foundAS){
        try {
          returnValue.put("error", "WRONG_ACCESS_SIGNATURE");
          resp.getWriter().write(returnValue.toString());
          return;
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } 
      // verify gameId existed
      Integer gameId = (Integer)jsonMap.get("gameId");
      List<Entity> result = DatabaseDriver.queryByProperty("Game", "gameId", gameId);
      if (result.isEmpty()) {
        try {
          returnValue.put("error", "WRONG_GAME_ID");
          resp.getWriter().write(returnValue.toString());
          return;
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } 
      // insert new match
      // TODO constant match ID
      Integer matchId = 23232;
      JSONObject match = new JSONObject();
      
      try {
        match.put("matchId", matchId);
        match.put("gameId", gameId);
        JSONArray jaPlayerIds = new JSONArray(playerIds);
        match.put("playerIds", jaPlayerIds);
        match.put("playerThatHasTurn", -1);        
        match.put("gameOverScores", new JSONObject());
        match.put("gameOverReason", "");
        match.put("history", new JSONArray());
        DatabaseDriver.insertMatchEntity(match);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
      
      // put result in returnValue
      try {
        returnValue.put("matchId", matchId);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }else {
      try {
        returnValue.put("error", "NO_DATA_GOT");
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
