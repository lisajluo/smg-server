package org.smg.server.servlet.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserUtil {
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
	 public static void jsonPut(JSONObject json, String key, Object obj) {
		    try {
		      json.put(key, obj);
		    } 
		    catch (JSONException e) {
		      e.printStackTrace();
		    }
		  }

}
