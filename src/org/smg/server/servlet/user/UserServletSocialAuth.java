package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserServletSocialAuth extends HttpServlet{
	
	private Map<String,String> getInfoFromSocialAuth(String SocialAuth)
	{
		//TODO implement social Auth
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    CORSUtil.addCORSHeader(resp);
	    Set<String> supportedSocialAuth = new HashSet<String> ();
	    supportedSocialAuth.add(GOOGLE);
	    Map user = new HashMap();
	    long userId = -1;
	    PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    String socialAuthType = req.getPathInfo().substring(1);
	    if (supportedSocialAuth.contains(socialAuthType)==false)
	    {
	    	 UserUtil.jsonPut(json, ERROR, UNSUPPORTED_SOCIAL_AUTH);
	    	 try
	    	 {
	    		 json.write(writer);
	    		 return;
	    	 }
	    	 catch(Exception e)
	    	 {
	    		 e.printStackTrace();
	    	 }
	    }
	    Map<String,String> userInfo = getInfoFromSocialAuth(socialAuthType);
	    //TODO : Maybe need to add sth here when the user doesn't have a correct account on the socialAuth side
	    String emailAddress =  userInfo.get(EMAIL);
	    List<Entity> userAsList = UserDatabaseDriver.queryUserByProperty(EMAIL,
				emailAddress);
	    if (userAsList==null||userAsList.size()==0)
	    {
	    	//TODO : EMAIL DOESN'T EXIST, CREATE NEW ENTRY FOR THIS USER
	    	
	    }
	    else
	    {
	    	//TODO : EMAIL EXISTS, IF THE USER ALREADY HAVE AN ACCOUNT, DENY THE REQUEST
	    }
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
	

}
