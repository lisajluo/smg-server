package org.smg.server.servlet.developer;

import static org.smg.server.servlet.developer.DeveloperConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriver;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class DeveloperServlet extends HttpServlet {  
  
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  /**
   * Delete a developer with developerId and accessSignature 
   * (/developers/{developerId}?accessSignature=...).
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    String devIdStr = req.getPathInfo().substring(1);
    long developerId = INVALID;
    
    try {
      developerId = Long.parseLong(devIdStr);
    }
    catch (NumberFormatException e) {
      //
    }
    Map developer = DatabaseDriver.getDeveloperMapByKey(developerId);
    
    if (developerId == INVALID || developer == null) {  // No developer found for developerId
      jsonPut(json, writer, ERROR, WRONG_DEVELOPER_ID);
    }
    else if (developer.get(ACCESS_SIGNATURE).equals(accessSignature)) {
      DatabaseDriver.deleteEntity(DEVELOPER, developerId);
      jsonPut(json, writer, SUCCESS, DELETED_DEVELOPER);
    }
    else {
      jsonPut(json, writer, ERROR, WRONG_ACCESS_SIGNATURE);
    }

    try {
      json.write(writer);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  /**
   * Login a developer with developerId and password (/developers/{developerId}?password=...).
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    
    String password = req.getParameter(PASSWORD);
    String devIdStr = req.getPathInfo().substring(1);
    long developerId = INVALID;
    
    try {
      developerId = Long.parseLong(devIdStr);
    }
    catch (NumberFormatException e) {
      //
    }
    Map developer = DatabaseDriver.getDeveloperMapByKey(developerId);

    if (developerId == INVALID || developer == null) {  // No developer found for developerId
      jsonPut(json, writer, ERROR, WRONG_DEVELOPER_ID);
    }
    else if (developer.get(PASSWORD).equals(password)) {
      developer.put(ACCESS_SIGNATURE, AccessSignatureUtil.generate(developerId));
      DatabaseDriver.updateDeveloper(developerId, developer);
      json = new JSONObject(developer);
    }
    else {
      jsonPut(json, writer, ERROR, WRONG_PASSWORD);
    }

    try {
      json.write(writer);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Inserts a new developer.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
   
      Map<Object, Object> parameterMap = deleteInvalid(
          (Map) JSONUtil.parse(buffer.toString()), validParams);
      
      if (parameterMap.get(EMAIL) == null || parameterMap.get(PASSWORD) == null) {
        jsonPut(json, writer, ERROR, MISSING_INFO);
      }
      else {
        // Add to database
        long developerId = DatabaseDriver.insertDeveloper(parameterMap);
        
        if (developerId == INVALID) {
          jsonPut(json, writer, ERROR, EMAIL_EXISTS);
        }
        else {
          String accessSignature = AccessSignatureUtil.generate(developerId);
          parameterMap.put(ACCESS_SIGNATURE, accessSignature);
          // Update database with access signature
          DatabaseDriver.updateDeveloper(developerId, parameterMap);
    
          // Return response  
          jsonPut(json, writer, DEVELOPER_ID, developerId);
          jsonPut(json, writer, ACCESS_SIGNATURE, accessSignature);
        }
      }
    }
    catch (Exception e) { 
      e.printStackTrace();
      jsonPut(json, writer, ERROR, INVALID_JSON);
    }

    try {
      json.write(writer);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
  
  /**
   * Deletes keys that are illegal and also massages map into form <String, String> (nested objects
   * are also illegal for the developer login).
   */
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
  
  private void jsonPut(JSONObject json, PrintWriter writer, String obj1, Object obj2) {
    try {
      json.put(obj1, obj2);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}