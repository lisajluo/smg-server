
package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.util.IDUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class StateServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();

    // verify playerId
    String pId = req.getPathInfo().substring(1);
    long playerId = 0;
    try {
      playerId = IDUtil.stringToLong(pId);
    } catch (Exception e) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
      return;
    }
    if (!ContainerVerification.playerIdVerify(playerId)) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
      return;
    }
    // verify accessSignature
    String accessSignature = req.getParameter(ContainerConstants.ACCESS_SIGNATURE);
    if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
      ContainerVerification.sendErrorMessage(
          resp, returnValue, ContainerConstants.WRONG_ACCESS_SIGNATURE);
      return;
    }

    Entity stateForPlayerEntity = new Entity("sadads");
    Map<String, Object> stateForPlayerMap = stateForPlayerEntity.getProperties();
    try {
      returnValue.put(ContainerConstants.MATCH_ID,
          stateForPlayerMap.get(ContainerConstants.MATCH_ID));
      returnValue.put(ContainerConstants.STATE,
          stateForPlayerMap.get(ContainerConstants.GAME_STATE));
      returnValue.put(ContainerConstants.LAST_MOVE,
          stateForPlayerMap.get(ContainerConstants.LAST_MOVE));
    } catch (JSONException e1) {
    }
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    super.doPost(req, resp);
  }
}
