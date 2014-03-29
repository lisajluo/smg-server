package org.smg.server.servlet.game;

import static org.smg.server.servlet.game.GameConstants.*;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriver;
import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.models.Player;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class GameUtil {
	public static  boolean signatureRight(HttpServletRequest req)
	{
		try
		{
		  long developerId = Long.parseLong(req.getParameter(DEVELOPER_ID));
		  Map developer = DeveloperDatabaseDriver.getDeveloperMap(developerId);
		  if (req.getParameter(ACCESS_SIGNATURE).equals(developer.get(ACCESS_SIGNATURE)))
			  return true;
		  else
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
		
	}
	public static boolean signatureRight(Map<Object, Object> parameterMap) {
		try {
			long developerId = Long.parseLong((String) parameterMap
					.get(DEVELOPER_ID));
			Map developer = DeveloperDatabaseDriver
					.getDeveloperMap(developerId);
			if (parameterMap.get(ACCESS_SIGNATURE).equals(
					developer.get(ACCESS_SIGNATURE)))

				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}

	}
	public static boolean signatureRightForPlayer(Map<Object, Object> parameterMap) {
		try {
			long playerId = Long.parseLong((String) parameterMap
					.get(PLAYER_ID));
			Player player = DatabaseDriver.getPlayerById(playerId);
			if (parameterMap.get(ACCESS_SIGNATURE).equals(
					player.getProperty(Player.PlayerProperty.ACCESSSIGNATURE)))

				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}

	}
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
	public static void put(List<JSONObject> jObj, HttpServletResponse resp)
	{
		try
		{
		      JSONArray jsonArray = new JSONArray();
		      for (int i=0;i<jObj.size();i++)
		      {
		    	  jsonArray.put(jObj.get(i));
		      }			  
              resp.setContentType("text/plain");
              System.out.println(jsonArray);
              jsonArray.write(resp.getWriter());
		}
		catch (Exception e)
		{
			return;
		}
	}
}
