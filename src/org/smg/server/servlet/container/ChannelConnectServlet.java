
package org.smg.server.servlet.container;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.database.ContainerDatabaseDriver;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class ChannelConnectServlet extends HttpServlet {

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    String urlPath = req.getPathInfo();
    if (urlPath.indexOf("/connected/") != -1) {
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      ChannelPresence presence = channelService.parsePresence(req);
      String clientId = presence.clientId();

      JSONObject returnValue = new JSONObject();
      try {
        returnValue.put("magic_msg", "Hello " + clientId + "!");
      } catch (JSONException e1) {
      }
      channelService.sendMessage(new ChannelMessage(clientId, returnValue.toString()));
    } else if (urlPath.indexOf("/disconnected/") != -1) {
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      ChannelPresence presence = channelService.parsePresence(req);
      long playerId = Long.parseLong(presence.clientId());

      ContainerDatabaseDriver.deleteQueueEntity(playerId);
    }
    resp.getWriter().close();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
  }
}
