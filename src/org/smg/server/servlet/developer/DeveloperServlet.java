package org.smg.server.servlet.developer;

import static org.smg.server.servlet.developer.Constants.*;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.smg.server.database.DatabaseDriver;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

@SuppressWarnings("serial")
public class DeveloperServlet extends HttpServlet {
  
  /**
   * Unique developerId for each developer account.  For now we will just increment by 1 for
   * each new developer.  Reserving some of the initial developerIds just in case.
   */
  static int incDeveloperId = 5000;
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    long developerId;
    
    String accessSignature = req.getParameter(ACCESS_SIGNATURE);
    try {
      developerId = Integer.parseInt(req.getPathInfo().substring(1));
    }
    catch (NumberFormatException e) {
      developerId = -1;
    }
    
    System.out.println(developerId);
    Map developer = DatabaseDriver.getEntityMapByKey(DEVELOPER, developerId);
    //Map developer = DatabaseDriver.queryByProperty(DEVELOPER, DEVELOPER_ID,  developerId).get(0).getProperties();
    
    if (developerId == -1 || developer == null) {  // No developer found for developerId
      json.put(ERROR, WRONG_DEVELOPER_ID);
    }
    else if (developer.get(ACCESS_SIGNATURE).equals(accessSignature)) {
      DatabaseDriver.deleteEntity(DEVELOPER, developerId);
      json.put(SUCCESS, DELETED_DEVELOPER);
    }
    else {
      json.put(ERROR, WRONG_ACCESS_SIGNATURE);
    }

    json.writeJSONString(writer);
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
    long developerId;
    
    String password = req.getParameter(PASSWORD);
    try {
      developerId = Integer.parseInt(req.getPathInfo().substring(1));
    }
    catch (NumberFormatException e) {
      developerId = -1;
    }
    System.out.println(developerId);
    Map developer = DatabaseDriver.getEntityMapByKey(DEVELOPER, developerId);
    /** @TODO fix this! Why doesn't key match work? Below will blow up if the query is empty
     */
    //Map developer = DatabaseDriver.queryByProperty(DEVELOPER, DEVELOPER_ID,  developerId).get(0).getProperties();
    
    if (developerId == -1 || developer == null) {  // No developer found for developerId
      json.put(ERROR, WRONG_DEVELOPER_ID);
    }
    else if (developer.get(PASSWORD).equals(password)) {
      developer.put(ACCESS_SIGNATURE, AccessSignatureUtil.generate(developerId));
      DatabaseDriver.insertEntity(DEVELOPER, developerId, developer);
      json = new JSONObject(developer);
    }
    else {
      json.put(ERROR, WRONG_PASSWORD);
    }

    json.writeJSONString(writer);
  }
  
  /**
   * Inserts a new developer.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    
    String[] validParams = {EMAIL, PASSWORD, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME};
    
    Map<Object, Object> parameterMap = deleteInvalid(req.getParameterMap(), validParams);
    
    if (parameterMap.get(EMAIL) == null || parameterMap.get(PASSWORD) == null) {
      json.put(ERROR, MISSING_INFO);
    }
    else if (DatabaseDriver.queryByProperty(
        DEVELOPER, EMAIL, (String) parameterMap.get(EMAIL)).isEmpty()) {
      String accessSignature = AccessSignatureUtil.generate(incDeveloperId);
      parameterMap.put(ACCESS_SIGNATURE, accessSignature);
      parameterMap.put(DEVELOPER_ID, incDeveloperId);
      // Add to database
      DatabaseDriver.insertEntity(DEVELOPER, incDeveloperId, parameterMap);

      // Return response  
      json.put(DEVELOPER_ID, incDeveloperId);
      json.put(ACCESS_SIGNATURE, accessSignature);
      incDeveloperId++;
    } 
    else {
      json.put(ERROR, EMAIL_EXISTS);
    }   
    
    json.writeJSONString(writer);
  }
  
  /**
   * Deletes keys that are illegal and also massages map into form <String, String>.
   */
  private Map<Object, Object> deleteInvalid(Map<String, String[]> params, String[] validParams) {
    Map<Object, Object> returnMap = new HashMap<Object, Object>();
    for (Map.Entry<String, String[]> entry : params.entrySet()) {
      if (Arrays.asList(validParams).contains(entry.getKey())) {
        returnMap.put(entry.getKey(), entry.getValue()[0]);
      }
    }
    
    return returnMap;
  }
}