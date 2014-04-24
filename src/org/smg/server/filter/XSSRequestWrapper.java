/*
 * Credit to http://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once
 */

package org.smg.server.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * A wrapper class allowing multiple reads from a request body.
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {
  private ByteArrayOutputStream cachedBytes;

    public XSSRequestWrapper(HttpServletRequest request) {
      super(request);
    }
    
    @Override
    public BufferedReader getReader() throws IOException, NullPointerException {
      return new BufferedReader(new InputStreamReader(getInputStream()));
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException, NullPointerException {
      if (cachedBytes == null) {
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedBytes);
      }

      return new CachedServletInputStream();
    }

    /**
     * An InputStream which reads the cached request body.
     */
    public class CachedServletInputStream extends ServletInputStream {
      private ByteArrayInputStream input;

      public CachedServletInputStream() {
        input = new ByteArrayInputStream(cachedBytes.toByteArray());
      }

      @Override
      public int read() throws IOException {
        return input.read();
      }
    }
}