package org.smg.server.servlet.game;

import static org.smg.server.servlet.game.GameConstants.*;
import static org.smg.server.servlet.game.GameUtil.*;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.database.GameDatabaseDriver;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class GameInfoServlet extends HttpServlet {
  private JSONObject  returnAllGameInfo()
  {
	  //TODO:return all the game info
	  
	  return GameDatabaseDriver.getAllGameInfo();
  }
  private JSONObject returnGameInfoByDeveloper(long developerId)
  {
	  //TODO : return gameinfo by developerId
	  
	  return null;
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	  
   
  }
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	  String developerIdStr = req.getParameter(DEVELOPER_ID);
	  CORSUtil.addCORSHeader(resp);
	  JSONObject jObj = new JSONObject();
	  if (developerIdStr==null)
	  {
		  JSONObject queryResult= returnAllGameInfo();
		  put(queryResult,resp);
		  return;
	  }
	  else
	  {
		  long developerId = Long.parseLong(developerIdStr);
		  String accessSignature = req.getParameter(ACCESS_SIGNATURE);
		  try 
		  {
			  boolean verify = DeveloperDatabaseDriver.verifyDeveloperAccess(developerId,accessSignature);
			  if (verify==false)
			  {
				  put(jObj, ERROR, WRONG_ACCESS_SIGNATURE, resp);
				  return;
			  }
			  JSONObject queryResult = returnGameInfoByDeveloper(developerId);
			  put (queryResult,resp);
		  }
		  catch (Exception e)
		  {
			  put(jObj, ERROR, WRONG_DEVELOPER_ID, resp);
		  }
	  }
	  
   
  }
}
