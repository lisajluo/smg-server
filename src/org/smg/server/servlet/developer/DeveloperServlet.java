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
  static int incDeveloperId = 300;
    
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    
    String json = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
    resp.getWriter().println(json);

  }
  
  /**
   * Inserts a new developer.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    
    String[] validParams = {EMAIL, PASSWORD, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME};
    
    @SuppressWarnings("unchecked")
    Map<Object, Object> parameterMap = deleteInvalid(req.getParameterMap(), validParams);
    
    PrintWriter writer = resp.getWriter();
    
    if (parameterMap.get(EMAIL) == null || parameterMap.get(PASSWORD) == null) {
      writer.println(MISSING_INFO_JSON);
    }
    else if (DatabaseDriver.queryByProperty(
        DEVELOPER, EMAIL, (String) parameterMap.get(EMAIL)).isEmpty()) {
      // Add to database
      DatabaseDriver.insertEntity(DEVELOPER, Integer.toString(incDeveloperId), parameterMap);

      // Return response  
      String accessSignature = AccessSignatureUtil.generateAccessSignature(incDeveloperId);
      /* writer.println("{ \"" + DEVELOPER_ID + "\": " + incDeveloperId + ", " +
                        "\"" + ACCESS_SIGNATURE + "\": \"" + accessSignature +	"\" }"); */
      
      JSONObject obj = new JSONObject();
      obj.put(DEVELOPER_ID, incDeveloperId);
      obj.put(ACCESS_SIGNATURE, accessSignature);
      obj.writeJSONString(writer);
      
      incDeveloperId++;
    } 
    else {
      writer.println(EMAIL_EXISTS_JSON);

    }    
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