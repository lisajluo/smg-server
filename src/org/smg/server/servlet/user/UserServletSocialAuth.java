package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserServletSocialAuth extends HttpServlet{
	
	private Map<String,String> getInfoFromSocialAuth(String SocialAuth)
	{
		//TODO implement social Auth
		return null;
	}
	
	@SuppressWarnings({ "rawtypes" })
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CORSUtil.addCORSHeader(resp);
		Set<String> supportedSocialAuth = new HashSet<String>();
		supportedSocialAuth.add(GOOGLE);
		Map user = new HashMap();
		long userId = -1;
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
					+ APPURI + "&response_type=code" + "&client_id="
					+ CLIENT_ID + "&approval_prompt=force");
			return;
		}
		default:
			break;
		}
		
		
	}

}
