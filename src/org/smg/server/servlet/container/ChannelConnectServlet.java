
package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.util.IDUtil;
import org.smg.server.util.JSONUtil;
import org.smg.server.database.ContainerDatabaseDriver;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class ChannelConnectServlet extends HttpServlet {

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CORSUtil.addCORSHeader(resp);
    }

    /**
     * Handle "/_ah/connected/*" from post requests.
     * The servlet is requested by app engine automatically.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException,
            IOException {
        String urlPath = req.getPathInfo();

        ChannelService channelService = ChannelServiceFactory.getChannelService();
        ChannelPresence presence = channelService.parsePresence(req);
        String[] channelId = Utils.decodeChannel(presence.clientId());
        String playerIdStr = channelId[0];
        String gameIdStr = channelId[1];
        long playerId = Long.parseLong(playerIdStr);
        long gameId = Long.parseLong(gameIdStr);

        if (urlPath.indexOf("/connected/") != -1) {
            JSONObject returnValue = new JSONObject();
            try {
                returnValue.put("magic_msg", "Hello " + playerId + "!");
            } catch (JSONException e1) {
            }
            channelService.sendMessage(new ChannelMessage(presence.clientId(), returnValue
                    .toString()));
        } else if (urlPath.indexOf("/disconnected/") != -1) {
            ContainerDatabaseDriver.deleteQueueEntity(playerId, gameId);

            // Notify other players that this player has disconnected.
            Entity matchEntity = ContainerDatabaseDriver.getUnfinishedMatchByPlayerIdGameId(
                    playerId,
                    gameId);
            if (matchEntity != null) {
                // add playerIds and matchId
                List<Long> playerIds = JSONUtil.parseDSPlayerIds(
                        (String) matchEntity.getProperty(ContainerConstants.PLAYER_IDS));
                List<String> pIds = IDUtil.longListToStringList(playerIds);
                JSONObject rtnJson = new JSONObject();
                try {
                    rtnJson.put(ContainerConstants.MESSAGE,
                            ContainerConstants.OPPONENTS_LOST_CONNECTION);
                } catch (JSONException e) {
                }
                for (String pId : pIds) {
                    channelService.sendMessage(new ChannelMessage(Utils.encodeToChannelId(pId,
                            gameIdStr),
                            rtnJson.toString()));
                }
            }
        }
        resp.getWriter().close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
    }
}
