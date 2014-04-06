package org.smg.server.servlet.image;

import static org.smg.server.servlet.developer.DeveloperConstants.ERROR;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;
import static org.smg.server.servlet.image.ImageConstants.*;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class ImageUploadServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private static final Logger log = Logger.getLogger(ImageUploadServlet.class.getName());

  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    
    log.warning("ARRIVED");
    
    PrintWriter writer = resp.getWriter();
    JSONObject json = new JSONObject();
    String urlPrefix = req.getRequestURL().toString().replace(BLOB_UPLOAD_URL, IMAGES_PATH);
    
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
    List<String> keyStringList = new ArrayList<String>(); 
    
    for (Map.Entry<String, List<BlobKey>> entry : blobs.entrySet()) {
      List<BlobKey> blobList = entry.getValue();
      for (BlobKey blobKey : blobList) {
        keyStringList.add(urlPrefix + blobKey.getKeyString());
      }
    }
    
    if (keyStringList.isEmpty()) {
      ImageUtil.jsonPut(json, ERROR, IMAGE_UPLOAD_ERROR);
    } 
    else {
      ImageUtil.jsonPut(json, IMAGES, keyStringList);
    }
    
    try {
      json.write(writer);
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
