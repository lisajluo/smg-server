package org.smg.server.servlet.game;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class GameUtil {
	public  static void put (JSONObject jObj,String key,String value,HttpServletResponse resp)
	{
		try
		{
              jObj.put(key, value);
              resp.setContentType("text/plain");
              jObj.write(resp.getWriter());
		}
		catch (Exception e)
		{
			return;
		}
	}
	public static void put(JSONObject jObj, HttpServletResponse resp)
	{
		try
		{
              resp.setContentType("text/plain");
              jObj.write(resp.getWriter());
		}
		catch (Exception e)
		{
			return;
		}
	}
}
