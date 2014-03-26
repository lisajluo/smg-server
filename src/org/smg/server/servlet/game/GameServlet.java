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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;


@SuppressWarnings("serial")
public class GameServlet extends HttpServlet{
	private Map<Object, Object> deleteInvalid(Map<Object, Object> params, String[] validParams) {
	    Map<Object, Object> returnMap = new HashMap<Object, Object>();
	    for (Map.Entry<Object, Object> entry : params.entrySet()) {
	      if (Arrays.asList(validParams).contains(entry.getKey())) {
	        if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
	          returnMap.put(entry.getKey(), entry.getValue());
	        }
	      }
	    }
	    
	    return returnMap;
	  }
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
	private boolean gameNameDuplicate(long gameId, Map<Object,Object> parameterMap)
	{
		 return DatabaseDriver.checkGameNameDuplicate(gameId,parameterMap);
		
	}
	private boolean signatureRight(Map<Object,Object> parameterMap)
	{
		try
		{
		  long developerId = Long.parseLong((String)parameterMap.get("developerId"));
		  Map developer = DatabaseDriver.getDeveloperMapByKey(developerId);
		  if (parameterMap.get("accessSignature").equals(developer.get("accessSignature")))
			  return true;
		  else
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
		
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
	
	private boolean requiredFieldForUpdate(Map<Object,Object> parameterMap)
	{
		if (parameterMap.get("developerId")==null)
			return false;
		return true;
	}
	private boolean requiredFieldsComplete(Map<Object,Object> parameterMap)
	{
		if (parameterMap.get("developerId")==null)
		{
			
			return false;
		}
		if (parameterMap.get("gameName")==null)
		{
		
			//System.out.println("gameName");
			return false;
		}
		if (parameterMap.get("description")==null)
		{
			
			//System.out.println("description");
			return false;
		}
		if (parameterMap.get("url")==null)
		{
			
			//System.out.println("url");
			return false;
		}
		if (parameterMap.get("width")==null)
		{
			
				//System.out.println("width");
				return false;
		}
		if (parameterMap.get("height")==null)
		{
			
				return false;
		}
		if (parameterMap.get("accessSignature")==null)
		{
		
				return false;
		}
		return true;
		
		
	}
	private boolean gameNameDuplicate(Map<Object,Object> parameterMap)
	{
		return DatabaseDriver.checkGameNameDuplicate(parameterMap);
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
		metainfo.put("postDate", targetEntity.getProperty("postDate"));
		/*JSONObject jobj=new JSONObject();
		jobj.put("icon",targetEntity.getProperty("icon"));
		jobj.put("screenshot", targetEntity.getProperty("screenshots"));
		metainfo.put("pic", jobj);*/
		metainfo.put("icon", targetEntity.getProperty("icon"));
		metainfo.put("screenshot", targetEntity.getProperty("screenshot"));
		metainfo.put("developerId", targetEntity.getProperty("developerId"));
		metainfo.write(resp.getWriter());
		
		
	}
	
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)  throws IOException{
		String pathInfo=req.getPathInfo();
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		String line = null;
		
		String[] validParams = {"screenshot","icon","developerId", "gameName","description","url","width","height","accessSignature"};
		try
		{
	      BufferedReader reader = req.getReader();
		  while ((line = reader.readLine()) != null) {
		       buffer.append(line);
		      }
	    Map<Object, Object> parameterMap = deleteInvalid(
		          (Map) JSONUtil.parse(buffer.toString()), validParams);
	    if (parameterMap.get("screenshot")==null)
	    	System.out.println("screenshot is null");
	    else
	    	System.out.println(parameterMap.get("screenshot"));
		if (parsePathForPost(pathInfo)==false)
		{
			
			put(jObj,"error","URL_PATH_ERROR",resp);
			
        //	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"URL_PATH_ERROR\"}");
        	return;
		}
		
		if (requiredFieldsComplete(parameterMap)==false)
        {
			put(jObj,"error","MISSING_INFO",resp);
        //	resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
        	return;
        }
        if (developerIdExists((String)parameterMap.get("developerId"))==false)
        {
        	
        	put(jObj,"error","DEVELOPERID_DOES_NOT_EXISTS",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
        if (signatureRight(parameterMap)==false)
        {
        	put(jObj,"error","WRONG_ACCESS_SIGNATURE",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_ACCESS_SIGNATURE\"}");
        	return;	
        }
        if (gameNameDuplicate(parameterMap)==true)
        {
        	put(jObj,new String("error"),new String("GAME_EXISTS"),resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"GAME_EXISTS\"}");
        	return;
        }
        else
        {
        	
        	   long gameId=DatabaseDriver.saveGameMetaInfo(parameterMap);
               CORSUtil.addCORSHeader(resp);
               resp.setContentType("text/plain");
              
        	    jObj.put("gameId",gameId);
        	    jObj.write(resp.getWriter());
        
        	
           // resp.getWriter().println("{\"success\" : \"GAME_SUBMISSION_SUCCESS\"}");   
        }
		}
		catch (Exception e)
		{

    		put(jObj,"error","INVALID_JSON_FORMAT",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"INVALID_JSON_FORMAT\"}");
        	return;	
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
		StringBuffer buffer = new StringBuffer();
		String line = null;
		String[] validParams = {"screenshot","icon","developerId", "gameName","description","url","width","height","accessSignature"};
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
		try
		{
		BufferedReader reader = req.getReader();
		  while ((line = reader.readLine()) != null) {
		       buffer.append(line);
		      }
	    Map<Object, Object> parameterMap = deleteInvalid(
		          (Map) JSONUtil.parse(buffer.toString()), validParams);
		if (requiredFieldForUpdate(parameterMap)==false)
		{
			put(jObj,"error","MISSING_INFO",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"MISSING_INFO\"}");
			return;		
		}
		if (developerIdExists((String)parameterMap.get("developerId"))==false)
        {
			put(jObj,"error","DEVELOPERID_DOES_NOT_EXISTS",resp);
        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"DEVELOPERID_DOES_NOT_EXISTS\"}");
        	return;	
        }
		if (signatureRight(parameterMap)==false)
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
		
	    if (gameNameDuplicate(Long.parseLong(gameId),parameterMap)==true)
	    {
	    	    put(jObj,"error","GAME_EXISTS",resp);
	        	//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"GAME_EXISTS\"}");
	        	return;
	    }
		String version = "versionOne";
		Entity targetEntity=DatabaseDriver.getEntity(gameId, version);
		List<String> developerList=(List<String>) targetEntity.getProperty("developerId");
		if (developerList.contains(parameterMap.get("developerId"))==false)
		{
			put(jObj,"error","WRONG_DEVELOPER_ID",resp);
			//resp.sendError(resp.SC_BAD_REQUEST, "{\"error\" : \"WRONG_DEVELOPER_ID\"}");
			return;
		}
		 
		  DatabaseDriver.update(gameId,parameterMap);		  
		  
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