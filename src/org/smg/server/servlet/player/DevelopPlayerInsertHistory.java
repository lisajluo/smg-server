package org.smg.server.servlet.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverPlayerHistory;
import org.smg.server.database.DatabaseDriverPlayerStatistic;
import org.smg.server.database.models.PlayerHistory;
import org.smg.server.database.models.PlayerHistory.MatchResult;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class DevelopPlayerInsertHistory extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }
  
  /**
   * For Development purpose only.
   * Add a winning Info to certain player
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    BufferedReader br = new BufferedReader(new InputStreamReader(
        req.getInputStream()));
    String json = new String();
    String line = "";
    StringBuffer buffer = new StringBuffer();
    while ((line = br.readLine()) != null)
      buffer.append(line);
    json = buffer.toString();
    Map<String, Object> map = JSONUtil.parse(json);
    long playerId = Long.parseLong((String) map.get("playerId"));
    long gameId = Long.parseLong((String) map.get("gameId"));
    long matchId = Long.parseLong((String) map.get("matchId"));
    PlayerHistory ph = new PlayerHistory(playerId,gameId,matchId);
    ph.setDate(new Date());
    ph.setMatchResult(MatchResult.valueOf((String) map.get("result")));
    ph.setScore(Long.valueOf((String) map.get("score")));
    ph.setToken(Long.valueOf((String) map.get("token")));
    List<String> ids = (List<String>) map.get("opponentIds");
    System.out.println(map.get("opponentIds"));
    for (String id:ids) {
      ph.addOpponentId(Long.parseLong(id));
    }
    DatabaseDriverPlayerHistory.savePlayerHistory(ph);
    DatabaseDriverPlayerStatistic.savePlayerStatisticFromHistory(ph);
    JSONObject returnValue = new JSONObject();
    try {
      returnValue.put("success", "SUCCESS");
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
