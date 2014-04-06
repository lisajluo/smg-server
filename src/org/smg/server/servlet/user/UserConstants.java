package org.smg.server.servlet.user;

import org.smg.server.util.NamespaceUtil;

public class UserConstants {
	public static final String ACCESS_SIGNATURE = "accessSignature";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String MIDDLE_NAME = "middleName";
	public static final String NICK_NAME = "nickName";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String USER_ID = "userId";
	public static final String USER = NamespaceUtil.VERSION + "User";
	public static final String ADMIN_EMAIL = "hk1642@nyu.edu";
	public static final String ADMIN_NAME = "huan-kang";
	public static final String MAIL_SUBJECT = "Please Reset Your Password";
    public static final String GOOGLE = "google";
	
	public static final String WRONG_PASSWORD = "WRONG_PASSWORD";
	public static final String ERROR = "error";
	public static final String MISSING_INFO = "MISSING_INFO";
	public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
	public static final String WRONG_EMAIL = "WRONG_EMAIL";
	public static final String INVALID_JSON = "INVALID_JSON";
	public static final String WRONG_USER_ID = "WRONG_USER_ID";
	public static final String MESSAGE_ERROR = "MESSAGE_ERROR";
	public static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
	public static final int INVALID = -1;
	public static final String  PASSWORD_TOO_SHORT = "PASSWORD_TOO_SHORT";
	public static final String UNSUPPORTED_SOCIAL_AUTH = "UNSUPPORTED_SOCIAL_AUTH";
	
	public static final String SUCCESS = "success";
	public static final String DELETED_USER = "DELETED_USER";
	public static final String EMAIL_UNABLE_TO_REACH = "EMAIL_UNABLE_TO_REACH";
	public static final String UPDATED_USER = "UPDATED_USER";
	public static final String EMAIL_SENT = "EMAIL_SENT";
}


