package org.smg.server.servlet.image;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.smg.server.servlet.image.ImageConstants.*;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Gnerates a Blobstore URL for uploading an image to the smg-server database.  Used at:
 * http://smg-server.appspot.com/index.html (in the menu for "Upload new avatar...").
 * For internal use only due to AppEngine image upload restrictions (cannot be called by an 
 * external site due to CORS restrictions).  Upon uploading to this URL the request is redirected
 * to {@link ImageUploadServlet}.
 */
@SuppressWarnings("serial")
public class ImageUploadURLServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  
  /**
   * Serves an image upload URL in response to: GET http://www.smg-server.appspot.com/uploadurl
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {    
    String absoluteURL = blobstoreService.createUploadUrl(IMAGE_UPLOAD_URL);
    resp.getWriter().println(absoluteURL);
  }
}
