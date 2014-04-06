package org.smg.server.servlet.player;

import static org.smg.server.servlet.game.GameConstants.DRAW;
import static org.smg.server.servlet.game.GameConstants.LOST;
import static org.smg.server.servlet.game.GameConstants.SCORE;
import static org.smg.server.servlet.game.GameConstants.TOKEN;
import static org.smg.server.servlet.game.GameConstants.WIN;
import static org.smg.server.servlet.game.GameConstants.RANK;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.DatabaseDriverPlayerStatistic;
import org.smg.server.database.models.PlayerStatistic;
import org.smg.server.database.models.PlayerStatistic.StatisticProperty;
import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class PlayerGameServlet extends HttpServlet{
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  


  /**
   *    Get player gaming info:
   *    method: GET
   *    url:/history?playerId=...&targetId=...&gameId=...&accessSignature=...
   *    return: 
   *            { token:..., score:...} 
   *            {"error": "WRONG_ACCESS_SIGNATURE"}
   *            {"error": "WRONG_PLAYER_ID"}
   *            {"error": "WRONG_TARGET_ID"}
   *            {"error": "WRONG_GAME_ID"}
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
      addErrorMessage(returnValue,"WRONG_PLAYER_ID",resp);
      return;
    }
    playerId = req.getParameter("playerId");
    long playerIdLong;
    try {
      playerIdLong = Long.parseLong(playerId);
    } catch (NumberFormatException e) {
      addErrorMessage(returnValue,"WRONG_PLAYER_ID",resp);
      return;
    }
    if (!map.containsKey("accessSignature")){
      addErrorMessage(returnValue,"WRONG_ACCESS_SIGNATURE",resp);
      return;
    }
    String accessSignature = req.getParameter("accessSignature");
    boolean valid = DatabaseDriverPlayer
        .validatePlayerAccessSignature(playerIdLong, accessSignature);
    if (!valid){
      addErrorMessage(returnValue,"WRONG_ACCESS_SIGNATURE",resp);
      return;
    } else {
      String targetId = null;
      if (!map.containsKey("targetId")){
        addErrorMessage(returnValue,"WRONG_TARGET_ID",resp);
        return;
      }
      targetId = req.getParameter("targetId");
      long targetIdLong;
      try {
        targetIdLong = Long.parseLong(targetId);
      } catch (NumberFormatException e) {
        addErrorMessage(returnValue,"WRONG_TARGET_ID",resp);
        return;
      }
      String gameId = null;
      if (!map.containsKey("gameId")){
        addErrorMessage(returnValue,"WRONG_GAME_ID",resp);
        return;
      }
      gameId = req.getParameter("gameId");
      long gameIdLong;
      try {
        gameIdLong = Long.parseLong(gameId);
      } catch (NumberFormatException e) {
        addErrorMessage(returnValue,"WRONG_GAME_ID",resp);
        return;
      }
      PlayerStatistic ps = DatabaseDriverPlayerStatistic.getPlayerStatistic(targetIdLong, gameIdLong);
      try {
        returnValue.put(TOKEN, ps.getProperty(StatisticProperty.TOKEN));
        returnValue.put(SCORE, ps.getProperty(StatisticProperty.HIGHSCORE));
        returnValue.put(WIN, ps.getProperty(StatisticProperty.WIN));
        returnValue.put(LOST, ps.getProperty(StatisticProperty.LOST));
        returnValue.put(DRAW, ps.getProperty(StatisticProperty.DRAW));
        returnValue.put(RANK, ps.getProperty(StatisticProperty.RANK));
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
      String errorMessage, HttpServletResponse resp) throws IOException {
    try {
      returnValue.put("error", errorMessage);
      returnValue.write(resp.getWriter());
    } catch (JSONException e2) {
      e2.printStackTrace();
    }
  }

}
