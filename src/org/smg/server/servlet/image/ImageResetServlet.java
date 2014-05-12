package org.smg.server.servlet.image;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

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
 * Resets a user's profile image to one of the four default smg-server images.
 */
@SuppressWarnings("serial")
public class ImageResetServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  
  /**
   * Resets the user's profile image to one of the four default smg-server images in response the 
   * request: DELETE http://smg-server.appspot.com/resetavatar/{userId}
   * Used internally at the main page: http://smg-server.appspot.com/index.html
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    
    try {
      long userId = Long.parseLong(req.getPathInfo().substring(1));
      Map user = UserDatabaseDriver.getUserMap(userId);
      
      // Remove old blob from blobstore
      if (user.containsKey(UserConstants.BLOBKEY)) {
        blobstoreService.delete((BlobKey) user.get(UserConstants.BLOBKEY));
        user.remove(UserConstants.BLOBKEY);
      } 
      
      // Randomly pick one of avatars
      String hostname = req.getScheme() + "://" + req.getServerName();
      
      // Local debugging
      /*
      int serverPort = req.getServerPort(); 
      if (serverPort != 80 && serverPort != 443) {
        hostname += ":" + serverPort;
      }
      */
      
      String imageUrl = hostname + "/" + ImageUtil.getAvatarURL();
      user.put(UserConstants.IMAGEURL, imageUrl);
      UserDatabaseDriver.updateUserWithoutPassWord(userId, user);
      
      ImageUtil.jsonPut(json, UserConstants.IMAGEURL, imageUrl);
    }
    catch (Exception e) {
      e.printStackTrace();
      ImageUtil.jsonPut(json, ERROR, IMAGE_DELETE_ERROR);
    }
    
    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
