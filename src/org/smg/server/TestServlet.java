package org.smg.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    resp.getWriter().println("Hello, world");
  }
  
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    resp.getWriter().println("Hello, world");
  }
  
  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    resp.getWriter().println("Hello, world");
  }
}
