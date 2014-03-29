package org.smg.server.servlet.developer;

import static org.smg.server.servlet.developer.DeveloperConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DeveloperDatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class DeveloperInfoServlet extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  /**
   * Get a developer's info with developerId and accessSignature 
   * (/developerinfo/{developerId}?accessSignature=...).
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    
    try {
      long developerId = Long.parseLong(req.getPathInfo().substring(1));
      Map developer = DeveloperDatabaseDriver.getDeveloperMap(developerId);
      
      if (developer.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        DeveloperDatabaseDriver.updateDeveloper(developerId, developer);
        json = new JSONObject(developer);
        json.remove(ACCESS_SIGNATURE);
        json.remove(PASSWORD);
      }
      else {
        DeveloperUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      DeveloperUtil.jsonPut(json, ERROR, WRONG_DEVELOPER_ID);
    }
    
    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Updates a developer's information (/developerinfo/{developerId}).
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();

    JSONObject json = new JSONObject();
    
    String[] validParams = {EMAIL, PASSWORD, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME};
    StringBuffer buffer = new StringBuffer();
    String line = null;
    try {
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
      Map originalMap = (Map) JSONUtil.parse(buffer.toString());
      String accessSignature = (String) originalMap.get(ACCESS_SIGNATURE);
      Map<Object, Object> parameterMap = DeveloperUtil.deleteInvalid(originalMap, validParams);
      
      long developerId = Long.parseLong(req.getPathInfo().substring(1));
      Map developer = DeveloperDatabaseDriver.getDeveloperMap(developerId);
      
      if (developer.get(ACCESS_SIGNATURE).equals(accessSignature)) {
        developer.putAll(parameterMap);
        boolean updated = DeveloperDatabaseDriver.updateDeveloper(developerId, developer);
        
        if (updated) {
          DeveloperUtil.jsonPut(json, SUCCESS, UPDATED_DEVELOPER);
        }
        else {
          DeveloperUtil.jsonPut(json, ERROR, EMAIL_EXISTS);
        }    
      }
      else {
        DeveloperUtil.jsonPut(json, ERROR, WRONG_ACCESS_SIGNATURE);
      }      
    }
    catch (EntityNotFoundException | NullPointerException | NumberFormatException | 
        IndexOutOfBoundsException e) {
      DeveloperUtil.jsonPut(json, ERROR, WRONG_DEVELOPER_ID);
    }
    catch (Exception e) { 
      e.printStackTrace();
      DeveloperUtil.jsonPut(json, ERROR, INVALID_JSON);
    }

    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
