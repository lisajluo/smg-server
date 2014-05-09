package org.smg.server.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.smg.server.filter.XSSConstants.*;

import org.owasp.esapi.ESAPI;
import org.smg.server.util.CORSUtil;
import org.smg.server.filter.XSSRequestWrapper;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class XSSFilter implements Filter {
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public void destroy() {
  }

  /**
   * Filter XSS content and redirect to certain servlet
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
      throws IOException, ServletException {
    
    CORSUtil.addCORSHeader((HttpServletResponse) response);
    PrintWriter writer = response.getWriter();
    JSONObject json = new JSONObject();
    XSSRequestWrapper multiReadRequest = new XSSRequestWrapper((HttpServletRequest) request);

    if (hasXSSContent(multiReadRequest)) {
      try {
        json.put(ERROR, XSS_ERROR);
        json.write(writer);
      } 
      catch (Exception e) {
        e.printStackTrace();
      }
    } 
    else {
      chain.doFilter(multiReadRequest, response);
    }
  }
  
  /**
   * Verify if a request contains XSS content, if yes return true, otherwise return false
   * @param request
   * @return
   */
  private boolean hasXSSContent(ServletRequest request) {
    BufferedReader reader;
    StringBuffer buffer = new StringBuffer();
    String line = null;
    
    try {
      reader = request.getReader();
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
      
      String originJson = buffer.toString();
      if (originJson.equals(stripXSS(new String(originJson)))) {
        return false;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return true;
  }
  
  /**
   * Given a JSON String, remove all XSS format.
   * Credit to http://www.javacodegeeks.com/2012/07/anti-cross-site-scripting-xss-filter.html
   * @param value
   * @return
   */
  private String stripXSS(String value) {
    if (value != null) {
        value = ESAPI.encoder().canonicalize(value);

        // Avoid null characters
        value = value.replaceAll("", "");

        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid anything in a src='...' type of expression
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid onload= expressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
    }
    
    return value;
  }
}
