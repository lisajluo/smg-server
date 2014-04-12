package org.smg.server.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import org.smg.server.database.GameDatabaseDriver;

import static org.smg.server.servlet.admin.adminConstants.*;
import static org.smg.server.servlet.game.GameUtil.put;

public class adminInfoServlet extends HttpServlet{
	private List<JSONObject> getGameList(boolean censored)
	{
		List<JSONObject> result = GameDatabaseDriver.getGameInfoAsJSON(censored);
		return result;
		
	}
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
	    	
	    	put(json, ERROR, NO_RECORD, resp);
	    	return;
	    }
	    
	    
	}

}
