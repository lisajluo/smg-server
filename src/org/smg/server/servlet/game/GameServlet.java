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
		long developerId = Long.parseLong(req.getParameter("developerId"));
		Map developer = DatabaseDriver.getDeveloperMapByKey(developerId);
		if (req.getParameter("accessSignature").equals(developer.get("accessSignature")))
			return true;
		else
			return false;
		
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
			return false;
		if (req.getParameter("gameName")==null)
			return false;
		if (req.getParameter("description")==null)
			return false;
		if (req.getParameter("url")==null)
			return false;
		if (req.getParameter("width")==null)
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
        if (requiredFieldsComplete(req)==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
        	return;
        }
        if (developerIdExists(req.getParameter("developerId"))==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
        if (signatureRight(req)==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
        	return;	
        }
        if (gameNameDuplicate(req)==true)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"GAME_EXISTS\"}");
        	return;
        }
        else
        {
        	
        	try
        	{
        	   long gameId=DatabaseDriver.saveGameMetaInfo(req);
               CORSUtil.addCORSHeader(resp);
               resp.setContentType("text/plain");
               JSONObject jObj = new JSONObject();
        	    jObj.put("gameId",gameId);
        	    jObj.write(resp.getWriter());
        	}
        	catch (Exception e)
        	{
        		CORSUtil.addCORSHeader(resp);
            	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"INVALID_JSON_FORMAT\"}");
            	return;	
        	}
        	
           // resp.getWriter().println("{\"success\" : \"GAME_SUBMISSION_SUCCESS\"}");   
        }
    }
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String targetId=req.getPathInfo().substring(1);
		//System.out.println(targetId);
		if (gameIdExist(targetId)==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;
			
		}
		else
		{
			CORSUtil.addCORSHeader(resp);
			try {
				returnMetaInfo(targetId,"versionOne",resp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"PARSE_ERROR\"}");
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
		
		CORSUtil.addCORSHeader(resp);
		String gameId= req.getPathInfo().substring(1);
		String developerId= req.getParameter("developerId");
		System.out.println("gamdId: "+gameId);
		System.out.println("developerId: "+developerId);
		System.out.println("sig: "+req.getParameter("accessSignature"));
		if (developerIdExists(developerId)==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
		if (signatureRight(req)==false)
		{

        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
        	return;	
		}
		if (gameIdExist(gameId)==false)
		{
			//CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;
		}
		Entity targetEntity=DatabaseDriver.getEntity(gameId, "versionOne");
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(developerId)==false)
		{
			//CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		
		DatabaseDriver.delete(gameId,"versionOne");
		//CORSUtil.addCORSHeader(resp);
		resp.setContentType("text/plain");
        resp.getWriter().println("{\"success\" : \"DELETED_GAME\"}");  
	}
	
	@Override 
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String gameId = req.getPathInfo().substring(1);
		if (requiredFieldForUpdate(req)==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
			return;		
		}
		if (developerIdExists(req.getParameter("developerId"))==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
		if (signatureRight(req)==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
			return;		
		}
		if (gameIdExist(gameId)==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;			
		}
		
	    if (gameNameDuplicate(Long.parseLong(gameId),req)==true)
	    {
	        	CORSUtil.addCORSHeader(resp);
	        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"GAME_EXISTS\"}");
	        	return;
	    }
		String version = "versionOne";
		Entity targetEntity=DatabaseDriver.getEntity(gameId, version);
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(req.getParameter("developerId"))==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		try
		{
		  CORSUtil.addCORSHeader(resp);
		  DatabaseDriver.update(gameId,req);		  
		  resp.setContentType("text/plain");
	      resp.getWriter().println("{\"success\" : \"UPDATED_GAME\"}");  
		}
		catch (Exception e)
		{
			CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"INVALID_JSON_FORMAT\"}");
        	return;	
		}
	}

}