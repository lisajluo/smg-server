package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ContainerVerification {
  
  /**
   * Verify if a accessSignature is associated with a playerId
   * @param accessSignature
   * @param playerId
   * @return
   */
  public static boolean accessSignatureVerify(String accessSignature, long playerId) {
    try {
      Player player = DatabaseDriverPlayer.getPlayerById(playerId);
      if (player != null) {
        return player.getProperty(PlayerProperty.ACCESSSIGNATURE).equals(accessSignature);
      }
    } catch (EntityNotFoundException e) {
      return false;
    }
    return false;
  }
  
  /**
   * Verify if a accessSignature is associated with one of playerId in list playerIds
   * @param accessSignature
   * @param playerIds
   * @return
   */
  public static boolean accessSignatureVerify(String accessSignature, List<Long> playerIds) {
    for (Object obj : playerIds) {
      long playerId = Long.parseLong(String.valueOf(obj));
      if (accessSignatureVerify(accessSignature,playerId)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Verify if all playerIds is valid
   * @param playerIds
   * @return
   */
  public static boolean playerIdsVerify(List<Long> playerIds) {
    for (Object obj: playerIds) {
      long playerId = 0;
      try {
        playerId = Long.parseLong(String.valueOf(obj));
      } catch (Exception e) {        
        return false;
      }
      if (!playerIdVerify(playerId)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Verify if a playerId is valid
   * @param playerId
   * @return
   */
  public static boolean playerIdVerify(long playerId) {
    try {
      Player player = DatabaseDriverPlayer.getPlayerById(playerId);
      if (player == null) {
        return false;
      }
    } catch (EntityNotFoundException e) {
      return false;
    }
    return true;
  }
  
  /**
   * Verify if a matchId is valid
   * @param matchId
   * @return
   */
  public static boolean matchIdVerify(long matchId) {
    Entity entity = ContainerDatabaseDriver.getEntityByKey(ContainerConstants.MATCH, matchId);
    if (entity == null) {
      return false;
    }
    return true;
  }
  
  /**
   * Verify if a gameId is valid
   * @param gameId
   * @return
   */
  public static boolean gameIdVerify(long gameId) {
    //Entity entity = ContainerDatabaseDriver.getEntityByKey(ContainerConstants.GAME, gameId);
    try {
      return GameDatabaseDriver.checkGameIdExists(gameId);
    } catch (EntityNotFoundException e) {
      return false;
    }
  }
  
  /**
   * send error message to the client
   * @param resp
   * @param returnValue
   * @param errorMSG
   */
  public static void sendErrorMessage (HttpServletResponse resp, 
      JSONObject returnValue, String errorMSG) {
    try {
      returnValue.put(ContainerConstants.ERROR, errorMSG);
      returnValue.write(resp.getWriter());
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }  
  }
}
