package org.smg.server.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.servlet.developer.DeveloperUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class XSSFilter implements Filter {
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    CORSUtil.addCORSHeader((HttpServletResponse) response);
    PrintWriter writer = response.getWriter();
    JSONObject json = new JSONObject();

    if (hasXSSContent(request)) {
      // TODO write XSS_ERROR
      DeveloperUtil.jsonPut(json, "test", "testing 123");
      try {
        json.write(writer);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } else {
      chain.doFilter(request, response);
    }

  }
  
  private boolean hasXSSContent(ServletRequest request) {
    // TODO filter here
    return true;
  }
}
