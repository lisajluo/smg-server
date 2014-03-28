
package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class QueueServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    CORSUtil.addCORSHeader(resp);
    String json = Utils.getBody(req);

    JSONObject returnValue = new JSONObject();

    if (json != null && !json.isEmpty()) {
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

      long playerId = Long.parseLong((String) jsonMap.get(ContainerConstants.PLAYER_ID));
      if (!ContainerVerification.playerIdVerify(playerId)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_PLAYER_ID);
          returnValue.write(resp.getWriter());
        } catch (JSONException e) {
        }
        return;
      }
      String accessSignature = String.valueOf(jsonMap.get(ContainerConstants.ACCESS_SIGNATURE));
      if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_ACCESS_SIGNATURE);
          returnValue.write(resp.getWriter());
        } catch (JSONException e) {
        }
        return;
      }
      long gameId = 0;
      try {
        gameId = Long.parseLong((String) (jsonMap.get(ContainerConstants.GAME_ID)));
      } catch (Exception e) {
        try {
          returnValue.put(ContainerConstants.ERROR, ContainerConstants.WRONG_GAME_ID);
          returnValue.write(resp.getWriter());
        } catch (JSONException e1) {
        }
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

      // Token Generating.
      String userId = String.valueOf(playerId);
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      String token = channelService.createChannel(Utils.getClientId(userId));

      // Send token back to client.
      try {
        returnValue.put(ContainerConstants.CHANNEL_TOKEN, token);
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
