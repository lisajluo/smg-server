package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

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
    if (json != null ) {
      //parse json message to jsonMap
      Map<String, Object> jsonMap = null;
      try {
        jsonMap = JSONUtil.parse(json);
      } catch (IOException e) {
        try {
          returnValue.put(ContainerConstants.ERROR, e.getMessage());
          returnValue.write(resp.getWriter());
        } catch (JSONException e2) {
        }
        return;
      }

      // verify playerIds      
      ArrayList<Long> playerIds = (ArrayList<Long>) jsonMap.get(ContainerConstants.PLAYER_IDS);
      if (!ContainerVerification.playerIdsVerify(playerIds)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_PLAYER_ID);
          returnValue.write(resp.getWriter());
        } catch (JSONException e) {
        }
        return;
      }
      // verify accessSignature
      String accessSignature = String.valueOf(jsonMap.get(ContainerConstants.ACCESS_SIGNATURE));
      if (!ContainerVerification.accessSignatureVerify(accessSignature, playerIds)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_ACCESS_SIGNATURE);
          returnValue.write(resp.getWriter());
        } catch (JSONException e) {
        }
        return;
      }
      // parse gameID and verify gameId existed
      long gameId = 0;
      try {
        gameId = Long.parseLong(String.valueOf(jsonMap.get(ContainerConstants.GAME_ID)));
      } catch (Exception e) {        
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_GAME_ID);
          returnValue.write(resp.getWriter());
        } catch (JSONException e1) { }
        return;
      }
      if (!ContainerVerification.gameIdVerify(gameId)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_GAME_ID);
          returnValue.write(resp.getWriter());
          return;
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }      
      // insert new match
      long matchId = 0;
      JSONObject match = new JSONObject();
      
      try {
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
  }
  
}
