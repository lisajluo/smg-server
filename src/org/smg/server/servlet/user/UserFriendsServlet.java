package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.ERROR;
import static org.smg.server.servlet.user.UserConstants.FRIEND_LIST;
import static org.smg.server.servlet.user.UserConstants.WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.WRONG_USER_ID;
import static org.smg.server.servlet.user.UserConstants.VERBOSE_WRONG_USER_ID;
import static org.smg.server.servlet.user.UserConstants.VERBOSE_WRONG_ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.DETAILS;
import static org.smg.server.servlet.user.UserConstants.USER_ID;

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

/**
 * Gets a user's list of Facebook/G+ friends and their SMG userId (if applicable).
 */
@SuppressWarnings("serial")
public class UserFriendsServlet extends HttpServlet {
  
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  /**
   * Gets a user's list of friends.
   * Call: GET /userfriendlist/{userId}?accessSignature=...
   * Returns: a list of { "type": "f/g", "socialId": "316530", "SMGId":"393933922" } if the user is 
   * a G+/Facebook user, otherwise returns an empty list.
   * If the userId is incorrect, then it returns: 
   * { 
   *   "error": "WRONG_USER_ID", 
   *   "userId": "...", 
   *   "accessSignature": "....", 
   *   "details": "The userId provided does not exist." 
   * }
   * If the accessSignature is incorrect, then it returns:
   * { 
   *   "error": "WRONG_ACCESS_SIGNATURE", 
   *   "userId": "...", 
   *   "accessSignature": "....", 
   *   "details": "The accessSignature provided is incorrect for the given userId." 
   * }
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    String friendListString = null;
    String stringUserId = "";

    try {
      stringUserId = req.getPathInfo().substring(1);
      long userId = Long.parseLong(stringUserId);
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
        UserUtil.jsonPut(json, ACCESS_SIGNATURE, accessSignature);
        UserUtil.jsonPut(json, USER_ID, stringUserId);
        UserUtil.jsonPut(json, DETAILS, VERBOSE_WRONG_ACCESS_SIGNATURE);
      }
    } catch (Exception e) {
      e.printStackTrace();
      UserUtil.jsonPut(json, ERROR, WRONG_USER_ID);
      UserUtil.jsonPut(json, ACCESS_SIGNATURE, accessSignature);
      UserUtil.jsonPut(json, USER_ID, stringUserId);
      UserUtil.jsonPut(json, DETAILS, VERBOSE_WRONG_USER_ID);
    }

    try {
      json.write(writer);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}
