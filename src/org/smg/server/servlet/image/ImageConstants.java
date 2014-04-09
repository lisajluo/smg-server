package org.smg.server.servlet.image;

public class ImageConstants {
  private ImageConstants() { }  // Prevent instantiation/subclassing
  
  // Upload URLs
  static final String BLOB_UPLOAD_URL = "/blobupload";
  static final String IMAGE_UPLOAD_URL = "/imageupload";
  static final String IMAGES_PATH = "/images/";
  
  // Error messages
  static final String ERROR = "error";
  static final String IMAGE_UPLOAD_ERROR = "IMAGE_UPLOAD_ERROR";
  
  // JSON key
  static final String IMAGES = "images";
}
