package org.smg.server.util;

import com.google.appengine.api.utils.SystemProperty;

public class NamespaceUtil {
  /** 
   * In order to prevent conflicts in our AppEngine database across versions (since the same 
   * database is used across all versions), we append the application version to database entities
   * such that the entities are distinct for each verison.  The form of applicationVersion is 
   * <majorVersion>.<minorVersion> where majorVersion is the application version specified in 
   * appengine-web.xml.
   */
  public static String VERSION;
  static {
    try {
      VERSION = SystemProperty.applicationVersion.get().split("\\.")[0];
    } catch (Exception e) {
      VERSION = "LOCAL";
    }
  }
}