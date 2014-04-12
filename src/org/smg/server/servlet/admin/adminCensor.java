package org.smg.server.servlet.admin;

import static org.smg.server.servlet.admin.adminConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.smg.server.servlet.admin.adminConstants.*;

import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class adminCensor extends HttpServlet{
	
    private String[] validParams = {PASS_CENSOR};
	private Map<Object, Object> deleteInvalid(Map<Object, Object> params,
		      String[] validParams) {
		    Map<Object, Object> returnMap = new HashMap<Object, Object>();
		    for (Map.Entry<Object, Object> entry : params.entrySet()) {
		      if (Arrays.asList(validParams).contains(entry.getKey())) {
		        if (entry.getKey() instanceof String) {
		          returnMap.put(entry.getKey(), entry.getValue());
		        }
		      }
		    }
		    return returnMap;
		  }
	
	 private void put(JSONObject jObj, String key, String value, HttpServletResponse resp) {
		    try {
		      jObj.put(key, value);
		      resp.setContentType("text/plain");
		      jObj.write(resp.getWriter());
		    } catch (Exception e) {
		      return;
		    }
		  }
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		      throws IOException{
		CORSUtil.addCORSHeader(resp);
	    JSONObject jObj = new JSONObject();
	    StringBuffer buffer = new StringBuffer();
	    String line = null;
	    BufferedReader reader = req.getReader();
	    String gameId = req.getParameter(GAME_ID);
	    try
	    {
	      while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	      }
	      Map<Object, Object> parameterMap = deleteInvalid(
	          (Map) JSONUtil.parse(buffer.toString()), validParams);
	      if (parameterMap.containsKey(PASS_CENSOR)==false)
	      {
	    	  put(jObj, ERROR, MISSING_INFO, resp);
	          return;
	      }
	      parameterMap.put(UPDATED, false);
	      long longId = Long.parseLong(gameId);
	      GameDatabaseDriver.updateGame(longId, parameterMap);
	      put (jObj,SUCCESS,ADMIN_FINISHED,resp);
	      return;
	    }
	   catch (Exception e)
	   {
		   put(jObj,ERROR,WRONG_GAME_ID,resp);
		   return;
	   }
		
	}
}
