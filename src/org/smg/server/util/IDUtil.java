package org.smg.server.util;

import java.util.ArrayList;
import java.util.List;

public class IDUtil {
  
  /**
   * Parse a long format id to String format
   * @param id
   * @return
   */
  public static String longToString (long id) {
    return String.valueOf(id);
  }
  
  /**
   * Parse a String format id to a long format id
   * @param id
   * @return
   * @throws Exception: not number format, or <= 0
   */
  public static long stringToLong (String id) throws Exception{
    long ret = Long.parseLong(id);
    if (ret <= 0) {
      throw new Exception();
    }
    return ret;
  }
  
  /**
   * Parse a String List id to Long list id
   * @param ids
   * @return
   * @throws Exception
   */
  public static List<Long> stringListToLongList (ArrayList<String> ids) throws Exception {
    ArrayList<Long> ret = new ArrayList<Long>();
    for (String id: ids) {
      ret.add(stringToLong(id));
    }
    return ret;
  }
  
  /**
   * Parse a Long List id to String list id
   * @param ids
   * @return
   */
  public static List<String> longListToStringList (List<Long> ids) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Long id: ids) {
      ret.add(longToString(id));
    }
    return ret;
  }
}
