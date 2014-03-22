package org.smg.server.servlet.developer.game;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.smg.server.database.GameManager;
import org.smg.util.CORSUtil;


public class gameServlet extends HttpServlet{
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
		return true;
		
		
	}
	private boolean gameNameDuplicate(HttpServletRequest req)
	{
		return GameManager.checkGameNameDuplicate(req);
	}
	
	private boolean gameNameDuplicate(String gameName,HttpServletRequest req)
	{
		return GameManager.checkGameNameDuplicate(gameName,req);
	}
	
	private void returnMetaInfo(String gameName,String versionNum,HttpServletResponse resp) throws IOException
	{
		JSONObject metainfo=new JSONObject();
		resp.setContentType("text/plain");
		Entity targetEntity=GameManager.getEntity(gameName, versionNum);
		metainfo.put("version", targetEntity.getProperty("version"));
		metainfo.put("gameName", targetEntity.getProperty("gameName"));
		metainfo.put("url", targetEntity.getProperty("url"));
		metainfo.put("description", targetEntity.getProperty("description"));
		metainfo.put("width", targetEntity.getProperty("width"));
		metainfo.put("height", targetEntity.getProperty("height"));		
		/*
		resp.getWriter().println("version :"+targetEntity.getProperty("version"));
		resp.getWriter().println("gameName :"+targetEntity.getProperty("gameName"));
		resp.getWriter().println("url :"+targetEntity.getProperty("url"));
		resp.getWriter().println("description :"+targetEntity.getProperty("description"));
		resp.getWriter().println("width :"+targetEntity.getProperty("width"));
		resp.getWriter().println("height :"+targetEntity.getProperty("height"));*/
		//resp.getWriter().println("icon: " + targetEntity.getProperty("icon"));
		JSONObject jobj=new JSONObject();
		jobj.put("icon",targetEntity.getProperty("icon"));
		jobj.put("screenshots", targetEntity.getProperty("screenshots"));
		//resp.getWriter().println("screenshot: " + targetEntity.getProperty("screenshot"));
		//resp.getWriter().println("pic :"+jobj);
		metainfo.put("pic", jobj);
		metainfo.put("developerId", targetEntity.getProperty("developerId"));
		resp.getWriter().println(metainfo);
		//resp.getWriter().println("developerId :"+targetEntity.getProperty("developerId"));
		
	}
	
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        if (requiredFieldsComplete(req)==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
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
        	GameManager.saveGameMetaInfo(req);
        	CORSUtil.addCORSHeader(resp);
        	resp.setContentType("text/plain");
            resp.getWriter().println("{\"success\" : \"GAME_SUBMISSION_SUCCESS\"}");   
        }
    }
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String targetId=req.getPathInfo().substring(1);
		System.out.println(targetId);
		if (gameNameDuplicate(targetId,req)==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;
			
		}
		else
		{
			CORSUtil.addCORSHeader(resp);
			returnMetaInfo(targetId,"versionOne",resp);
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
		String pathInfo= req.getPathInfo().substring(1);
		String[] deleteInfo= pathInfo.split("&");
		String developerId = deleteInfo[0];
		String gameName = deleteInfo[1];
		if (gameNameDuplicate(gameName,req)==false)
		{
			//CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;
		}
		Entity targetEntity=GameManager.getEntity(gameName, "versionOne");
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(developerId)==false)
		{
			//CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		
		GameManager.delete(gameName,"versionOne");
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
		System.out.println(gameId);
		if (gameNameDuplicate(gameId,req)==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_GAME_ID\"}");
			return;			
		}
		String version = "versionOne";
		Entity targetEntity=GameManager.getEntity(gameId, version);
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(req.getParameter("developerId"))==false)
		{
			CORSUtil.addCORSHeader(resp);
			resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		try
		{
		  GameManager.update(gameId,req);
		  CORSUtil.addCORSHeader(resp);
		  resp.setContentType("text/plain");
	      resp.getWriter().println("{\"success\" : \"UPDATED_GAME\"}");  
		}
		catch (Exception e)
		{
			
		}
	}

}