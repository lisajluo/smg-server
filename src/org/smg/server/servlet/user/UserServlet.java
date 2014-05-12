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
import org.smg.server.servlet.game.GameHelper;
import org.smg.server.servlet.game.GameUtil;
import org.smg.server.servlet.image.ImageUtil;
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
	  /**
	   * doPost is called when a new user is created
	   * POST /user
	   * 
	   *  {
	   *   “email”: …, //required
       *   “password”: …,  //required 
       *   “firstname”: …, //optional
       *   “lastname”: …, //optional
       *   “nickname”: …, //optional
       *   }
       *
       *  A successful response:
       *  {userId”: “1230850494”, “accessSignature”: ...}
       *
	   */
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    PrintWriter writer = resp.getWriter();

	    JSONObject json = new JSONObject();
	    //the parameters that should be kept in the json input 
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
				
					String details = "Your password length is less than 6";
					UserHelper.sendErrorMessageForJson(resp, json,  PASSWORD_TOO_SHORT, details,
							buffer.toString());
					return;
			
				
			}
	      if (parameterMap.get(EMAIL) == null || parameterMap.get(PASSWORD) == null) {
	    	String details = "Please provide your email and password to register";
	    	UserHelper.sendErrorMessageForJson(resp, json,  MISSING_INFO, details,
					buffer.toString());
	        return;
	      }
	      else {
	        // Pick random avatar
	        String imageUrl = DOMAIN + ImageUtil.getAvatarURL();
	        parameterMap.put(UserConstants.IMAGEURL, imageUrl);
	        
	        // Add to database
	        long userId = UserDatabaseDriver.insertUser(parameterMap);
	        
	        if (userId == INVALID) {
	          String details = "Your email address has been registered";
		      UserHelper.sendErrorMessageForJson(resp, json,  EMAIL_EXISTS, details,
						buffer.toString());
		        return;
	          
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
	      String details = "Your input json format is invalid";
	      UserHelper.sendErrorMessageForJson(resp, json,  INVALID_JSON, details,
					buffer.toString());
	      return;
	    }

	    try {
	      json.write(writer);
	    } 
	    catch (JSONException e) {
	      e.printStackTrace();
	    }
	  }
	/*
	 * doGet is called when a user is trying to log in
	 * GET /user/{userId}?password=...
	 * or GET /user/?email=.....&password=...
	 * Clients can use email instead of userId to login
	 * 
	 * If the password is correct, then it will return the player info including email and an accessSignature :
     * {
     *  “email”: …, 
     *  “accessSignature”: …//MD5 Hash String
     *  }
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
					String url = GameUtil.getFullURL(req); 
					String details = "The email you provide does not exist";
					UserHelper.sendErrorMessageForUrl(resp, json,  WRONG_EMAIL, details,
							url);
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
			if (user.get(SOCIAL_AUTH) != null) {
				String url = GameUtil.getFullURL(req); 
				String details = "This is a socialAuth Account, please login via socialAuth";
				UserHelper.sendErrorMessageForUrl(resp, json,  SOCIAL_AUTH_ACCOUNT, details,
						url);
				return;

			} else {
				if (user.get(PASSWORD).equals(
						AccessSignatureUtil.getHashedPassword(req
								.getParameter(PASSWORD)))) {
					user.put(ACCESS_SIGNATURE,
							AccessSignatureUtil.generate(userId));
					user.put(PASSWORD, req.getParameter(PASSWORD));
					UserDatabaseDriver.updateUser(userId, user);
					user.remove(PASSWORD);
					user.put(USER_ID, userId);
					json = new JSONObject(user);
				} else {
					String url = GameUtil.getFullURL(req); 
					String details = "Your password is wrong";
					UserHelper.sendErrorMessageForUrl(resp, json,  WRONG_PASSWORD, details,
							url);
					return;
				}
			}
		} catch (Exception e) {
			String url = GameUtil.getFullURL(req); 
			String details = "The userId you provide does not exist";
			UserHelper.sendErrorMessageForUrl(resp, json,  WRONG_USER_ID, details,
					url);
			return;
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
	  /**
	   * doDelete is called when a user is trying to delete the account
	   * 
	   * DELETE /user/{userId}?accessSignature=...
	   * 
	   * It will delete the player and all its associated data 
	   * (including matches with other players}
	   *  If this is a successful delete, it will return:
       * {“success”: “DELETED_USER”}
	   */
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
	    	String url = GameUtil.getFullURL(req); 
			String details = "The accessSignature is not correct";
			UserHelper.sendErrorMessageForUrl(resp, json,  WRONG_ACCESS_SIGNATURE, details,
						url);
	        return;
	      }
	    }
	    catch (Exception e) {
			String url = GameUtil.getFullURL(req);
			String details = "The userId does not exist";
			UserHelper.sendErrorMessageForUrl(resp, json, WRONG_USER_ID,
					details, url);
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
