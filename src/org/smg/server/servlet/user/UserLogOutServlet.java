package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.game.GameUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class UserLogOutServlet extends HttpServlet {
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  /**
   * doGet is called when a user is trying to do a logout
   * GET /userLogOut/{userId}?accessSignature=.....
   * The successful response:
   * {“success” : “LOG_OUT”}
   * 
   * After a successful logout, the accessSignature of that use
   * would be changed in the dataStore so that the original accessSignature
   * can't be used again
   * 
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    Map user = new HashMap();
    long userId = -1;
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    try {
      userId = Long.parseLong(req.getPathInfo().substring(1));
      user = UserDatabaseDriver.getUserMap(userId);
      if (user.get(ACCESS_SIGNATURE).equals(req.getParameter(ACCESS_SIGNATURE))) {
        String accessSignatureNew = AccessSignatureUtil.generate(userId);
        UserDatabaseDriver
            .updateUserAccessSignature(userId, accessSignatureNew);
        UserUtil.jsonPut(json, SUCCESS, LOG_OUT);
      } else {
    	  String url = GameUtil.getFullURL(req); 
		  String details = "The accessSignature you provide is not correct";
		  UserHelper.sendErrorMessageForUrl(resp, json,  WRONG_ACCESS_SIGNATURE, details,
						url);
		  return;
        
      }
    } catch (Exception e) {
      String url = GameUtil.getFullURL(req); 
	  String details = "The userId does not exist";
	  UserHelper.sendErrorMessageForUrl(resp, json, WRONG_USER_ID, details,
						url);
	  return;
     
    }
    resp.sendRedirect(DOMAIN+"login.html");
  }
}
