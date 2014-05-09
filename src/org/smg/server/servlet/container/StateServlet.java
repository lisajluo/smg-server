package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.servlet.container.GameApi.GameState;
import org.smg.server.servlet.container.GameApi.Message;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.IDUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class StateServlet extends HttpServlet {

  /**
   * Add CORS header
   */
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   * URL: GET /state/{matchId}?playerId=...&accessSignature=...
   * 
   * /state/2323232?playerId=123344&accessSignature=SJDK99898J89JD
   * 
   * JSON toclient: {"matchId": "2323232", "state": { "stateKey1":
   * "stateValue1", "stateKey2": 1234, "stateKey3": ["a1","a2"], "stateKey4":
   * {...} } "lastMove": [...] }
   * 
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();

    // verify matchId
    if (req.getPathInfo().length() < 2) {
      String details = "MatchId in url is not correct";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_MATCH_ID, details, json);
      return;
    }
    String mId = req.getPathInfo().substring(1);
    long matchId = 0;
    try {
      matchId = IDUtil.stringToLong(mId);
    } catch (Exception e) {
      String details = "MatchId in url is not int the correct format. "
          + "Cannot be negative or zero";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_MATCH_ID, details, json);
      return;
    }
    if (!ContainerVerification.matchIdVerify(matchId)) {
      String details = "MatchId do not exist in our datastore.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_MATCH_ID, details, json);
      return;
    }
    // verify playerId
    if (!req.getParameterMap().containsKey(ContainerConstants.PLAYER_ID)) {
      String details = "No playerId received.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.PLAYER_ID, details, json);
      return;
    }
    String pId = String.valueOf(req.getParameter(ContainerConstants.PLAYER_ID));
    long playerId = 0;
    try {
      playerId = IDUtil.stringToLong(pId);
    } catch (Exception e) {
      String details = "PlayerId in url is not int the correct format. "
          + "Cannot be negative or zero";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_PLAYER_ID, details, json);
      return;
    }
    if (!ContainerVerification.playerIdVerify(playerId)) {
      String details = "PlayerId does not exist in our datastore.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_PLAYER_ID, details, json);
      return;
    }
    // verify accessSignature
    if (!req.getParameterMap().containsKey(ContainerConstants.ACCESS_SIGNATURE)) {
      String details = "No access signature received.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_ACCESS_SIGNATURE, details, json);
      return;
    }
    String accessSignature = req
        .getParameter(ContainerConstants.ACCESS_SIGNATURE);
    if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
      String details = "Access signature is not associated with playerId client provided.";
      String json = Utils.getFullURL(req);
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.WRONG_ACCESS_SIGNATURE, details, json);
      return;
    }

    Entity stateForPlayerEntity = ContainerDatabaseDriver.getEntityByKey(
        ContainerConstants.MATCH, matchId);
    MatchInfo mi;
    try {
      mi = MatchInfo.getMatchInfoFromEntity(stateForPlayerEntity);
    } catch (JSONException e2) {
      try {
        returnValue.put(ContainerConstants.ERROR,
            ContainerConstants.NO_MATCH_FOUND);
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
      }
      return;
    }

    GameState state;
    List<Map<String, Object>> lastMove;

    // At this time, the first player hasn't made the initial move.
    if (mi.getHistory().size() == 0) {
      state = new GameState();
      lastMove = Lists.newArrayList();
    } else {
      int lastIndex = mi.getHistory().size() - 1;
      state = mi.getHistory().get(lastIndex).getGameState();
      lastMove = mi.getHistory().get(lastIndex).getLastMove();
    }

    try {
      returnValue.put(ContainerConstants.PLAYER_THAT_HAS_LAST_TURN,
          String.valueOf(mi.getPlayerThatHasLastTurn()));
      returnValue.put(ContainerConstants.MATCH_ID,
          String.valueOf(mi.getMatchId()));
      returnValue.put(ContainerConstants.STATE,
          state.getStateForPlayerId(String.valueOf(playerId)));
      returnValue.put(ContainerConstants.LAST_MOVE, Message
          .listToMessage(GameStateHelper.getOperationsListForPlayer(
              GameStateHelper.messageToOperationList(lastMove),
              String.valueOf(playerId))));
    } catch (JSONException e1) {
    }
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
    }
  }
}
