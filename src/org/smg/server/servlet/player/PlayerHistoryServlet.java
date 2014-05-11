package org.smg.server.servlet.player;

import static org.smg.server.servlet.game.GameConstants.DATE;
import static org.smg.server.servlet.game.GameConstants.OPPONENTIDS;
import static org.smg.server.servlet.game.GameConstants.RESULT;
import static org.smg.server.servlet.game.GameConstants.SCORE;
import static org.smg.server.servlet.game.GameConstants.TOKENCHANGE;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.DatabaseDriverPlayerHistory;
import org.smg.server.database.models.PlayerHistory;
import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class PlayerHistoryServlet extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  


  /**
   *    Get player History:
   *    method: GET
   *    url:/history?playerId=...&targetId=...&gameId=...&accessSignature=...
   *    return: 
   *            ¡°history¡± : 
   *            [
   *            {¡±date¡± : ¡­, ¡°isWInner¡± : ¡­, ¡°tokenChange¡± : ¡­., ¡°scoreChange¡± : ¡­.., ¡°opponentIds¡± : []}, 
   *            {"error": "WRONG_ACCESS_SIGNATURE"}
   *            {"error": "WRONG_PLAYER_ID"}
   *            {"error": "WRONG_TARGETID"}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    JSONObject returnValue = new JSONObject();
    Map<String, String[]> map = req.getParameterMap();
    String playerId = null;
    if (!map.containsKey("playerId")){
      addErrorMessage(returnValue,"WRONG_PLAYER_ID",resp,map);
      return;
    }
    playerId = req.getParameter("playerId");
    long playerIdLong;
    try {
      playerIdLong = Long.parseLong(playerId);
    } catch (NumberFormatException e) {
      addErrorMessage(returnValue,"WRONG_PLAYER_ID",resp,map);
      return;
    }
    if (!map.containsKey("accessSignature")){
      addErrorMessage(returnValue,"WRONG_ACCESS_SIGNATURE",resp,map);
      return;
    }
    String accessSignature = req.getParameter("accessSignature");
    boolean valid = DatabaseDriverPlayer
        .validatePlayerAccessSignature(playerIdLong, accessSignature);
    if (!valid){
      addErrorMessage(returnValue,"WRONG_ACCESS_SIGNATURE",resp,map);
      return;
    } else {
      String targetId = null;
      if (!map.containsKey("targetId")){
        addErrorMessage(returnValue,"WRONG_TARGET_ID",resp,map);
        return;
      }
      targetId = req.getParameter("targetId");
      long targetIdLong;
      try {
        targetIdLong = Long.parseLong(targetId);
      } catch (NumberFormatException e) {
        addErrorMessage(returnValue,"WRONG_TARGET_ID",resp,map);
        return;
      }
      String gameId = null;
      if (!map.containsKey("gameId")){
        addErrorMessage(returnValue,"WRONG_GAME_ID",resp,map);
        return;
      }
      gameId = req.getParameter("gameId");
      long gameIdLong;
      try {
        gameIdLong = Long.parseLong(gameId);
      } catch (NumberFormatException e) {
        addErrorMessage(returnValue,"WRONG_GAME_ID",resp,map);
        return;
      }
      
      List<PlayerHistory> lph = DatabaseDriverPlayerHistory.getPlayerHistory(targetIdLong, gameIdLong);
      JSONArray his = new JSONArray();
      if (targetIdLong == playerIdLong) {
        for (PlayerHistory ph: lph) {
          JSONObject h = new JSONObject();
          try {
            //TODO TOSTRING?
            h.put(DATE, ph.getDate());
            h.put(RESULT, ph.getMatchResult().toString());
            h.put(TOKENCHANGE, String.valueOf(ph.getTokenChange()));
            h.put(SCORE, String.valueOf(ph.getScore()));
            JSONArray oppoids = new JSONArray();
            for (long id: ph.getOpponentIds()) {
              oppoids.put(String.valueOf(id));
            }
            h.put(OPPONENTIDS, oppoids);
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          his.put(h);
        }
      }
      try {
        returnValue.put("history", his);
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return;
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }

  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }

  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }
  
  private void addErrorMessage(JSONObject returnValue, 
      String errorMessage, HttpServletResponse resp, Map<String, String[]> map) throws IOException {
    try {
      returnValue.put("error", errorMessage);
      returnValue.put("parameters", map);
      returnValue.write(resp.getWriter());
    } catch (JSONException e2) {
      e2.printStackTrace();
    }
  }
  
  private void addErrorMessage(JSONObject returnValue, 
      String errorMessage, HttpServletResponse resp) throws IOException {
    try {
      returnValue.put("error", errorMessage);
      returnValue.write(resp.getWriter());
    } catch (JSONException e2) {
      e2.printStackTrace();
    }
  }
}
