package org.smg.server.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.smg.server.servlet.admin.adminConstants.*;
import static org.smg.server.servlet.user.UserConstants.ACCESS_SIGNATURE;
import static org.smg.server.servlet.user.UserConstants.EMAIL;
import static org.smg.server.servlet.user.UserConstants.ERROR;
import static org.smg.server.servlet.user.UserConstants.PASSWORD;
import static org.smg.server.servlet.user.UserConstants.SOCIAL_AUTH;
import static org.smg.server.servlet.user.UserConstants.SOCIAL_AUTH_ACCOUNT;
import static org.smg.server.servlet.user.UserConstants.USER_ID;
import static org.smg.server.servlet.user.UserConstants.WRONG_EMAIL;
import static org.smg.server.servlet.user.UserConstants.WRONG_PASSWORD;
import static org.smg.server.servlet.user.UserConstants.WRONG_USER_ID;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.user.UserUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class adminServlet extends HttpServlet{
	
	
	
public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CORSUtil.addCORSHeader(resp);
		PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    Map user = new HashMap();
	    String email = req.getParameter(ADMIN_ID);
	    String passWord = AccessSignatureUtil.getHashedPassword(req.getParameter(PASS_WORD));
	    long userId;
	    try
	    {
	    	List<Entity> userAsList = UserDatabaseDriver.queryUserByProperty(EMAIL,	email);
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
			user = UserDatabaseDriver.getUserMap(userId);
			String correctPassword = (String)user.get(PASSWORD);
			if (correctPassword.equals(passWord)==true) 
			{
				if (user.get(ADMIN)==null||(boolean)user.get(ADMIN)==false)
				{
					if (user.get(EMAIL).equals(SUPER_ADMIN)==false)
					{
					  throw new Exception();
					}
				}
					user.put(ACCESS_SIGNATURE,
							AccessSignatureUtil.generate(userId));
					user.put(PASSWORD, req.getParameter(PASSWORD));
					UserDatabaseDriver.updateUser(userId, user);
					user.remove(PASSWORD);
					user.put(USER_ID, userId);
					
					json = new JSONObject(user);
		    
			}
	    }
	    catch (Exception e)
	    {
	    	UserUtil.jsonPut(json, ERROR, WRONG_ADMIN_INFO);
	    }
	    try 
	    {
	    	json.write(writer);
	    }
	    catch (Exception e)
	    {
	    	System.out.println(e.getMessage());
	    }
	    
		
	}
	

}
