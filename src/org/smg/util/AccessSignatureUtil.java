package org.smg.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class AccessSignatureUtil {
  //TODO auto generate and update
  private static String saltAccessSignature = "ASDKLFJLHH123S981";
  private static String saltPassword = "SADKF123987SDKJH";
  private static AccessSignatureUtil instance = null;
  private static Object mutex = new Object();
  private static MessageDigest md = null;
  
  public static AccessSignatureUtil getInstance(){
    if (instance == null){
      synchronized(mutex){
        if (instance == null) {
          return new AccessSignatureUtil();
        }
      }
    }
    return instance;
  }
  
  private AccessSignatureUtil() {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  public static String getAccessSignature(String playerId) {
    AccessSignatureUtil.getInstance();
    String sig = playerId+(new Date().toString())+saltAccessSignature;
    md.update(sig.getBytes());
    return new BigInteger(1,md.digest()).toString(16);
  }
  
  public static String getHashedPassword(String password) {
    AccessSignatureUtil.getInstance();
    String sig = password+saltPassword;
    md.update(sig.getBytes());
    return new BigInteger(1,md.digest()).toString(16);
  }
}