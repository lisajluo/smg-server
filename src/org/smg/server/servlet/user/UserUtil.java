package org.smg.server.servlet.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserUtil {
	/**
	 * Filter out all the keys that are not contained in the validParams in the
	 * JSON input when the client is doing a POST request
	 * 
	 * @param params a Map of the json input
	 * @param validParams an array of the desired keywords
	 * @return a map with all its keywords coming from the validParams
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
	 * Put the key value pair into a jsonObject
	 * @param json
	 * @param key
	 * @param obj
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
