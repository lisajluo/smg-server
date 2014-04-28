package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.BLOBKEY;
import static org.smg.server.servlet.user.UserConstants.ERROR;
import static org.smg.server.servlet.user.UserConstants.FRIEND_LIST;
import static org.smg.server.servlet.user.UserConstants.PASSWORD;
import static org.smg.server.servlet.user.UserConstants.SOCIAL_AUTH;
import static org.smg.server.servlet.user.UserConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.WRONG_USER_ID;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class UserFriendsServlet extends HttpServlet {
  
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   * Gets a user's list of friends: {"type":"f/g","socialId":"316530","SMGId":"393933922"}.
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    String friendListString = null;

    try {
      long userId = Long.parseLong(req.getPathInfo().substring(1));
      Map user = UserDatabaseDriver.getUserMap(userId);
      if (user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        if (user.containsKey(FRIEND_LIST)) {
          friendListString = ((Text) user.get(FRIEND_LIST)).getValue();
        }
        else {
          friendListString = "[]";
        }
        JSONArray fl = new JSONArray(friendListString);
        UserUtil.jsonPut(json, FRIEND_LIST, fl);
      } else {
        UserUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
      }
    } catch (Exception e) {
      e.printStackTrace();
      UserUtil.jsonPut(json, ERROR, WRONG_USER_ID);
    }

    try {
      json.write(writer);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}
