package org.smg.server.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class AccessSignatureUtil {
  
  /**
   * Returns an MD5 hash given an id (ie., playerId or developerId).
   */
  public static String generateAccessSignature(int id) {
    String digest = null;
    String secretSignature = "sldfjsldf";
    String hashInput = id + new Date().getTime() + secretSignature;

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hash = md.digest(hashInput.getBytes("UTF-8"));
      
      StringBuilder sb = new StringBuilder(2*hash.length);
      for (byte b : hash) {
          sb.append(String.format("%02x", b&0xff));
      }
     
      digest = sb.toString();
      
    }
    catch (NoSuchAlgorithmException e) {
      //
    }
    catch (UnsupportedEncodingException ex) {
      //
    }
    
    return digest;
  }
}
