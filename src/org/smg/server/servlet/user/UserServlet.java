package org.smg.server.servlet.user;


import static org.smg.server.servlet.user.UserConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.user.UserUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class UserServlet extends HttpServlet{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    PrintWriter writer = resp.getWriter();

	    JSONObject json = new JSONObject();
	    
	    String[] validParams = {EMAIL, PASSWORD, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICK_NAME};
	    StringBuffer buffer = new StringBuffer();
	    String line = null;
	    try {
	      BufferedReader reader = req.getReader();
	      while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	      }
	      
	      Map<Object, Object> parameterMap = UserUtil.deleteInvalid(
	          (Map) JSONUtil.parse(buffer.toString()), validParams);
	      String originalString = (String) parameterMap.get(PASSWORD);
			if (originalString == null || originalString.length() < 6) {
				try {
					json.put(ERROR, PASSWORD_TOO_SHORT);
					json.write(resp.getWriter());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return;
			}
	      if (parameterMap.get(EMAIL) == null || parameterMap.get(PASSWORD) == null) {
	        UserUtil.jsonPut(json, ERROR, MISSING_INFO);
	      }
	      else {
	        // Add to database
	        long userId = UserDatabaseDriver.insertUser(parameterMap);
	        
	        if (userId == INVALID) {
	          UserUtil.jsonPut(json, ERROR, EMAIL_EXISTS);
	        }
	        else {
	          String accessSignature = AccessSignatureUtil.generate(userId);
	          parameterMap.put(ACCESS_SIGNATURE, accessSignature);
	          UserDatabaseDriver.updateUser(userId, parameterMap);
	          UserUtil.jsonPut(json, USER_ID, Long.toString(userId));
	          UserUtil.jsonPut(json, ACCESS_SIGNATURE, accessSignature);
	        }
	      }
	    }
	    catch (Exception e) { 
	      e.printStackTrace();
	      UserUtil.jsonPut(json, ERROR, INVALID_JSON);
	    }

	    try {
	      json.write(writer);
	    } 
	    catch (JSONException e) {
	      e.printStackTrace();
	    }
	  }
	/*
	 * Log-in
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    Map user = new HashMap();
	    long userId = -1;
	    PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
		if (req.getParameter(EMAIL) != null) {
			List<Entity> userAsList = null;
			userAsList = UserDatabaseDriver.queryUserByProperty(EMAIL,
					req.getParameter(EMAIL));
			if (userAsList == null || userAsList.size() == 0) {
				try {
					UserUtil.jsonPut(json, ERROR, WRONG_EMAIL);
					json.write(resp.getWriter());
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			userId = userAsList.get(0).getKey().getId();
		}
		try {
			if (userId == -1) {
				userId = Long.parseLong(req.getPathInfo().substring(1));
			}
			user = UserDatabaseDriver.getUserMap(userId);
			if (user.get(PASSWORD).equals(AccessSignatureUtil.getHashedPassword(req.getParameter(PASSWORD)))) {
				user.put(ACCESS_SIGNATURE, AccessSignatureUtil.generate(userId));
				user.put(PASSWORD, req.getParameter(PASSWORD));
				UserDatabaseDriver.updateUser(userId, user);
				json = new JSONObject(user);
			} else {
				UserUtil.jsonPut(json, ERROR, WRONG_PASSWORD);
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
	@Override
	  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	  }
	@SuppressWarnings({ "rawtypes" })
	  @Override
	  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
	    
	    try {
	      long userId = Long.parseLong(req.getPathInfo().substring(1));
	      Map user = UserDatabaseDriver.getUserMap(userId);
	      
	      if (user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
	        UserDatabaseDriver.deleteUser(userId);
	        UserUtil.jsonPut(json, SUCCESS, DELETED_USER);
	      }
	      else {
	        UserUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
	      }
	    }
	    catch (Exception e) {
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
