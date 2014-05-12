package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.ERROR;
import static org.smg.server.servlet.user.UserConstants.FILTER;
import static org.smg.server.servlet.user.UserConstants.FRIEND_LIST;
import static org.smg.server.servlet.user.UserConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.WRONG_USER_ID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverFriendBlackList;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.database.models.FriendBlackList;
import org.smg.server.database.models.FriendBlackList.FriendType;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class UserFriendBlackList extends HttpServlet{

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   * Gets a user's list of friends with filter: 
   * {"type":"f/g","socialId":"316530","SMGId":"393933922"}.
   * parameter:
   *  filter : "1" return friend list without blacklist
   *  filter : "0" return friend list only in blacklist
   *  accessSignature
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    String filter = req.getParameter(FILTER);
    
    boolean ft;
    try {
      ft = filter.equals("1");
    } catch (Exception e1) {
      ft = true;
    }
    String friendListString = null;

    try {
      long userId = Long.parseLong(req.getPathInfo().substring(1));
      Map user = UserDatabaseDriver.getUserMap(userId);
      FriendBlackList fbl = DatabaseDriverFriendBlackList
          .getFriendBlackList(String.valueOf(userId));
      if (user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        if (user.containsKey(FRIEND_LIST)) {
          friendListString = ((Text) user.get(FRIEND_LIST)).getValue();
        }
        else {
          friendListString = "[]";
        }
        JSONArray fl2 = new JSONArray(friendListString);
//        JSONArray fl2 = new JSONArray();
//        for (int i = 100; i < 1000; i += 100){
//          JSONObject obj = new JSONObject();
//          obj.put("type", "g");
//          obj.put("socialId", String.valueOf(i));
//          obj.put("SMGId", String.valueOf(i*100));
//          fl2.put(obj);
//        }
        JSONArray fl = new JSONArray();
        
        for (int i = 0; i < fl2.length(); i ++){
          JSONObject obj = fl2.getJSONObject(i);
          String type = (String)obj.get("type");
          String id = (String)obj.get("socialId");
          if (type.equals("f")) {
            if (fbl.getFacebookSet().contains(id) == ft) {
              continue;
            }
          } else if (type.equals("g")) {
            if (fbl.getGoogleSet().contains(id) == ft) {
              continue;
            }
          }
          fl.put(obj);
        }
        UserUtil.jsonPut(json, FRIEND_LIST, fl);
      } else {
        UserUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
        UserUtil.jsonPut(json, "parameters", req.getParameterMap());
      }
    } catch (Exception e) {
      e.printStackTrace();
      UserUtil.jsonPut(json, ERROR, WRONG_USER_ID);
      UserUtil.jsonPut(json, "parameters", req.getParameterMap());
    }

    try {
      json.write(writer);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Add or remove a User from friend filtered list 
   * POST /userfriendfiltter/{userId}
      data input:
      {
        "operation¡±: "add¡± or "remove¡±,
        "accessSignature¡±:...,
        "socialId¡±: facebook or G+ id,
        "type¡±: "f¡± or "g¡±,
      }
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    BufferedReader br = new BufferedReader(new InputStreamReader(
        req.getInputStream()));
    String json = new String();
    String line = "";
    StringBuffer buffer = new StringBuffer();
    while ((line = br.readLine()) != null)
      buffer.append(line);
    json = buffer.toString();
    Map<String, Object> map = JSONUtil.parse(json);
    try {
      String accessSignature = (String)map.get(ACCESS_SIGNATURE);
      long userId = Long.parseLong(req.getPathInfo().substring(1));
      Map user = UserDatabaseDriver.getUserMap(userId);
      if (!user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        addErrorMessage(returnValue,WRONG_ACCESS_SIGNATURE,resp,map);
        return;
      }
      String op;
      try {
        op = (String) map.get("operation");
        op = op.toLowerCase();
      } catch (Exception e1) {
        addErrorMessage(returnValue,"INVALID_OPERATION",resp,map);
        return;
      }
      String socialId;
      try {
        socialId = (String) map.get("socialId");
      } catch (Exception e1) {
        addErrorMessage(returnValue,"WRONG_SOCIAL_ID",resp,map);
        return;
      }
      FriendType ft;
      try {
        String socialType = (String) map.get("type");
        if (socialType.equals("f")) {
          ft = FriendType.Facebook;
        } else if (socialType.equals("g")) {
          ft = FriendType.Google;
        } else {
          throw new Exception();
        }
      } catch (Exception e) {
        addErrorMessage(returnValue,"INVALID_TYPE",resp,map);
        return;
      }
      String id = String.valueOf(userId);
      FriendBlackList fbl = DatabaseDriverFriendBlackList.getFriendBlackList(id);
      if (op.equals("add")){
        fbl.addFriend(socialId, ft);
      } else if (op.equals("remove")) {
        fbl.removeFriend(socialId, ft);
      } else {
        addErrorMessage(returnValue,"INVALID_OPERATION",resp,map);
        return;
      }
      DatabaseDriverFriendBlackList.saveFriendBlackList(fbl);
      returnValue.put("SUCCESS", op);
      returnValue.write(resp.getWriter());
    } catch (Exception e) {
      addErrorMessage(returnValue,WRONG_USER_ID,resp,map);
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void addErrorMessage(JSONObject returnValue, 
      String errorMessage, HttpServletResponse resp, Map<String, Object> map) throws IOException {
    try {
      returnValue.put("error", errorMessage);
      returnValue.put("parameters", map);
      returnValue.write(resp.getWriter());
    } catch (JSONException e2) {
      e2.printStackTrace();
    }
  }
}
