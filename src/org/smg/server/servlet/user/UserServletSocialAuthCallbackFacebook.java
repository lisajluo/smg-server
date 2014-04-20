package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserServletSocialAuthCallbackFacebook extends HttpServlet {
	private String processPost(String parameters, URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod(POST);
		conn.setRequestProperty(CONTENT_TYPE, URL_ENCODED);
		conn.setRequestProperty(CHAR_SET, UTF);
		conn.setRequestProperty(CONTENT_LENGTH,
				"" + Integer.toString(parameters.getBytes().length));
		conn.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();

		InputStream in = conn.getInputStream();
		StringBuffer theString = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null)
			theString.append(line);
		/*
		 * StringWriter writer= new StringWriter(); IOUtils.copy(in, writer,
		 * "UTF-8"); String theString = writer.toString();
		 */
		conn.disconnect();
		return theString.toString();
	}

	private String processGet(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod(GET);
		conn.setRequestProperty(CONTENT_TYPE, URL_ENCODED);
		conn.setRequestProperty(CHAR_SET, UTF);
		conn.setUseCaches(false);

		InputStream in = conn.getInputStream();
		StringBuffer theString = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null)
			theString.append(line);
		/*
		 * StringWriter writer = new StringWriter(); IOUtils.copy(in, writer,
		 * "UTF-8"); String theString = writer.toString();
		 */
		conn.disconnect();
		return theString.toString();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		JSONObject jsonStore = new JSONObject();

		String authCode = req.getParameter("code");
		if (authCode == null || authCode.equals("")) {
			resp.getWriter().println("Authentication code failed");
			return;
		}
		String token = null;
		try {
			String g = "https://graph.facebook.com/oauth/access_token?client_id="
					+ FACEBOOK_APP_ID
					+ "&redirect_uri="
					+ URLEncoder.encode(FACEBOOK_REDIRECT_URI, "UTF-8")
					+ "&client_secret="
					+ FACEBOOK_APP_SECRET
					+ "&code="
					+ authCode;
			URL u = new URL(g);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			token = b.toString();
			if (token.startsWith("{"))
				throw new Exception("error on requesting token: " + token
						+ " with code: " + authCode);
		} catch (Exception e) {
			resp.getWriter().println("Requesting token failed");
			return;
		}

		String graph = null;
		try {
			String g = "https://graph.facebook.com/me?" + token;
			URL u = new URL(g);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			graph = b.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String facebookId = null;
		String firstName = null;

		String lastName = null;
		String email = null;
		try {
			JSONObject json = new JSONObject(graph);
			facebookId = json.getString("id");
			firstName = json.getString("first_name");
			lastName = json.getString("last_name");
			email = json.getString("email");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		graph = processGet(new URL("https://graph.facebook.com/" + facebookId
				+ "?fields=picture.type(large)"));

		String profileURL = null;
		try {
			JSONObject json = new JSONObject(graph);
			JSONObject picOb = (JSONObject) json.get("picture");
			JSONObject dataOb = (JSONObject) picOb.get("data");
			profileURL = dataOb.getString("url");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		graph = processGet(new URL("https://graph.facebook.com/me/friends?"
				+ token));

		String friendNames[] = new String[5];
		String friendIds[] = new String[5];
		String friends = "[";
		try {
			JSONObject json = new JSONObject(graph);
			JSONArray friendArray = json.getJSONArray("data");
			for (int i = 0; i < 5; i++) {
				String friendStr = "";
				JSONObject friend = friendArray.getJSONObject(i);
				friendNames[i] = friend.getString("name");
				friendIds[i] = friend.getString("id");
				if (friendIds[i].equals("") && friendIds[i] == null)
					break;
				friendStr = "{" + "\"name\":" + friendNames[i] + ",\"id\":"
						+ friendIds[i] + "}";
				if (i != 0)
					friends += ",";
				friends += friendStr;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		friends += "]";
		// firstName, lastName, email, friends, profileURL
		Map<Object, Object> infoMap = new HashMap<Object, Object>();
		List<Entity> userAsList = UserDatabaseDriver.queryUserByProperty(EMAIL,
				email);
		if (userAsList == null || userAsList.size() == 0) {
			infoMap.put(SOCIAL_AUTH, FACEBOOK);
			infoMap.put(EMAIL, email);
			infoMap.put(FIRST_NAME, firstName);
			infoMap.put(LAST_NAME, lastName);
			infoMap.put(FRIEND_LIST, friends);
			infoMap.put(IMAGEURL, profileURL);
			try {
				long userId = UserDatabaseDriver.insertUser(infoMap);
				String accessSignature = AccessSignatureUtil.generate(userId);
				infoMap.put(ACCESS_SIGNATURE, accessSignature);
				UserDatabaseDriver.updateUser(userId, infoMap);
				UserUtil.jsonPut(jsonStore, USER_ID, Long.toString(userId));
				UserUtil.jsonPut(jsonStore, ACCESS_SIGNATURE, accessSignature);
				resp.sendRedirect(MAIN_PAGE + "userId=" + Long.toString(userId)
						+ "&accessSignature=" + accessSignature);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (userAsList.get(0).getProperty(SOCIAL_AUTH) != null
					&& userAsList.get(0).getProperty(SOCIAL_AUTH)
							.equals(FACEBOOK)) {
				long userId = userAsList.get(0).getKey().getId();
				try {
					Map user = UserDatabaseDriver.getUserMap(userId);
					user.put(ACCESS_SIGNATURE,
							AccessSignatureUtil.generate(userId));
					UserDatabaseDriver.updateUser(userId, user);
					jsonStore = new JSONObject(user);
					resp.sendRedirect(MAIN_PAGE + "userId="
							+ Long.toString(userId) + "&accessSignature="
							+ user.get(ACCESS_SIGNATURE)+"&accessTokenFB="+token);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				UserUtil.jsonPut(jsonStore, ERROR, EMAIL_HAS_BEEN_REGISTERED);
			}
		}
		try {
			jsonStore.write(resp.getWriter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
