package org.smg.server.servlet.developer;

import static org.smg.server.servlet.developer.DeveloperConstants.*;
import static org.smg.server.servlet.user.UserConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.DOMAIN;
import static org.smg.server.servlet.user.UserConstants.EMAIL;
import static org.smg.server.servlet.user.UserConstants.ERROR;
import static org.smg.server.servlet.user.UserConstants.PASSWORD;
import static org.smg.server.servlet.user.UserConstants.SOCIAL_AUTH;
import static org.smg.server.servlet.user.UserConstants.SOCIAL_AUTH_ACCOUNT;
import static org.smg.server.servlet.user.UserConstants.USER_ID;
import static org.smg.server.servlet.user.UserConstants.WRONG_EMAIL;
import static org.smg.server.servlet.user.UserConstants.WRONG_PASSWORD;
import static org.smg.server.servlet.user.UserConstants.WRONG_USER_ID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.image.ImageUtil;
import org.smg.server.servlet.user.UserConstants;
import org.smg.server.servlet.user.UserUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class DeveloperServlet extends HttpServlet {  
  
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  /**
   * Delete a developer with developerId and accessSignature 
   * (/developers/{developerId}?accessSignature=...).
   */
  @SuppressWarnings({ "rawtypes" })
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    
    try {
      long developerId = Long.parseLong(req.getPathInfo().substring(1));
      Map developer = DeveloperDatabaseDriver.getDeveloperMap(developerId);
      
      if (developer.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        DeveloperDatabaseDriver.deleteDeveloper(developerId);
        DeveloperUtil.jsonPut(json, SUCCESS, DELETED_DEVELOPER);
      }
      else {
        DeveloperUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
      }
    }
    catch (Exception e) {
      DeveloperUtil.jsonPut(json, ERROR, WRONG_DEVELOPER_ID);
    }
    
    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Login a developer with developerId and password (/developers/{developerId}?password=...).
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
			if (user.get(SOCIAL_AUTH) != null) {
				UserUtil.jsonPut(json, ERROR, SOCIAL_AUTH_ACCOUNT);

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
					UserUtil.jsonPut(json, ERROR, WRONG_PASSWORD);
				}
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
  
  /**
   * Inserts a new developer (/developers/).
   */
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
      
      Map<Object, Object> parameterMap = DeveloperUtil.deleteInvalid(
          (Map) JSONUtil.parse(buffer.toString()), validParams);
      String originalString = (String) parameterMap.get(PASSWORD);
		if (originalString == null || originalString.length() < 6) {
			try {
				json.put("error", "PASSWORD_TOO_SHORT");
				json.write(resp.getWriter());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
      if (parameterMap.get(EMAIL) == null || parameterMap.get(PASSWORD) == null) {
        DeveloperUtil.jsonPut(json, ERROR, MISSING_INFO);
      }
      else {
        // Pick random avatar
        String imageUrl = DOMAIN + ImageUtil.getAvatarURL();
        parameterMap.put(UserConstants.IMAGEURL, imageUrl);
        
        // Add to database
        long developerId = DeveloperDatabaseDriver.insertDeveloper(parameterMap);
        
        if (developerId == INVALID) {
          DeveloperUtil.jsonPut(json, ERROR, EMAIL_EXISTS);
        }
        else {
          String accessSignature = AccessSignatureUtil.generate(developerId);
          parameterMap.put(ACCESS_SIGNATURE, accessSignature);
          // Update database with access signature
          DeveloperDatabaseDriver.updateDeveloper(developerId, parameterMap);
          
          // Return response  
          DeveloperUtil.jsonPut(json, DEVELOPER_ID, Long.toString(developerId));
          DeveloperUtil.jsonPut(json, ACCESS_SIGNATURE, accessSignature);
        }
      }
    }
    catch (Exception e) { 
      e.printStackTrace();
      DeveloperUtil.jsonPut(json, ERROR, INVALID_JSON);
    }

    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}