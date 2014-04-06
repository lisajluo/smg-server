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
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserLogOutServlet extends HttpServlet{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    Map user = new HashMap();
	    long userId = -1;
	    PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
		try {
		    userId = Long.parseLong(req.getPathInfo().substring(1));
			user = UserDatabaseDriver.getUserMap(userId);
				if (user.get(ACCESS_SIGNATURE).equals(req
								.getParameter(ACCESS_SIGNATURE))) {
					String accessSignatureNew = AccessSignatureUtil.generate(userId);
					UserDatabaseDriver.updateUserAccessSignature(userId, accessSignatureNew);
					UserUtil.jsonPut(json, SUCCESS,LOG_OUT);
				} else {
					UserUtil.jsonPut(json, ERROR,WRONG_ACCESS_SIGNATURE);
				}
		} catch (Exception e) {
			UserUtil.jsonPut(json, ERROR, WRONG_USER_ID);
		}
	    try {
	      json.write(writer);
	    } 
	    catch (JSONException e) {
	      e.printStackTrace();
	    }
	  }
}
