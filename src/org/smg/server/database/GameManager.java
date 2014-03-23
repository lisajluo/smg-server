package org.smg.server.database;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;






import org.smg.server.util.JSONUtil;

import com.fasterxml.jackson.core.JsonParseException;





//To-do-List: parse the json object
public class GameManager {
	
	static public long  saveGameMetaInfo(HttpServletRequest req) throws IOException
	{
		
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			//JSONParser parser=new JSONParser();
			Key versionKey=KeyFactory.createKey("Version", "versionOne");
//			Key idKey=KeyFactory.createKey(versionKey, "idKey","currentIdKey");
//			String gameId=new String();
			//System.out.println(req.getParameter("developerId"));
//			try
//			{
//				Entity target=datastore.get(idKey);
//				gameId=(String)target.getProperty("iD");
//				int numericValue=Integer.parseInt(gameId);
//				gameId=String.valueOf(numericValue+1);
//				target.setProperty("iD", gameId);
//				datastore.put(target);
				
//			}
//			catch (Exception e)
//			{
//				gameId=new String("0");
//				Entity target=new Entity("idKey","currentIdKey",versionKey);
//				target.setProperty("iD", gameId);
//				datastore.put(target);
//				System.out.println("current Id: "+gameId);
//				return gameId;
//			}
//			finally
//			{
			Date date=new Date();
			Entity game=new Entity("gameMetaInfo",versionKey);
		//	long gameId = game.getKey().getId();
			game.setProperty("version","versionOne");
			game.setProperty("postDate", date);
			game.setProperty("gameName", req.getParameter("gameName"));
			game.setProperty("description", req.getParameter("description"));
			game.setProperty("url", req.getParameter("url"));
			game.setProperty("width", req.getParameter("width"));
			game.setProperty("height", req.getParameter("height"));
			String picInfo=req.getParameter("pic");
			//System.out.println(picInfo);
			//Object obj=parser.parse(picInfo);
			Map<String, Object> jObj = JSONUtil.parse(picInfo);
			//JSONObject jObj=(JSONObject) obj;
		    game.setProperty("icon",jObj.get("icon"));
		    //JSONArray array=(JSONArray)jObj.get("screenshots");
		    ArrayList<String> screenshot=(ArrayList<String>) (jObj.get("screenshots"));
		  //  for (int i=0;i<array.size();i++)
		   // 	screenshot.add((String)(array.get(i)));
		    System.out.println(screenshot);
		    game.setProperty("screenshots", screenshot);
			List<String> developerList=new ArrayList<String> ();
			developerList.add(req.getParameter("developerId"));
			game.setProperty("developerId", developerList);
		//	game.setProperty("gameId", gameId);
			datastore.put(game);
			long gameId = game.getKey().getId();
			Key queryKey = KeyFactory.createKey(versionKey,"gameMetaInfo",gameId);
			
			return gameId;
//			}
		
		
	}
	static public Entity getEntity(String gameId,String versionNum)
	{
		try 
		{
			Key versionKey=KeyFactory.createKey("Version", versionNum);
			long ID = Long.parseLong(gameId);
			Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
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
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String versionNum="versionOne";
			Key versionKey=KeyFactory.createKey("Version", versionNum);
			System.out.println(req.getParameter("gameName"));
			Filter nameFilter =new FilterPredicate("gameName",FilterOperator.EQUAL,req.getParameter("gameName"));
			Query q = new Query("gameMetaInfo").setFilter(nameFilter);
			PreparedQuery pq = datastore.prepare(q);
			if (pq.countEntities()>0)
				return true;
			else
				return false;
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
	static public boolean checkIdExists(String gameId)
	{
		try
		{
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String versionNum="versionOne";
			Key versionKey=KeyFactory.createKey("Version", versionNum);
			long ID = Long.parseLong(gameId);
			//System.out.println(ID);
			Key idKey= KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
			try 
			{
				datastore.get(idKey);
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
	
	static public void delete(String gameId,String versionNum)
	{
		Key versionKey=KeyFactory.createKey("Version", versionNum);
		long ID = Long.parseLong(gameId);
		Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.delete(gameKey);
    }
	static public void update(String gameId,HttpServletRequest req) throws EntityNotFoundException, IOException
	{
		//JSONParser parser=new JSONParser();
		Key versionKey=KeyFactory.createKey("Version", "versionOne");
		long ID = Long.parseLong(gameId);
		Key gameKey=KeyFactory.createKey(versionKey, "gameMetaInfo", ID);
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
			Map<String, Object> jObj=JSONUtil.parse(req.getParameter("pic"));
			//Object obj=parser.parse(req.getParameter("pic"));
		//	JSONObject jObj=(JSONObject) obj;
			if (jObj.get("icon")!=null)
		      target.setProperty("icon",jObj.get("icon"));
			if (jObj.get("screenshots")!=null)
			{
				//JSONArray array=(JSONArray)jObj.get("screenshots");
			    //ArrayList<String> screenshot=new ArrayList<String> ();
				ArrayList<String> screenshot =(ArrayList<String>) (jObj.get("screenshots"));
			   //for (int i=0;i<array.size();i++)
			   // 	screenshot.add((String)(array.get(i)));
			    target.setProperty("screenshots",screenshot);
			    
			}
				
		}
			
		datastore.put(target);
		
    }

}