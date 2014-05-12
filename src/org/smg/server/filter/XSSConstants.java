package org.smg.server.filter;

public class XSSConstants {
  private XSSConstants() { }  // Prevent instantiation/subclassing
  
  public static String ERROR = "error";
  public static String DETAILS = "details";
  public static String XSS_ERROR = "XSS_ERROR"; 
  public static String VERBOSE_XSS_ERROR = "Your request contained a potential XSS attack. Please correct and resubmit.";
}
