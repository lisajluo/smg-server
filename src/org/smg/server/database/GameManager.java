package org.smg.server.database;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.*;
import org.json.simple.parser.*;






//To-do-List: parse the json object
public class GameManager {
	
	static public void  saveGameMetaInfo(HttpServletRequest req)
	{
		try 
		{
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			JSONParser parser=new JSONParser();
			Key versionKey=KeyFactory.createKey("Version", "versionOne");
			Key idKey=KeyFactory.createKey(versionKey, "idKey","currentIdKey");
			String gameId=new String();
			try
			{
				Entity target=datastore.get(idKey);
				gameId=(String)target.getProperty("iD");
				int numericValue=Integer.parseInt(gameId);
				gameId=String.valueOf(numericValue+1);
			}
			catch (Exception e)
			{
				gameId=new String("0");
			}
			Date date=new Date();
			Entity game=new Entity("gameMetaInfo",gameId,versionKey);
			game.setProperty("gameId", gameId);
			game.setProperty("version","versionOne");
			game.setProperty("postDate", date);
			game.setProperty("gameName", req.getParameter("gameName"));
			game.setProperty("description", req.getParameter("description"));
			game.setProperty("url", req.getParameter("url"));
			game.setProperty("width", req.getParameter("width"));
			game.setProperty("height", req.getParameter("height"));
			String picInfo=req.getParameter("pic");
			Object obj=parser.parse(picInfo);
			JSONObject jObj=(JSONObject) obj;
		    game.setProperty("icon",jObj.get("icon"));
		    JSONArray array=(JSONArray)jObj.get("screenshots");
		    ArrayList<String> screenshot=new ArrayList<String> ();
		    for (int i=0;i<array.size();i++)
		    	screenshot.add((String)(array.get(i)));
		    System.out.println(screenshot);
		    game.setProperty("screenshots", screenshot);
			List<String> developerList=new ArrayList<String> ();
			developerList.add(req.getParameter("developerId"));
			game.setProperty("developerId", developerList);
			System.out.println(req.getParameter("developerId"));
			datastore.put(game);
		}
		catch (Exception e)
		{
			return ;
		}
	}
	static public Entity getEntity(String gameId,String versionNum)
	{
		try 
		{
			Key versionKey=KeyFactory.createKey("Version", versionNum);
			Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo",gameId);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			return datastore.get(gameKey);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	static public boolean checkGameNameDuplicate(HttpServletRequest req)
	{
		try
		{
			String versionNum="versionOne";
			Key versionKey=KeyFactory.createKey("Version", versionNum);
			System.out.println(req.getParameter("gameName"));
			Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo",req.getParameter("gameName"));
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			try
			{
				datastore.get(gameKey);
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	    
	}
	static public boolean checkGameNameDuplicate(String gameName,HttpServletRequest req)
	{
		try
		{
			String versionNum="versionOne";
			Key versionKey=KeyFactory.createKey("Version", versionNum);
			Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo",gameName);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			try
			{
				datastore.get(gameKey);
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	    
	}
	static public void delete(String gameName,String versionNum)
	{
		Key versionKey=KeyFactory.createKey("Version", versionNum);
		Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo",gameName);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.delete(gameKey);
    }
	static public void update(String gameName,HttpServletRequest req) throws EntityNotFoundException, ParseException
	{
		JSONParser parser=new JSONParser();
		Key versionKey=KeyFactory.createKey("Version", "versionOne");
		Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo",gameName);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity target = datastore.get(gameKey);
		if (req.getParameter("description")!=null)
			target.setProperty("description", req.getParameter("description"));
		if (req.getParameter("url")!=null)
			target.setProperty("url", req.getParameter("url"));
		if (req.getParameter("width")!=null)
			target.setProperty("width", req.getParameter("width"));
		if (req.getParameter("height")!=null)
			target.setProperty("height", req.getParameter("height"));
		if (req.getParameter("pic")!=null)
		{
			Object obj=parser.parse(req.getParameter("pic"));
			JSONObject jObj=(JSONObject) obj;
			if (jObj.get("icon")!=null)
		      target.setProperty("icon",jObj.get("icon"));
			if (jObj.get("screenshots")!=null)
			{
				JSONArray array=(JSONArray)jObj.get("screenshots");
			    ArrayList<String> screenshot=new ArrayList<String> ();
			    for (int i=0;i<array.size();i++)
			    	screenshot.add((String)(array.get(i)));
			    target.setProperty("screenshots",screenshot);
			    
			}
				
		}
			
		datastore.put(target);
		
    }

}