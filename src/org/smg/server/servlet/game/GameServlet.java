package org.smg.server.servlet.game;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_DEVELOPER_ID;
import static org.smg.server.servlet.developer.DeveloperConstants.MISSING_INFO;
import static org.smg.server.servlet.developer.DeveloperConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.game.GameUtil.*;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet {

  private static final String[] validParams = { HAS_TOKENS, PICS, DEVELOPER_ID, 
    GAME_NAME, DESCRIPTION, URL, WIDTH, HEIGHT, ACCESS_SIGNATURE };

  private Map<Object, Object> deleteInvalid(Map<Object, Object> params,
      String[] validParams) {
    Map<Object, Object> returnMap = new HashMap<Object, Object>();
    for (Map.Entry<Object, Object> entry : params.entrySet()) {
      if (Arrays.asList(validParams).contains(entry.getKey())) {
        if (entry.getKey() instanceof String) {
          returnMap.put(entry.getKey(), entry.getValue());
        }
      }
    }
    if (returnMap.containsKey(HAS_TOKENS) == false)
      returnMap.put(HAS_TOKENS, false);
    return returnMap;
  }

  private void put(JSONObject jObj, String key, String value, HttpServletResponse resp) {
    try {
      jObj.put(key, value);
      resp.setContentType("text/plain");
      jObj.write(resp.getWriter());
    } catch (Exception e) {
      return;
    }
  }

  private boolean parsePathForPost(String pathInfo) {
    if (pathInfo == null)
      return true;
    if (pathInfo.length() > 0) {
      if (pathInfo.length() == 1) {
        if (pathInfo.charAt(0) != '/') {
          return false;
        } else
          return true;
      }
      return false;

    }
    return true;
  }

  @SuppressWarnings("rawtypes")
  private boolean developerIdExists(String idAsStr) {
    try {
      long developerId = Long.parseLong(idAsStr);
      Map developer = DeveloperDatabaseDriver.getDeveloperMap(developerId);
      if (developer == null)
        return false;
      else
        return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean gameNameDuplicate(long gameId, Map<Object, Object> parameterMap) {
    return GameDatabaseDriver.checkGameNameDuplicate(gameId, parameterMap);
  }

  private boolean requiredFieldForUpdate(Map<Object, Object> parameterMap) {
    if (parameterMap.get(DEVELOPER_ID) == null)
      return false;
    return true;
  }

  private boolean requiredFieldsComplete(Map<Object, Object> parameterMap) {
    if (parameterMap.get(DEVELOPER_ID) == null) {
      return false;
    }

    if (parameterMap.get(GAME_NAME) == null) {
      return false;
    }

    if (parameterMap.get(DESCRIPTION) == null) {
      return false;
    }

    if (parameterMap.get(URL) == null) {
      return false;
    }

    if (parameterMap.get(WIDTH) == null) {
      return false;
    }

    if (parameterMap.get(HEIGHT) == null) {
      return false;
    }

    if (parameterMap.get(ACCESS_SIGNATURE) == null) {
      return false;
    }
    return true;

  }

  private boolean gameNameDuplicate(Map<Object, Object> parameterMap) {
    return GameDatabaseDriver.checkGameNameDuplicate(parameterMap);
  }

  private boolean gameIdExist(long gameId) {
    try {
      return GameDatabaseDriver.checkGameIdExists(gameId);
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }

  private void returnMetaInfo(long gameName, HttpServletResponse resp)
      throws IOException, JSONException {
    JSONObject metainfo = new JSONObject();

    Entity targetEntity;
    try {
      targetEntity = GameDatabaseDriver.getGame(gameName);
      metainfo.put(GAME_NAME, targetEntity.getProperty(GAME_NAME));
      metainfo.put(HAS_TOKENS, targetEntity.getProperty(HAS_TOKENS));
      metainfo.put(URL, targetEntity.getProperty(URL));
      metainfo.put(DESCRIPTION, targetEntity.getProperty(DESCRIPTION));
      metainfo.put(WIDTH, targetEntity.getProperty(WIDTH));
      metainfo.put(HEIGHT, targetEntity.getProperty(HEIGHT));
      metainfo.put(POST_DATE, targetEntity.getProperty(POST_DATE));
      if (targetEntity.hasProperty(PICS))
      {
    	  Text picText = (Text)targetEntity.getProperty(PICS);
    	  JSONObject picMap = new JSONObject(picText.getValue());
    	  System.out.println("I am finished");
    	  metainfo.put(PICS, picMap);
      }
      metainfo.put(DEVELOPER_ID, targetEntity.getProperty(DEVELOPER_ID));

      metainfo.write(resp.getWriter());
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String pathInfo = req.getPathInfo();
    CORSUtil.addCORSHeader(resp);
    JSONObject jObj = new JSONObject();
    StringBuffer buffer = new StringBuffer();
    String line = null;

    try {
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
      System.out.println(buffer.toString());
      Map<Object, Object> parameterMap = deleteInvalid(
          (Map) JSONUtil.parse(buffer.toString()), validParams);
      System.out.println("fnished");
      if (parsePathForPost(pathInfo) == false) {

        put(jObj, ERROR, URL_ERROR, resp);
        return;
      }

      if (requiredFieldsComplete(parameterMap) == false) {
        put(jObj, ERROR, MISSING_INFO, resp);
        return;
      }
      if (developerIdExists((String) parameterMap.get(DEVELOPER_ID)) == false) {
        put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);

        return;
      }
      if (signatureRight(parameterMap) == false) {
        put(jObj, ERROR, WRONG_ACCESS_SIGNATURE, resp);
        return;
      }
      if (gameNameDuplicate(parameterMap) == true) {
        put(jObj, ERROR, GAME_EXISTS, resp);
        return;
      } else {
        long gameId = GameDatabaseDriver.saveGameMetaInfo(parameterMap);
        jObj.put(GAME_ID, Long.toString(gameId));
        jObj.write(resp.getWriter());

      }
    } catch (Exception e) {
      put(jObj, ERROR, INVALID_JSON, resp);
      return;
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String targetIdStr = null;
    JSONObject jObj = new JSONObject();
    long targetId = 0;
    CORSUtil.addCORSHeader(resp);
    try {
      targetIdStr = req.getPathInfo().substring(1);
      targetId = Long.parseLong(targetIdStr);
      if (gameIdExist(targetId) == false) {
        put(jObj, ERROR, WRONG_GAME_ID, resp);
        return;
      } else {
        try {
          returnMetaInfo(targetId, resp);
        } catch (JSONException e) {
          put(jObj, ERROR, INVALID_JSON, resp);
        }
      }
    } catch (Exception e) {
      put(jObj, ERROR, URL_ERROR, resp);
      return;
    }

  }

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String targetId = null;
    JSONObject jObj = new JSONObject();
    CORSUtil.addCORSHeader(resp);
    try {
      targetId = req.getPathInfo().substring(1);
      long gameId = Long.parseLong(targetId);
      String developerId = req.getParameter(DEVELOPER_ID);
      if (developerIdExists(developerId) == false) {
        put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);
        return;
      }
      if (signatureRight(req) == false) {

        put(jObj, ERROR, WRONG_ACCESS_SIGNATURE, resp);
        return;
      }
      if (gameIdExist(gameId) == false) {
        put(jObj, ERROR, WRONG_GAME_ID, resp);
        return;
      }
      Entity targetEntity = GameDatabaseDriver.getGame(gameId);
      List<String> developerList = (List<String>) targetEntity
          .getProperty(DEVELOPER_ID);
      if (developerList.contains(developerId) == false) {
        put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);
        return;
      }

      GameDatabaseDriver.deleteGame(targetId);
      put(jObj, SUCCESS, DELETED_GAME, resp);
    } catch (Exception e) {
      put(jObj, ERROR, URL_ERROR, resp);

      return;
    }

  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    JSONObject jObj = new JSONObject();
    CORSUtil.addCORSHeader(resp);

    String gameId = null;
    StringBuffer buffer = new StringBuffer();
    String line = null;
    try {
      gameId = req.getPathInfo().substring(1);
      long longId = Long.parseLong(gameId);
      try {
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
          buffer.append(line);
        }
        Map<Object, Object> parameterMap = deleteInvalid(
            (Map) JSONUtil.parse(buffer.toString()), validParams);
        if (requiredFieldForUpdate(parameterMap) == false) {
          put(jObj, ERROR, MISSING_INFO, resp);
          return;
        }
        if (developerIdExists((String) parameterMap.get(DEVELOPER_ID)) == false) {
          put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);

          return;
        }
        if (signatureRight(parameterMap) == false) {
          put(jObj, ERROR, WRONG_ACCESS_SIGNATURE, resp);
          return;
        }
        if (gameIdExist(Long.parseLong(gameId)) == false) {
          put(jObj, ERROR, WRONG_GAME_ID, resp);
          return;
        }

        if (gameNameDuplicate(longId, parameterMap) == true) {
          put(jObj, ERROR, GAME_EXISTS, resp);
          return;
        }
        Entity targetEntity = GameDatabaseDriver.getGame(longId);
        List<String> developerList = (List<String>) targetEntity
            .getProperty(DEVELOPER_ID);
        if (developerList.contains(parameterMap.get(DEVELOPER_ID)) == false) {
          put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);
          return;
        }

        GameDatabaseDriver.updateGame(longId, parameterMap);

        put(jObj, SUCCESS, UPDATED_GAME, resp);
      } catch (Exception e) {
        put(jObj, ERROR, INVALID_JSON, resp);

        return;
      }
    } catch (Exception e) {
      put(jObj, ERROR, URL_ERROR, resp);
      return;
    }
  }
}