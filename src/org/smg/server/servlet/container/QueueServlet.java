
package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.IDUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("serial")
public class QueueServlet extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    
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
        ContainerVerification.sendErrorMessage(
           resp, returnValue, ContainerConstants.JSON_PARSE_ERROR);
        return;
      }

   // verify playerId
      String pId = String.valueOf(req.getParameter(ContainerConstants.PLAYER_ID));
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
      // parse gameID and verify gameId existed
      String gId = String.valueOf(jsonMap.get(ContainerConstants.GAME_ID));
      long gameId = 0;
      try {
        gameId = IDUtil.stringToLong(gId);
      } catch (Exception e) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_GAME_ID);
        return;
      }
      if (!ContainerVerification.gameIdVerify(gameId)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_GAME_ID);
        return;
      }

      // Token Generating.
      String userId = String.valueOf(playerId);
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      String channelToken = Utils.getClientId(userId);
      String clientToken = channelService.createChannel(channelToken);

      Map<String, Object> entityMap = ImmutableMap.<String, Object> of(
          ContainerConstants.GAME_ID, gameId,
          ContainerConstants.PLAYER_ID, playerId,
          ContainerConstants.CHANNEL_TOKEN, channelToken);

      ContainerDatabaseDriver.insertQueueEntity(entityMap);

      // Send token back to client.
      try {
        returnValue.put(ContainerConstants.CHANNEL_TOKEN, clientToken);
      } catch (JSONException e) {
      }

    } else {
      try {
        returnValue.put(ContainerConstants.ERROR, ContainerConstants.NO_DATA_RECEIVED);
      } catch (JSONException e) {
      }
    }
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
    }
  }
}
