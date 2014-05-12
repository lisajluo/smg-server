package org.smg.server.servlet.user;



import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.smg.server.servlet.game.GameConstants;


import static org.smg.server.servlet.user.UserConstants.*;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserHelper {
	/**
	   * Send error message to the client with human readable details, and json
	   * string received from client. Check {@link GameConstants} to find all
	   * the error type and msg.
	   * 
	   * @param resp
	   * @param returnValue
	   * @param errorMSG
	   * @param details
	   * @param json
	   */
	
	public static void sendErrorMessageForJson(HttpServletResponse resp,
		      JSONObject returnValue, String errorMSG, String details, String json) {
		    try {
		      returnValue.put(ERROR, errorMSG);
		      returnValue.put(DETAILS, details);
		      returnValue.put(JSON_RECEIVED, new JSONObject(json));
		      returnValue.write(resp.getWriter());
		    } catch (JSONException | IOException e) {
		      e.printStackTrace();
		    }
		  }
	
	/**
	   * Send error message to the client with human readable details, and the
	   * absolute URL that the client is making request to. Check {@link GameConstants} 
	   * to find all the error type and msg.
	   * 
	   * @param resp
	   * @param returnValue
	   * @param errorMSG
	   * @param details
	   * @param json
	   */
	public static void sendErrorMessageForUrl(HttpServletResponse resp,
		      JSONObject returnValue, String errorMSG, String details, String json) {
		    try {
		      returnValue.put(ERROR, errorMSG);
		      returnValue.put(DETAILS, details);
		      returnValue.put(URL, json);
		      returnValue.write(resp.getWriter());
		    } catch (JSONException | IOException e) {
		      e.printStackTrace();
		    }
		  }

}
