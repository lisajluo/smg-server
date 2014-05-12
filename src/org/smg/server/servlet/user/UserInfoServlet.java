package org.smg.server.servlet.user;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.smg.server.servlet.game.GameUtil.put;
import static org.smg.server.servlet.user.UserConstants.*;

import org.smg.server.servlet.game.GameUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;
import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class UserInfoServlet extends HttpServlet{
	/**
	 * doGet is called when a user wants to get the complete info of that user
	 * GET /userinfo/{userId}?accessSignature=... 
	 * 
	 * If the userId exists, then it will return the user info
     *  {
     *   “email”: “developer123@gmail.com”,
     *    “firstName”: “Leonardo”,  // optional
     *    “middleName”: “M”,  // optional
     *    “lastName”: “Turtle”,  // optional
     *    “nickname”: “Leo”  // optional,
     *   “imageURL”: “http://foo-bar.com/bar.gif”
     *   }
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
	    
	    if (req.getPathInfo().substring(1).equals(ALL))
	    {
	    	JSONObject allUsers = UserDatabaseDriver.getAllUser();
	    	put(allUsers, resp);
	        return;
	    }
	    try {
	      long userId = Long.parseLong(req.getPathInfo().substring(1));
	      Map user = UserDatabaseDriver.getUserMap(userId);	      
	      if (user.get(ACCESS_SIGNATURE).equals(accessSignature)) {
	    	if (user.get(EMAIL).equals(SUPER_ADMIN))
	    	{
	    		user.put(IS_SUPER, true);
	    		user.put(ADMIN, true);
	    	}
	        user.remove(BLOBKEY);
	        user.remove(FRIEND_LIST);
	        user.remove(SOCIAL_AUTH);
	        json = new JSONObject(user);
	        json.remove(ACCESS_SIGNATURE);
	        json.remove(PASSWORD);
	      }
	      else {
	    	String url = GameUtil.getFullURL(req); 
		    String details = "The accessSignature you provide is not correct";
			UserHelper.sendErrorMessageForUrl(resp, json,  WRONG_ACCESS_SIGNATURE, details,
						url);
	        return;
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      String url = GameUtil.getFullURL(req); 
		  String details = "The userId does not exist";
		  UserHelper.sendErrorMessageForUrl(resp, json,   WRONG_USER_ID, details,
		  url);
	      
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
	  /**
	   * doPut is called when the user is trying to update the userInfo
	   * PUT /userinfo/{userId}
	   * 
       *{“accessSignature”: …, //required
       *  “email”:.......,//Optional, the email you want to update to 
       *  “password”: …, //Optional below, the password you want to update to 
       *   “firstname”: …, //
       *   “lastname”: …, 
       *   “nickname”:..., 
       *    “imageURL”: “http://www.foo-bar.com/profilepic.gif”
       *  }
       *  
       *  The successful response:
       *  
       *   {“success”: “UPDATED_USER”}
	   */
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
					String details = "The email address you are trying to update is duplicated";
					UserHelper.sendErrorMessageForJson(resp, json,
							EMAIL_EXISTS, details, buffer.toString());
					return;
	        }    
	      }
	      else {
	    	  
	    	 String details = "Your access signature is incorrect";
			  UserHelper.sendErrorMessageForJson(resp, json,
					  WRONG_ACCESS_SIGNATURE, details, buffer.toString());
				return;
	       
	      }      
	    }
	    catch (EntityNotFoundException | NullPointerException | NumberFormatException | 
	        IndexOutOfBoundsException e) {
	      String details = "The userId you provide does not exist";
	      UserHelper.sendErrorMessageForJson(resp, json,
	    		  WRONG_USER_ID, details, buffer.toString());
			return;
	      
	    }
	    catch (Exception e) { 
	      e.printStackTrace();
	      String details = "Your input json format is invalid";
	      UserHelper.sendErrorMessageForJson(resp, json,
	    		  INVALID_JSON, details, buffer.toString());
	      return;
	      
	    }

	    try {
	      json.write(writer);
	    } 
	    catch (JSONException e) {
	      e.printStackTrace();
	    }
	  }

}
