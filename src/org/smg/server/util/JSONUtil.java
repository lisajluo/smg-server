package org.smg.server.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
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
  
  public static List<HashMap<String, Object>> parseHistory(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();    
    // read JSON from a file
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
  
  public static String mapToString(Map<String,Object> map) throws IOException {
    ObjectMapper mapper = new ObjectMapper();   
    String json = mapper.writeValueAsString(map);
    return json;
  }
  
  
}
