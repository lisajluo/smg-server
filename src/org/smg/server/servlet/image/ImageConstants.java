package org.smg.server.servlet.image;

public class ImageConstants {
  private ImageConstants() { }  // Prevent instantiation/subclassing
  
  // Upload URLs
  static final String IMAGE_UPLOAD_URL = "/imageupload";
  static final String IMAGE_DELETE_URL = "/removeavatar";
  static final String IMAGES_PATH = "/images/";
  
  // Error messages
  static final String ERROR = "error";
  static final String IMAGE_UPLOAD_ERROR = "IMAGE_UPLOAD_ERROR";
  static final String IMAGE_DELETE_ERROR = "IMAGE_DELETE_ERROR";
}
