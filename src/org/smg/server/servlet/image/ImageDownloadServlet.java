package org.smg.server.servlet.image;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Serves up images stored as blobs in the AppEngine datastore.
 */
@SuppressWarnings("serial")
public class ImageDownloadServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  /**
   * Serves up an image in response the request: GET http://smg-server.appspot.com/images/{blobKey}
   * Used internally at the main page: http://smg-server.appspot.com/index.html
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("image/jpeg");
    
    try {
      BlobKey blobKey = new BlobKey(req.getPathInfo().substring(1));
      blobstoreService.serve(blobKey, resp);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
