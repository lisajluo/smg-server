package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class QueueServlet extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   * When a player want to add in a waiting queue of a game, he need to send
   * request to the server. The server will return a Channel API token to the
   * player. Then the player can create channel using the token. We will keep
   * the channel until game ends.
   * 
   * JSON from client: {"accessSignature": ..., "playerId": "1234", "gameId":
   * "12312"}
   * 
   * JSON to client: "channelToken":
   * "e80e601d49cdff49abcafffd290202a5-channel-d9clr0-1395956358130-6372769394589696"
   * ,"playerIds["123","234"]}
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    CORSUtil.addCORSHeader(resp);
    BufferedReader br = new BufferedReader(new InputStreamReader(
        req.getInputStream()));
    String json = "";
    if (br != null) {
      json = br.readLine();
    }
    JSONObject returnValue = new JSONObject();
    if (json != null) {
      // parse json message to jsonMap
      Map<String, Object> jsonMap = null;
      try {
        jsonMap = JSONUtil.parse(json);
      } catch (IOException e) {
        String details = "Json string from client is not in the correct format.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.JSON_PARSE_ERROR, details, json);
        return;
      }
      // check if missing info
      if (!jsonMap.containsKey(ContainerConstants.PLAYER_ID)
          || !jsonMap.containsKey(ContainerConstants.ACCESS_SIGNATURE)
          || !jsonMap.containsKey(ContainerConstants.GAME_ID)) {
        String details = "Json string from client does not contains all the required field."
            + "Requires: playerId, accessSignature, gameId.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.MISSING_INFO, details, json);
        return;
      }
      // verify playerId
      String pId = String.valueOf(jsonMap.get(ContainerConstants.PLAYER_ID));
      long playerId = 0;
      try {
        playerId = IDUtil.stringToLong(pId);
      } catch (Exception e) {
        String details = "PlayerIds is not in the correct format. Cannot be negative or zero";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_PLAYER_ID, details, json);
        return;
      }
      if (!ContainerVerification.playerIdVerify(playerId)) {
        String details = "PlayerIds do not exist in our datastore.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_PLAYER_ID, details, json);
        return;
      }
      // verify accessSignature
      String accessSignature = String.valueOf(jsonMap
          .get(ContainerConstants.ACCESS_SIGNATURE));
      if (!ContainerVerification.accessSignatureVerify(accessSignature,
          playerId)) {
        String details = "Access signature is not associated with any of playerId client provided.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_ACCESS_SIGNATURE, details, json);
        return;
      }
      // parse gameID and verify gameId existed
      String gId = String.valueOf(jsonMap.get(ContainerConstants.GAME_ID));
      long gameId = 0;
      try {
        gameId = IDUtil.stringToLong(gId);
      } catch (Exception e) {
        String details = "GameId is not in the correct format. Cannot be negative or zero.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_GAME_ID, details, json);
        return;
      }
      if (!ContainerVerification.gameIdVerify(gameId)) {
        String details = "GameId does not exist in our datastore.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.WRONG_GAME_ID, details, json);
        return;
      }
      // verify if player already in queue
      if (ContainerVerification.playerAlreadyInQueue(playerId)) {
        String details = "This player already in queue.";
        ContainerVerification.sendErrorMessage(resp, returnValue,
            ContainerConstants.ENQUEUE_FAILED, details, json);
        return;
      }

      // Token Generating.
      String userId = String.valueOf(playerId);
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      String channelToken = Utils.encodeToChannelId(userId,
          String.valueOf(gameId));
      String clientToken = channelService.createChannel(channelToken);

      Map<String, Object> entityMap = ImmutableMap.<String, Object> of(
          ContainerConstants.GAME_ID, gameId, ContainerConstants.PLAYER_ID,
          playerId, ContainerConstants.CHANNEL_TOKEN, channelToken);

      /*
       * Try making a match.
       */
      int playerCount = ContainerDatabaseDriver.getPlayerNumberInQueue(gameId);
      List<String> playerIdsToBeSent = Lists.newArrayList();

      // Currently we will start a match for exact 2 players.
      if (playerCount >= 1) {
        List<Entity> playerIdsEntity = ContainerDatabaseDriver
            .getPlayersInQueue(gameId, 1);
        for (Entity playerEntity : playerIdsEntity) {
          Map<String, Object> props = playerEntity.getProperties();
          String channelId = (String) props
              .get(ContainerConstants.CHANNEL_TOKEN);
          String pid = Utils.decodeChannel(channelId)[0];
          playerIdsToBeSent.add(pid);
        }

        // Delete the players who has been selected in a Match.
        for (String id : playerIdsToBeSent) {
          ContainerDatabaseDriver.deleteQueueEntity(
              Long.parseLong(Utils.decodeChannel(id)[0]), gameId);
        }

        playerIdsToBeSent.add(String.valueOf(playerId));
        try {
          returnValue.put(ContainerConstants.PLAYER_IDS, playerIdsToBeSent);
        } catch (JSONException e) {
        }
      } else {
        ContainerDatabaseDriver.insertQueueEntity(entityMap);
      }

      // Send token back to client.
      try {
        returnValue.put(ContainerConstants.CHANNEL_TOKEN, clientToken);
      } catch (JSONException e) {
      }

    } else {      
      String details = "No json received";
      ContainerVerification.sendErrorMessage(resp, returnValue,
          ContainerConstants.NO_DATA_RECEIVED, details, json); 
      return;
    }
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
    }
  }
}
