package org.smg.server.servlet.image;

import java.util.logging.Logger;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;

import static org.smg.server.servlet.image.ImageConstants.*;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class ImageForwardServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private static final Logger log = Logger.getLogger(ImageForwardServlet.class.getName());
  
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    
    String absoluteURL = blobstoreService.createUploadUrl(BLOB_UPLOAD_URL);
    String hostname = req.getRequestURL().toString().replace(IMAGE_UPLOAD_URL, "");
    String relativeUrl = absoluteURL.replace(hostname, "");
    
    log.warning("Relative URL is " + relativeUrl);
    
    RequestDispatcher rd = req.getRequestDispatcher(relativeUrl);
    rd.forward(req, resp);
  }
}
