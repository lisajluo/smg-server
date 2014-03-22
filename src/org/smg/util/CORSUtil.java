package org.smg.util;

import javax.servlet.http.HttpServletResponse;

public class CORSUtil {
  public static void addCORSHeader(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
    resp.setHeader("Content-Type", "application/json");
  }
}
