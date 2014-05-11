package org.smg.server.servlet.player;

import static org.smg.server.servlet.game.GameConstants.DRAW;
import static org.smg.server.servlet.game.GameConstants.LOST;
import static org.smg.server.servlet.game.GameConstants.RANK;
import static org.smg.server.servlet.game.GameConstants.SCORE;
import static org.smg.server.servlet.game.GameConstants.TOKEN;
import static org.smg.server.servlet.game.GameConstants.WIN;

import java.io.IOException;
import java.util.List;
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
public class PlayerAllGameServlet extends HttpServlet{
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   *    Get player all gaming info:
   *    method: GET
   *    url:/playerAllGame?playerId=...&targetId=...&accessSignature=...
   *    return: 
   *     Success:{"123":{"draw":"1","RANK":"1500","lost":"0","token":"1","score":"47","win":"0"},
   *              "12323":{"draw":"0","RANK":"1516","lost":"0","token":"3","score":"43","win":"1"}}
   *     Errors: {"error": "WRONG_ACCESS_SIGNATURE", "parameters": ...}
   *             {"error": "WRONG_PLAYER_ID", "parameters": ...}
   *             {"error": "WRONG_TARGET_ID", "parameters": ...}
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
      List<PlayerStatistic> lps = DatabaseDriverPlayerStatistic.getPlayerStatistics(targetIdLong);
      for (PlayerStatistic ps: lps) {
        JSONObject s = new JSONObject();
        try {
          s.put(TOKEN, ps.getProperty(StatisticProperty.TOKEN));
          s.put(SCORE, ps.getProperty(StatisticProperty.HIGHSCORE));
          s.put(WIN, ps.getProperty(StatisticProperty.WIN));
          s.put(LOST, ps.getProperty(StatisticProperty.LOST));
          s.put(DRAW, ps.getProperty(StatisticProperty.DRAW));
          s.put(RANK, ps.getProperty(StatisticProperty.RANK));
          returnValue.put(ps.getProperty(StatisticProperty.GAMEID), s);
        } catch (JSONException e) {
          // TODO: handle exception
          e.printStackTrace();
        }
      }
      try {
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
