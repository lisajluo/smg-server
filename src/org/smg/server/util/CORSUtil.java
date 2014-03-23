package org.smg.server.util;

import javax.servlet.http.HttpServletResponse;

public class CORSUtil {
  public static void addCORSHeader(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
    resp.setHeader("Content-Type", "text/csv");
  }
}
