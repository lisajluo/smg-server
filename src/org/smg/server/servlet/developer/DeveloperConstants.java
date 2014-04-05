package org.smg.server.servlet.developer;

import org.smg.server.util.NamespaceUtil;

public final class DeveloperConstants {
  private DeveloperConstants() { }  // Prevent instantiation/subclassing
  
  // Developer entity
  public static final String DEVELOPER = NamespaceUtil.VERSION+"User";
  
  // Various properties in the table
  public static final String EMAIL = "email";
  public static final String PASSWORD = "password";
  public static final String FIRST_NAME = "firstName";
  static final String MIDDLE_NAME = "middleName";
  static final String LAST_NAME = "lastName";
  public static final String NICKNAME = "nickName";
  public static final String DEVELOPER_ID = "developerId";
  public static final String ACCESS_SIGNATURE = "accessSignature";
  
  // Error messages
  public static final String ERROR = "error";
  public static final String MISSING_INFO = "MISSING_INFO";
  public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
  public static final String WRONG_DEVELOPER_ID = "WRONG_DEVELOPER_ID";
  public static final String WRONG_PASSWORD = "WRONG_PASSWORD";
  public static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
  public static final String INVALID_JSON = "INVALID_JSON";
  
  // Success messages
  static final String SUCCESS = "success";
  static final String DELETED_DEVELOPER = "DELETED_DEVELOPER";
  static final String UPDATED_DEVELOPER = "UPDATED_DEVELOPER";
  
  // Invalid indicator
  public static final int INVALID = -1;
}
