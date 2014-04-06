package org.smg.server.servlet.image;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ImageUtil {
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
