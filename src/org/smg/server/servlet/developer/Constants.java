package org.smg.server.servlet.developer;

public final class Constants {
  private Constants() { }  // Prevent instantiation/subclassing
  
  // Developer entity
  static final String DEVELOPER = "Developer";
  
  // Various properties in the table
  static final String EMAIL = "email";
  static final String PASSWORD = "password";
  static final String FIRST_NAME = "firstName";
  static final String MIDDLE_NAME = "middleName";
  static final String LAST_NAME = "lastName";
  static final String NICKNAME = "nickname";
  static final String DEVELOPER_ID = "developerId";
  static final String ACCESS_SIGNATURE = "accessSignature";
  
  /* Error JSON objects */
  static final String MISSING_INFO_JSON = "{ \"error\": \"MISSING_INFO\" }";
  static final String EMAIL_EXISTS_JSON = "{ \"error\": \"EMAIL_EXISTS\" }";
}
