package org.smg.server.util;

import javax.servlet.http.HttpServletResponse;

public class CORSUtil {
  /**
   * Adds the proper headers to allow cross-domain REST calls.  In the future when the components
   * are all merged to the same domain, this call can be removed.
   */
  public static void addCORSHeader(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
    resp.setHeader("Access-Control-Allow-Headers", "X-GWT-Module-Base, X-GWT-Permutation, Content-Type");
  }
}