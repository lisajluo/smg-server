package org.smg.server.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for change String Id to Long Id and vice versa
 * 
 * @author piper
 * 
 */
public class IDUtil {

  /**
   * Parse a <Long> format id to <String> format
   * 
   * @param id
   * @return
   */
  public static String longToString(long id) {
    return String.valueOf(id);
  }

  /**
   * Parse a <String> id to a <Long> format id
   * 
   * @param id
   * @return
   * @throws Exception
   *           : not number format, or <= 0
   */
  public static long stringToLong(String id) throws Exception {
    long ret = Long.parseLong(id);
    if (ret <= 0) {
      throw new Exception();
    }
    return ret;
  }

  /**
   * Parse a List<String> ids to List<Long> ids
   * 
   * @param ids
   * @return
   * @throws Exception
   *           : not number format, or <= 0
   */
  public static List<Long> stringListToLongList(ArrayList<String> ids)
      throws Exception {
    ArrayList<Long> ret = new ArrayList<Long>();
    for (String id : ids) {
      ret.add(stringToLong(id));
    }
    return ret;
  }

  /**
   * Parse a List<Long> ids to List<String> ids
   * 
   * @param ids
   * @return
   */
  public static List<String> longListToStringList(List<Long> ids) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Long id : ids) {
      ret.add(longToString(id));
    }
    return ret;
  }
}
