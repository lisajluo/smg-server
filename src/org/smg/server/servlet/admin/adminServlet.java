package org.smg.server.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.smg.server.servlet.admin.adminConstants.*;

import org.smg.server.servlet.user.UserUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class adminServlet extends HttpServlet{
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CORSUtil.addCORSHeader(resp);
		PrintWriter writer = resp.getWriter();
	    JSONObject json = new JSONObject();
	    String adminId = req.getParameter(ADMIN_ID);
	    String passWord = req.getParameter(PASS_WORD);
	    try
	    {
	    	if (adminId.equals(UNIQUE_ADMIN)==false||passWord.equals(UNIQUE_PASSWORD)==false)
	    		throw new Exception();
	    	String adminToken = AccessSignatureUtil.getHashedPassword(PASS_WORD);
	    	UserUtil.jsonPut(json,SUCCESS,adminToken);
	    }
	    catch (Exception e)
	    {
	    	UserUtil.jsonPut(json, ERROR, WRONG_ADMIN_INFO);
	    }
	    try 
	    {
	    	json.write(writer);
	    }
	    catch (Exception e)
	    {
	    	System.out.println(e.getMessage());
	    }
	    
		
	}
	

}
