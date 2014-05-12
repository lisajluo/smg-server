package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.util.CORSUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * Responds to the request of user's login via Google or Facebook
 * @author fei
 *
 */
@SuppressWarnings("serial")
public class UserServletSocialAuth extends HttpServlet {


	/**
	 * Responds to the request of user's login via Google or Facebook, by sending an authentication redirect 
	 * request to FB or G+. 
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		Set<String> supportedSocialAuth = new HashSet<String>();
		supportedSocialAuth.add(GOOGLE);
		supportedSocialAuth.add(FACEBOOK);
		PrintWriter writer = resp.getWriter();
		JSONObject json = new JSONObject();
		String socialAuthType = req.getPathInfo().substring(1);
		if (supportedSocialAuth.contains(socialAuthType) == false) {
			UserUtil.jsonPut(json, ERROR, UNSUPPORTED_SOCIAL_AUTH);
			try {
				json.write(writer);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		switch (socialAuthType) {
		case (GOOGLE): {
			resp.sendRedirect(GOOGLE_SOCIAL_AUTH + "scope=" + EMAIL_SCOPE + " "
					+ PROFILE_SCOPE + "&state=%2Fprofile" + "&redirect_uri="
					+ GOOGLE_CALLBACK + "&response_type=code" + "&client_id="
					+ GOOGLE_CLIENT_ID + "&approval_prompt=force");
			return;
		}
		case (FACEBOOK): {
			resp.sendRedirect("https://www.facebook.com/dialog/oauth?"
					+ "client_id=" + FACEBOOK_APP_ID + "&redirect_uri="
					+ FACEBOOK_REDIRECT_URI + FACEBOOK_SCOPE);
			return;
		}
		default:
			break;
		}

	}

}
