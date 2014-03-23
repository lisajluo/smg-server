
package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriver;
import org.smg.server.servlet.container.GameApi.GameState;
import org.smg.server.servlet.container.GameApi.Operation;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("serial")
public class MatchOperationServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    long matchId = Integer.parseInt(req.getPathInfo().substring(1));
    if (!ContainerVerification.matchIdVerify(matchId)) {
      try {
      returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_MATCH_ID);
      returnValue.write(resp.getWriter());
      } catch (JSONException e) { }
      return;
    }
    long playerId = Long.parseLong(req.getParameter(ContainerConstants.PLAYER_ID));
    if (!ContainerVerification.matchIdVerify(playerId)) {
      try {
      returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_PLAYER_ID);
      returnValue.write(resp.getWriter());
      } catch (JSONException e) { }
      return;
    }
    String accessSignature = req.getParameter(ContainerConstants.ACCESS_SIGNATURE);
    if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
      try {
        returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_ACCESS_SIGNATURE);
        returnValue.write(resp.getWriter());
        } catch (JSONException e) { }
        return;
    }
    //TODO: Write stuff here
    
    // write to resp then return
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) { }
    
    
    /*
    PrintWriter res = resp.getWriter();
    MatchInfoManager mim = MatchInfoManager.getInstance();
    GameStateManager gsm = GameStateManager.getInstance();

    CORSUtil.addCORSHeader(resp);
    int matchId = Integer.parseInt(req.getPathInfo().substring(1));

    resp.setHeader("Content-Type", "application/json");
    String accessSignature = req.getParameter(ContainerConstants.ACCESS_SIGNATURE);

    MatchInfo mi = mim.getMatchInfo(matchId);
    Map<String, Object> returnJsonMap = mi.toMap();
    returnJsonMap.put(ContainerConstants.HISTORY, 
        gsm.getHistoryState(matchId, accessSignature));

    String rtnJson = new ObjectMapper().writeValueAsString(returnJsonMap);
    res.print(rtnJson);
    res.close();
    */
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    // get json string the parse to map
    BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
    String json = "";
    if (br != null) {
      json = br.readLine();
    }
    JSONObject returnValue = new JSONObject();
    if (json != null ) {
      Map<String,Object> jsonMap = null;
      try {
        jsonMap = JSONUtil.parse(json);
      } catch (IOException e) {
        try {
          returnValue.put(ContainerConstants.ERROR, e.getMessage());
          returnValue.write(resp.getWriter());
        } catch (JSONException e2) { }
        return;
      }
      long matchId = Long.parseLong(req.getPathInfo().substring(1));      
      if (!ContainerVerification.matchIdVerify(matchId)) {
        try {
        returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_MATCH_ID);
        returnValue.write(resp.getWriter());
        } catch (JSONException e) { }
        return;
      }
      ArrayList<Long> playerIds = (ArrayList<Long>)jsonMap.get(ContainerConstants.PLAYER_IDS);
      if (!ContainerVerification.playerIdsVerify(playerIds)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_PLAYER_ID);
          returnValue.write(resp.getWriter());
          } catch (JSONException e) { }
          return;
      }
      String accessSignature = (String) jsonMap.get(ContainerConstants.ACCESS_SIGNATURE);
      if (!ContainerVerification.accessSignatureVerify(accessSignature, playerIds)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_ACCESS_SIGNATURE);
          returnValue.write(resp.getWriter());
          } catch (JSONException e) { }
          return;
      }
      Entity entity = DatabaseDriver.getEntityByKey(ContainerConstants.MATCH, matchId);
      //TODO: add all the stuff here
      
      
    } else {
      try {
        returnValue.put(ContainerConstants.ERROR, ContainerConstants.NO_DATA_RECEIVED);
      } catch (JSONException e) { }        
    }
    
    // write to resp then return
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) { }
    
    /*
    try {
      long matchId = Long.parseLong(req.getPathInfo().substring(1));
      String jsonString = Utils.getBody(req);
      Map<String, Object> jsonMap = JSONUtil.parse(jsonString);

      Entity entity = DatabaseDriver.getEntityByKey(ContainerConstants.MATCH, matchId);
      String accessSignature = (String) jsonMap.get(ContainerConstants.ACCESS_SIGNATURE);
      MatchInfo mi = MatchInfo.getMatchInfoFromEntity(entity);
      
      GameStateManager gsm = GameStateManager.getInstance();
      GameState gameState = gsm.getGameStateByAS(accessSignature);

      // Convert json string to Operation list.
      List<Map<String, String>> operationsList;

      operationsList = (List<Map<String, String>>) jsonMap.get(ContainerConstants.OPERATIONS);
      List<Operation> operations = GameStateManager
          .messageToOperationList(operationsList);

      // Generate return data.
      gameState.makeMove(operations);
      int playerId = gsm.getPlayerIdByAccessSignature(accessSignature);
      Map<String, Object> returnState = gameState.getStateForPlayerId(playerId);

      Map<String, Object> returnJsonMap = ImmutableMap.<String, Object> of(
          ContainerConstants.MATCH_ID, matchId, ContainerConstants.GAME_STATE, returnState);

     // String rtnJson = mapper.writeValueAsString(returnJsonMap);
     // res.print(rtnJson);
      res.close();
    } catch (Exception e) {
      e.printStackTrace();
      // Parse failed. Json not valid.
      res.print(ContainerConstants.ERROR);
      res.close();
      return;
    }
    */
  }
}
