package org.smg.server.util;

import static org.smg.server.servlet.user.UserConstants.DOMAIN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.servlet.image.ImageUtil;
import org.smg.server.servlet.user.UserConstants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
  private static final String LOWER_FIRST_NAME = "firstname";
  private static final String LOWER_LAST_NAME = "lastname";
  private static final String LOWER_NICKNAME = "nickname";
  
	public static Player jSON2Player(String json) throws IOException{
		Map<String, Object> jsonMap = JSONUtil.parse(json);
		
		// Pick random avatar
    String imageUrl = DOMAIN + ImageUtil.getAvatarURL();
    jsonMap.put(UserConstants.IMAGEURL, imageUrl);
		
		/* Case change to ensure backwards compatibility with V2 API calls */
		if (jsonMap.containsKey(LOWER_FIRST_NAME)) {
		  jsonMap.put(PlayerProperty.firstName.name(), jsonMap.get(LOWER_FIRST_NAME));
		  jsonMap.remove(LOWER_FIRST_NAME);
		}
		if (jsonMap.containsKey(LOWER_NICKNAME)) {
		  jsonMap.put(PlayerProperty.nickName.name(), jsonMap.get(LOWER_NICKNAME));
		  jsonMap.remove(LOWER_NICKNAME);
		}
		if (jsonMap.containsKey(LOWER_LAST_NAME)) {
		  jsonMap.put(PlayerProperty.lastName.name(), jsonMap.get(LOWER_LAST_NAME));
		  jsonMap.remove(LOWER_LAST_NAME);
		}
		
		Player player = new Player();
		for(String key : jsonMap.keySet())
		{
		  PlayerProperty pp;
		  String value;
		  if (key.equalsIgnoreCase("password")) {
		    pp = PlayerProperty.password;
		    value = AccessSignatureUtil.getHashedPassword((String)jsonMap.get(key));
		  } else {
		    pp = PlayerProperty.findByValue(key);
        value = (String)jsonMap.get(key);
		  }
		  boolean success;
      try {
        success = player.setProperty(pp, value);
      } catch (Exception e) {
        System.err.println("Trying to set illegal property to player: "+key);
        e.printStackTrace();
      }
		}
		return player;
	}
	
	/**
	 * Convert JSONString to a Map
	 * @param json
	 * @return
	 * @throws IOException
	 */
  public static Map<String, Object> parse(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();    
    // read JSON from a file
    Map<String, Object> map = new HashMap<String,Object>();
    
      try {
        map = mapper.readValue(
          json,
          new TypeReference<Map<String, Object>>() {
        });
      } catch (IOException e) {
        // TODO Auto-generated catch block
        throw new IOException("JSON_PARSE_ERROR");
      }    
     return map;
  }
  
  /**
   * Convert JSONString to a List.
   */
  public static List<HashMap<String, Object>> parseList(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();    
    List<HashMap<String, Object>> list = new LinkedList<HashMap<String,Object>>();    
      try {
        list = mapper.readValue(
          json,
          new TypeReference<List<HashMap<String, Object>>>() {
        });
      } catch (IOException e) {
        // TODO Auto-generated catch block
        throw new IOException("JSON_PARSE_ERROR");
      }    
     return list;
  }
  
  /**
   * Convert a map to a JSONString
   * @param map
   * @return
   * @throws IOException
   */
  public static String mapToString(Map<String,Object> map) throws IOException {
    ObjectMapper mapper = new ObjectMapper();   
    String json = mapper.writeValueAsString(map);
    return json;
  }
  
  /**
   * Parse a playerIds string to a list<String>
   * @param json
   * @return
   * @throws IOException
   */
  public static List<String> parsePlayerIds(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();    
    List<String> list = new ArrayList<String>();    
      try {
        list = mapper.readValue(
          json,
          new TypeReference<List<String>>() {
        });
      } catch (IOException e) {
        // TODO Auto-generated catch block
        throw new IOException("JSON_PARSE_ERROR");
      }    
     return list;
  }
  
  /**
   * Parse a playerIds string to a list<Long>
   * @param json
   * @return
   * @throws IOException
   */
  public static List<Long> parseDSPlayerIds(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();    
    List<Long> list = new ArrayList<Long>();    
      try {
        list = mapper.readValue(
          json,
          new TypeReference<List<Long>>() {
        });
      } catch (IOException e) {
        // TODO Auto-generated catch block
        throw new IOException("JSON_PARSE_ERROR");
      }    
     return list;
  }
}
