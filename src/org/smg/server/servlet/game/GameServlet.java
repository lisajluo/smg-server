package org.smg.server.servlet.game;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriver;
import org.smg.server.util.CORSUtil;


@SuppressWarnings("serial")
public class GameServlet extends HttpServlet{
	private void put (JSONObject jObj,String key,String value,HttpServletResponse resp)
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
	private boolean parsePathForPost(String pathInfo)
	{
		if (pathInfo==null)
			return true;
		if (pathInfo.length()>0)
		{
			if (pathInfo.length()==1)
			{
				if (pathInfo.charAt(0)!='/')
				{
					return false;
					
				}
				else
					return true;
			}
			return false;
			
		}
		return true;
	}
	private boolean developerIdExists(String idAsStr)
	{
		try 
		{
		long developerId = Long.parseLong(idAsStr);
		Map developer = DatabaseDriver.getDeveloperMapByKey(developerId);
		if (developer==null)
			return false;
		else
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	private boolean gameNameDuplicate(long gameId, HttpServletRequest req)
	{
		 return DatabaseDriver.checkGameNameDuplicate(gameId,req);
		
	}
	private boolean signatureRight(HttpServletRequest req)
	{
		try
		{
		  long developerId = Long.parseLong(req.getParameter("developerId"));
		  Map developer = DatabaseDriver.getDeveloperMapByKey(developerId);
		  if (req.getParameter("accessSignature").equals(developer.get("accessSignature")))
			  return true;
		  else
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
		
	}
	
	private boolean requiredFieldForUpdate(HttpServletRequest req)
	{
		if (req.getParameter("developerId")==null)
			return false;
		return true;
	}
	private boolean requiredFieldsComplete(HttpServletRequest req)
	{
		if (req.getParameter("developerId")==null)
		{
			return false;
		}
		if (req.getParameter("gameName")==null)
		{
			//System.out.println("gameName");
			return false;
		}
		if (req.getParameter("description")==null)
		{
			//System.out.println("description");
			return false;
		}
		if (req.getParameter("url")==null)
		{
			//System.out.println("url");
			return false;
		}
		if (req.getParameter("width")==null)
				//System.out.println("width");
				return false;
		if (req.getParameter("height")==null)
			
				return false;
			
		if (req.getParameter("accessSignature")==null)
				return false;
		return true;
		
		
	}
	private boolean gameNameDuplicate(HttpServletRequest req)
	{
		return DatabaseDriver.checkGameNameDuplicate(req);
	}
	
	private boolean gameNameDuplicate(String gameName,HttpServletRequest req)
	{
		return DatabaseDriver.checkGameNameDuplicate(gameName,req);
	}
	private boolean gameIdExist(String GameId)
	{
		return DatabaseDriver.checkIdExists(GameId);
	}
	private void returnMetaInfo(String gameName,String versionNum,HttpServletResponse resp) throws IOException, JSONException
	{
		JSONObject metainfo=new JSONObject();
		resp.setContentType("text/plain");
		Entity targetEntity=DatabaseDriver.getEntity(gameName, versionNum);
		metainfo.put("version", targetEntity.getProperty("version"));
		metainfo.put("gameName", targetEntity.getProperty("gameName"));
		metainfo.put("url", targetEntity.getProperty("url"));
		metainfo.put("description", targetEntity.getProperty("description"));
		metainfo.put("width", targetEntity.getProperty("width"));
		metainfo.put("height", targetEntity.getProperty("height"));		
		JSONObject jobj=new JSONObject();
		jobj.put("icon",targetEntity.getProperty("icon"));
		jobj.put("screenshots", targetEntity.getProperty("screenshots"));
		metainfo.put("pic", jobj);
		metainfo.put("developerId", targetEntity.getProperty("developerId"));
		metainfo.write(resp.getWriter());
		
		
	}
	
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)  throws IOException{
		String pathInfo=req.getPathInfo();
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		if (parsePathForPost(pathInfo)==false)
		{
			
			put(jObj,"error","URL_PATH_ERROR",resp);
			
        //	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"URL_PATH_ERROR\"}");
        	return;
		}
		
		if (requiredFieldsComplete(req)==false)
        {
			put(jObj,"error","MISSING_INFO",resp);
        //	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
        	return;
        }
        if (developerIdExists(req.getParameter("developerId"))==false)
        {
        	//System.out.println(req.getParameter("developerId"));
        	put(jObj,"error","DEVELOPERID_DOES_NOT_EXISTS",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
        if (signatureRight(req)==false)
        {
        	put(jObj,"error","WRONG_ACCESS_SIGNATURE",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
        	return;	
        }
        if (gameNameDuplicate(req)==true)
        {
        	put(jObj,new String("error"),new String("GAME_EXISTS"),resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"GAME_EXISTS\"}");
        	return;
        }
        else
        {
        	
        	try
        	{
        	   long gameId=DatabaseDriver.saveGameMetaInfo(req);
               CORSUtil.addCORSHeader(resp);
               resp.setContentType("text/plain");
              
        	    jObj.put("gameId",gameId);
        	    jObj.write(resp.getWriter());
        	}
        	catch (Exception e)
        	{
        		put(jObj,"error","INVALID_JSON_FORMAT",resp);
            	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"INVALID_JSON_FORMAT\"}");
            	return;	
        	}
        	
           // resp.getWriter().println("{\"success\" : \"GAME_SUBMISSION_SUCCESS\"}");   
        }
    }
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String targetId=null;
		JSONObject jObj = new JSONObject();
		CORSUtil.addCORSHeader(resp);
		try
		{
			 targetId=req.getPathInfo().substring(1);
			Long.parseLong(targetId);
		}
		catch (Exception e)
		{
			put(jObj,"error","URL_PATH_ERROR",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"URL_PATH_ERROR\"}");
			return;
		}
		//S (ystem.out.println(targetId);
		if (gameIdExist(targetId)==false)
		{
			put(jObj,"error","WRONG_GAME_ID",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;
			
		}
		else
		{
			
			try {
				returnMetaInfo(targetId,"versionOne",resp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				put(jObj,"error","PARSE_ERROR",resp);
				//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"PARSE_ERROR\"}");
			}
		}
		
	}
	@Override
	public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		CORSUtil.addCORSHeader(resp);
	}
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String targetId=null;
		JSONObject jObj = new JSONObject();
		CORSUtil.addCORSHeader(resp);
		try
		{
			targetId=req.getPathInfo().substring(1);
			Long.parseLong(targetId);
		}
		catch (Exception e)
		{
			put(jObj,"error","URL_PATH_ERROR",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"URL_PATH_ERROR\"}");
			return;
		}
		
		String gameId= req.getPathInfo().substring(1);
		String developerId= req.getParameter("developerId");
		if (developerIdExists(developerId)==false)
        {
			put(jObj,"error","DEVELOPERID_DOES_NOT_EXISTS",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
		if (signatureRight(req)==false)
		{

			put(jObj,"error","WRONG_ACCESS_SIGNATURE",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
        	return;	
		}
		if (gameIdExist(gameId)==false)
		{
			put(jObj,"error","WRONG_GAME_ID",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;
		}
		Entity targetEntity=DatabaseDriver.getEntity(gameId, "versionOne");
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(developerId)==false)
		{
			put(jObj,"error","WRONG_DEVELOPER_ID",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		
		DatabaseDriver.delete(gameId,"versionOne");
		
		resp.setContentType("text/plain");
        resp.getWriter().println("{\"success\" : \"DELETED_GAME\"}");  
	}
	
	@Override 
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		JSONObject jObj = new JSONObject();
		CORSUtil.addCORSHeader(resp);
		String gameId=null;
		try
		{
			gameId = req.getPathInfo().substring(1);
			Long.parseLong(gameId);
		}
		catch (Exception e)
		{
			put(jObj,"error","URL_PATH_ERROR",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"URL_PATH_ERROR\"}");
			return;
		}
		if (requiredFieldForUpdate(req)==false)
		{
			put(jObj,"error","MISSING_INFO",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
			return;		
		}
		if (developerIdExists(req.getParameter("developerId"))==false)
        {
			put(jObj,"error","DEVELOPERID_DOES_NOT_EXISTS",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
		if (signatureRight(req)==false)
		{
			put(jObj,"error","WRONG_ACCESS_SIGNATURE",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
			return;		
		}
		if (gameIdExist(gameId)==false)
		{
			put(jObj,"error","WRONG_GAME_ID",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;			
		}
		
	    if (gameNameDuplicate(Long.parseLong(gameId),req)==true)
	    {
	    	    put(jObj,"error","GAME_EXISTS",resp);
	        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"GAME_EXISTS\"}");
	        	return;
	    }
		String version = "versionOne";
		Entity targetEntity=DatabaseDriver.getEntity(gameId, version);
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(req.getParameter("developerId"))==false)
		{
			put(jObj,"error","WRONG_DEVELOPER_ID",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		try
		{
		 
		  DatabaseDriver.update(gameId,req);		  
		  
		  put(jObj,"success","GAME_UPDATED",resp);  
		}
		catch (Exception e)
		{
			put(jObj,"error","INVALID_JSON_FORMAT",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"INVALID_JSON_FORMAT\"}");
        	return;	
		}
	}

}