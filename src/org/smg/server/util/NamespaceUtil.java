package org.smg.server.util;

import com.google.appengine.api.utils.SystemProperty;

public class NamespaceUtil {
  /** 
   * Form of applicationVersion is <majorVersion>.<minorVersion> where majorVersion is 
   * the application version specified in appengine-web.xml
   */
  public static String VERSION;
  static {
    try {
      VERSION = SystemProperty.applicationVersion.get().split("\\.")[0];
    } catch (Exception e) {
      VERSION = "LOCAL";
      System.out.println("LOCAL TEST");
    }

  }
}