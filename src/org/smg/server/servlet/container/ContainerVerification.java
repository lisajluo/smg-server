package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.database.GameDatabaseDriver;

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
    Entity entity = ContainerDatabaseDriver.getEntityByKey(ContainerConstants.PLAYER, playerId);
    if (entity.getProperty(ContainerConstants.DS_ACCESS_SIGNATURE).equals(accessSignature)) {
      return true;
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
    Entity entity = ContainerDatabaseDriver.getEntityByKey(ContainerConstants.PLAYER, playerId);
    if (entity == null) {
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
    //TODO get rid of Version info
    Entity entity;
    try {
      entity = GameDatabaseDriver.getGame(gameId);
      return true;
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
