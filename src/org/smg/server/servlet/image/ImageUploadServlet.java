package org.smg.server.servlet.image;

import static org.smg.server.servlet.image.ImageConstants.ERROR;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.user.UserConstants;
import static org.smg.server.servlet.image.ImageConstants.*;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * Uploads a new profile picture to the database and stores the URL in the user entity.
 * Used internally at the main page: http://smg-server.appspot.com/index.html
 */ 
@SuppressWarnings("serial")
public class ImageUploadServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  
  /**
   * AppEngine will redirect the upload request to this servlet upon success since the url 
   * /imageupload was specified in the creation of the upload URL at {@link ImageUploadURLServlet}.
   * For internal use only due to AppEngine image upload restrictions ({@link ImageUploadURLServlet} 
   * cannot be called by an external site due to CORS restrictions).
   * If the upload is successful, the servlet returns: { "imageURL": "...." }
   * Otherwise if there was some database error (pertaining to the blob/image upload), then
   * the servlet returns: 
   * { "error": "IMAGE_UPLOAD_ERROR", "details": "Image failed to save to the Blobstore." }
   * (There is no other information in the request other than image data, so we cannot return
   * userId or any other pertinent information.)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String urlPrefix = req.getRequestURL().toString().replace(IMAGE_UPLOAD_URL, IMAGES_PATH);

    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
    try {
      BlobKey blobKey = blobs.get("profile_pic").get(0);
      String imageUrl = urlPrefix + blobKey.getKeyString();
      
      Long userId = Long.parseLong(req.getParameter("userId"));
      Map user = UserDatabaseDriver.getUserMap(userId);
      
      // Remove old blob from blobstore
      if (user.containsKey(UserConstants.BLOBKEY)) {
        blobstoreService.delete((BlobKey) user.get(UserConstants.BLOBKEY));
      }
      
      user.put(UserConstants.IMAGEURL, imageUrl);
      user.put(UserConstants.BLOBKEY, blobKey);
      UserDatabaseDriver.updateUserWithoutPassWord(userId, user);
      
      ImageUtil.jsonPut(json, UserConstants.IMAGEURL, imageUrl);
    }
    catch (Exception e) {
      e.printStackTrace();
      ImageUtil.jsonPut(json, ERROR, IMAGE_UPLOAD_ERROR);
      ImageUtil.jsonPut(json, DETAILS, VERBOSE_UPLOAD_ERROR);
    }
    
    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
