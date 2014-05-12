package org.smg.server.servlet.developer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@Deprecated
/**
 * Player and developer entities have been unified to a single User entity.  Developer and Player 
 * calls have been kept in for compatibility reasons (Player and Developer pages requested to keep 
 * the old servlet calls), but in future iterations all should move forward to User servlet calls.
 * Please refer to the {@link org.smg.server.servlet.user.UserUtil}
 */
public class DeveloperUtil {
  /**
   * Deletes keys that are illegal and also massages map into form <String, String> (nested objects
   * are also illegal for the developer login).
   */
  static Map<Object, Object> deleteInvalid(Map<Object, Object> params, String[] validParams) {
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
  
  /**
   * Wrapper for putting object to JSON (catches exception silently).
   */
  public static void jsonPut(JSONObject json, String key, Object obj) {
    try {
      json.put(key, obj);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
