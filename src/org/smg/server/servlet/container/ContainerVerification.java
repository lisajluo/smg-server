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

/**
 * ContainerVerification class contains a series of static methods used for
 * verification of servlets in container component.
 * 
 * @author piper
 * 
 */
public class ContainerVerification {

  /**
   * Verify if a accessSignature is associated with a playerId if yes return
   * true, if not return false
   * 
   * @param accessSignature
   * @param playerId
   * @return
   */
  public static boolean accessSignatureVerify(String accessSignature,
      long playerId) {
    try {
      Player player = DatabaseDriverPlayer.getPlayerById(playerId);
      if (player != null) {
        return player.getProperty(PlayerProperty.accessSignature).equals(
            accessSignature);
      }
    } catch (EntityNotFoundException e) {
      return false;
    }
    return false;
  }

  /**
   * Verify if a accessSignature is associated with one of playerId in list
   * playerIds if yes return true, if not return false
   * 
   * @param accessSignature
   * @param playerIds
   * @return
   */
  public static boolean accessSignatureVerify(String accessSignature,
      List<Long> playerIds) {
    for (Object obj : playerIds) {
      long playerId = Long.parseLong(String.valueOf(obj));
      if (accessSignatureVerify(accessSignature, playerId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verify if all playerIds is valid, which means existing in our datastore if
   * yes return true, if not return false
   * 
   * @param playerIds
   * @return
   */
  public static boolean playerIdsVerify(List<Long> playerIds) {
    for (Object obj : playerIds) {
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
   * Verify if a playerId is valid, which means existing in our datastore if yes
   * return true, if not return false
   * 
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
   * Verify if a player could insert new match for a game. When he has
   * unfinished match of this game, it is not allowed to insert new match. if
   * yes return true, if not return false
   * 
   * @param playerId
   *          , gameId
   * @return
   */
  public static boolean insertMatchVerify(long playerId, long gameId) {
    if (ContainerDatabaseDriver.getUnfinishedMatchByPlayerIdGameId(playerId,
        gameId) == null) {
      return true;
    }
    return false;
  }

  /**
   * Verify if all players could insert new match for a game. When he has
   * unfinished match, it is not allowed to insert new match. if yes return
   * true, if not return false
   * 
   * @param playerIds
   *          , gameId
   * @return
   */
  public static boolean insertMatchVerify(List<Long> playerIds, long gameId) {
    for (long playerId : playerIds) {
      if (!insertMatchVerify(playerId, gameId)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Verify if a matchId is valid, which means existing in our datastore if yes
   * return true, if not return false
   * 
   * @param matchId
   * @return
   */
  public static boolean matchIdVerify(long matchId) {
    Entity entity = ContainerDatabaseDriver.getEntityByKey(
        ContainerConstants.MATCH, matchId);
    if (entity == null) {
      return false;
    }
    return true;
  }

  /**
   * Verify if a gameId is valid, which means existing in our datastore if yes
   * return true, if not return false
   * 
   * @param gameId
   * @return
   */
  public static boolean gameIdVerify(long gameId) {
    // Entity entity =
    // ContainerDatabaseDriver.getEntityByKey(ContainerConstants.GAME, gameId);
    try {
      return GameDatabaseDriver.checkGameIdExists(gameId);
    } catch (EntityNotFoundException e) {
      return false;
    }
  }

  /**
   * Verify if a player already in waiting queue, if he is, it is not allowed to
   * enqueue again. if yes return true, if not return false
   * 
   * @param playerId
   * @return
   */
  public static boolean playerAlreadyInQueue(long playerId) {
    Entity entity = ContainerDatabaseDriver.getEntityByKey(
        ContainerConstants.QUEUE, playerId);
    if (entity == null) {
      return false;
    }
    return true;
  }

  /**
   * Send error message to the client Check {@link ContainerConstants} to find
   * all the error type and msg.
   * 
   * @param resp
   * @param returnValue
   * @param errorMSG
   */
  public static void sendErrorMessage(HttpServletResponse resp,
      JSONObject returnValue, String errorMSG) {
    try {
      returnValue.put(ContainerConstants.ERROR, errorMSG);
      returnValue.write(resp.getWriter());
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
  }
}
