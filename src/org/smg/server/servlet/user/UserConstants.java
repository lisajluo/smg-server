package org.smg.server.servlet.user;

import org.smg.server.util.NamespaceUtil;

public class UserConstants {
	public static final String ACCESS_SIGNATURE = "accessSignature";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String MIDDLE_NAME = "middleName";
	public static final String NICK_NAME = "nickName";
	public static final String EMAIL = "email";
	public static final String CODE = "code";
	public static final String PASSWORD = "password";
	public static final String USER_ID = "userId";
	public static final String USER = NamespaceUtil.VERSION + "User";
	public static final String ADMIN_EMAIL = "lisa.j.luo@gmail.com";
	public static final String ADMIN_NAME = "smg-server";
	public static final String SOCIAL_AUTH = "socialAuth";
	public static final String MAIL_SUBJECT = "Please Reset Your Password";
	public static final String GOOGLE = "google";
	public static final String FACEBOOK = "facebook";

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
	public static final String PASSWORD_TOO_SHORT = "PASSWORD_TOO_SHORT";
	public static final String UNSUPPORTED_SOCIAL_AUTH = "UNSUPPORTED_SOCIAL_AUTH";
	public static final String EMAIL_HAS_BEEN_REGISTERED = "EMAIL_HAS_BEEN_REGISTERED";
	public static final String SOCIAL_AUTH_DENIED = "SOCIAL_AUTH_DENIED";
	public static final String SOCIAL_AUTH_ACCOUNT = "SOCIAL_AUTH_ACCOUNT";

	public static final String LOG_OUT = "LOG_OUT";
	public static final String SUCCESS = "success";
	public static final String DELETED_USER = "DELETED_USER";
	public static final String EMAIL_UNABLE_TO_REACH = "EMAIL_UNABLE_TO_REACH";
	public static final String UPDATED_USER = "UPDATED_USER";
	public static final String EMAIL_SENT = "EMAIL_SENT";

	public static final String GOOGLE_SOCIAL_AUTH = "https://accounts.google.com/o/oauth2/auth?";
	public static final String GOOGLE_TOKEN = "https://accounts.google.com/o/oauth2/token";

	public static final String GOOGLE_PEOPLE = "https://www.googleapis.com/plus/v1/people/me?";
	public static final String EMAIL_SCOPE = "email";
	public static final String PROFILE_SCOPE = "profile";

//	public static final String DOMAIN = "http://3-dot-apt-momentum-547.appspot.com/";
	public static final String DOMAIN = "http://smg-server.appspot.com/";
	// public static final String APPURI =
	// "http://smg-server.appspot.com/socialAuthCallBack";
	public static final String GOOGLE_CALLBACK = DOMAIN
			+ "socialAuthCallbackGoogle";
	// public static final String GOOGLE_CLIENT_ID =
	// "1000413534688-h542e5qi0i3l2e3prn9nn7epna5d97hd.apps.googleusercontent.com";
	public static final String GOOGLE_CLIENT_ID = "558267404896.apps.googleusercontent.com";
	// public static final String GOOGLE_CLIENT_SECRET =
	// "fddHJ5A73LjV8D5K0jnf01Qq";
	public static final String GOOGLE_CLIENT_SECRET = "nlg2URIHD3UY5icICwoyyT-N";
	public static final String MAIN_PAGE = DOMAIN + "login-page/mainPage.html?";
	public static final String AUTHORIZATION_CODE = "authorization_code";
	public static final String RETRIEVE_URL = "http://smg-server.appspot.com/passWordReset.html?";
	// public static final String RETRIEVE_URL =
	// "http://3-dot-apt-momentum-547.appspot.com/passWordReset.html";
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String URL_ENCODED = "application/x-www-form-urlencoded";
	public static final String CHAR_SET = "charset";
	public static final String UTF = "utf-8";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String EMAILS = "emails";
	public static final String TYPE = "type";
	public static final String ACCOUNT = "account";
	public static final String VALUE = "value";
	public static final String NAME = "name";
	public static final String FAMILY_NAME = "familyName";
	public static final String GIVEN_NAME = "givenName";
	public static final String GREETINGS = "Please reset your password at the following link:\n";

	public static final String FACEBOOK_APP_ID = "643046942410038";
	// public static final String FACEBOOK_APP_ID = "708188635890698";

	public static final String FACEBOOK_APP_SECRET = "5c0f4887437a71c76df975525b2ee3c2";
	// public static final String FACEBOOK_APP_SECRET =
	// "0f2bd5bb2469cbf2dc4d63f02c8ebfbf";
		
	public static final String FACEBOOK_REDIRECT_URI = DOMAIN
			+ "socialAuthCallbackFacebook";
	public static final String FACEBOOK_SCOPE = "&scope=email,read_friendlists";

	public static final String URL = "url";
	public static final String IMAGE = "image";
	public static final String IMAGEURL = "imageURL";
	public static final String BLOBKEY = "blobKey";
	public static final String FRIEND_LIST = "friendList";
	public static final String FACEBOOKID = "facebookId";
}
