package org.smg.server.servlet.game;
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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.GameManager;
import org.smg.util.CORSUtil;


public class GameServelet extends HttpServlet{
	private boolean requiredFieldsComplete(HttpServletRequest req)
	{
		if (req.getParameter("version")==null)
			return false; 
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
	private boolean gameNameUnique(HttpServletRequest req)
	{
		return GameManager.checkGameNameDuplicate(req);
	}
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        if (requiredFieldsComplete(req)==false)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "MISSING_INFO");
        	return;
        }
        if (gameNameUnique(req)==true)
        {
        	CORSUtil.addCORSHeader(resp);
        	resp.sendError(resp.SC_BAD_REQUEST, "GAME_EXISTS");
        	return;
        }
        else
        {
        	GameManager.saveGameMetaInfo(req);
        	CORSUtil.addCORSHeader(resp);
        	resp.setContentType("text/plain");
            resp.getWriter().println("GAME_SUBMISSION_SUCCESS");   
        }
    }

}
