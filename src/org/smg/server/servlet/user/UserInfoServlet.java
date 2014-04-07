package org.smg.server.servlet.user;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.smg.server.servlet.user.UserConstants.*;

import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;
import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserInfoServlet extends HttpServlet{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
	    
	    try {
	      long userId = Long.parseLong(req.getPathInfo().substring(1));
	      Map user = UserDatabaseDriver.getUserMap(userId);	      
	      if (user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
	        UserDatabaseDriver.updateUser(userId, user);
	        json = new JSONObject(user);
	        json.remove(ACCESS_SIGNATURE);
	        json.remove(PASSWORD);
	      }
	      else {
	        UserUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	  @Override
	  public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
	      Map originalMap = (Map) JSONUtil.parse(buffer.toString());
	      String accessSignature = (String) originalMap.get(ACCESS_SIGNATURE);
	      Map<Object, Object> parameterMap = new HashMap<Object,Object> ();
	      Map<Object, Object> parameterMapOriginal = UserUtil.deleteInvalid(originalMap, validParams);
	      for (Object key:parameterMapOriginal.keySet())
	      {
	    	  if (parameterMapOriginal.get(key)!=null)
	    		  parameterMap.put(key,parameterMapOriginal.get(key));
	      }
	      if (parameterMap.containsKey(PASSWORD))
	      {
	    	  parameterMap.put(PASSWORD, AccessSignatureUtil.getHashedPassword((String)parameterMap.get(PASSWORD)));
	      }
	      long userId = Long.parseLong(req.getPathInfo().substring(1));
	      Map user = UserDatabaseDriver.getUserMap(userId);
	      if (user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
	        user.putAll(parameterMap);
	        boolean updated = UserDatabaseDriver.updateUserWithoutPassWord(userId, user);
	        
	        if (updated) {
	          UserUtil.jsonPut(json, SUCCESS, UPDATED_USER);
	        }
	        else {
	          UserUtil.jsonPut(json, ERROR, EMAIL_EXISTS);
	        }    
	      }
	      else {
	        UserUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
	      }      
	    }
	    catch (EntityNotFoundException | NullPointerException | NumberFormatException | 
	        IndexOutOfBoundsException e) {
	      UserUtil.jsonPut(json, ERROR, WRONG_USER_ID);
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

}
