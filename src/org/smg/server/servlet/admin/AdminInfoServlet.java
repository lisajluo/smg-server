package org.smg.server.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.servlet.game.GameHelper;
import org.smg.server.servlet.game.GameUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import org.smg.server.database.GameDatabaseDriver;

import static org.smg.server.servlet.admin.AdminConstants.*;
import static org.smg.server.servlet.game.GameUtil.put;
import static org.smg.server.servlet.user.UserConstants.WRONG_EMAIL;

public class AdminInfoServlet extends HttpServlet{
	/**
	 * Return the game List indicated by the boolean value censored
	 * @param censored Return all the authorized game when censored ==true,otherwise return all the unauthorized games
	 * @return
	 */
	private List<JSONObject> getGameList(boolean censored)
	{
		List<JSONObject> result = GameDatabaseDriver.getGameInfoAsJSON(censored);
		return result;
		
	}
	/**
	 * doGet is called when the client is asking for all the games(both authorized and unauthorized)
	 * The response will be a JSON Object with two keys
	 * The value of PASSED_LIST is all the games that have been authorized
	 * The value of BLOCKED_LIST is all the games that haven't been authorized
	 * 
	 * A successful response:
	 *
	 * { “PASSED_LIST” : 
	 * [{“developerId”: “120480234”, 
	 * “accessSignature”:“secretAccessSignature”, 
	 * “gameName”: “Cheat”, 
	 *  "description":"Game description.", 
	 *   “url”: “http://www.cheatgame.com”}, 
	 * {....}], 
	 * “BLOCKED_LIST” : [{...},{...}...{}] 
	 * }
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CORSUtil.addCORSHeader(resp);
		PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    List<JSONObject> passedList = new ArrayList<JSONObject> ();
	    List<JSONObject> unpassedList = new ArrayList<JSONObject> ();
		try {
			passedList = getGameList(true);
			unpassedList = getGameList(false);
			if (passedList != null) {
				json.put(PASSED_LIST, new JSONArray(passedList));
			}
			if (unpassedList != null) {
				json.put(BLOCKED_LIST, new JSONArray(unpassedList));
			}
			put(json,resp);
			return;
		}
	    catch (Exception e)
	    {
	    	JSONObject jObj = new JSONObject();
	    	String urlStr = GameUtil.getFullURL(req);
			String details = "There is no record of games in the dataStore";
			GameHelper.sendErrorMessageForUrl(resp, jObj, NO_RECORD,
					details, urlStr);
	    	return;
	    }
	    
	    
	}

}
