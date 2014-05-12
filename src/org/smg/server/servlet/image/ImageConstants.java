package org.smg.server.servlet.image;

/**
 * String constants used in image upload servlets: paths and error messages.
 */
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
  static final String DETAILS = "details";
  static final String VERBOSE_UPLOAD_ERROR = "Image failed to save to the Blobstore.";
}
