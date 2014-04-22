package org.smg.server.servlet.image;

import java.util.Random;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ImageUtil {
  public static String[] avatarPics = { "elephant.gif", "giraffe.gif", "lion.gif", "monkey.gif" };
  
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
  
  /**
   * Picks one of 4 random avatars (lion, monkey, giraffe, elephant) and returns relative URL.
   */
  public static String getAvatarURL() {
    String directoryPrefix = "images/";
    Random rand = new Random(); 
    return directoryPrefix + avatarPics[rand.nextInt(4)];
  }
}
