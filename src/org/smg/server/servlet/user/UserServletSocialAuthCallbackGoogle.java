package org.smg.server.servlet.user;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import static org.smg.server.servlet.user.UserConstants.*;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
/**
 * Respond to the callback from Google, after authentication.
 * @author fei
 *
 */
public class UserServletSocialAuthCallbackGoogle extends HttpServlet {
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

	/**
	 * Send a Get request, return the response, only invoked in this servlet
	 * @param url: endpoint URL
	 * @return: response string
	 * @throws IOException
	 */
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

	private Map<Object, Object> getInfoMap(String getReqResp) {
		Map<Object, Object> infoMap = new HashMap<Object, Object>();
		JSONObject jsonOb = null;
		String googleId = null;
		String email = null;
		String familyName = null;
		String givenName = null;
		String imageURL = null;
		try {
			jsonOb = new JSONObject(getReqResp);
			JSONArray emailList = jsonOb.getJSONArray(EMAILS);
			for (int i = 0; i < emailList.length(); i++) {
				JSONObject emailInfo = emailList.getJSONObject(i);
				if (emailInfo.getString(TYPE).equals(ACCOUNT)) {
					email = emailInfo.getString(VALUE);
					break;
				}
			}
			JSONObject names = (JSONObject) jsonOb.get(NAME);
			familyName = names.getString(FAMILY_NAME);
			givenName = names.getString(GIVEN_NAME);
			JSONObject imageOb = jsonOb.getJSONObject(IMAGE);
			imageURL = imageOb.getString(URL);
			googleId = jsonOb.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		infoMap.put(EMAIL, email);
		infoMap.put(FIRST_NAME, givenName);
		infoMap.put(LAST_NAME, familyName);
		infoMap.put(IMAGEURL, imageURL);
		infoMap.put(GOOGLEID, googleId);
		return infoMap;
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		Map<String, String[]> map = req.getParameterMap();
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		JSONObject json = new JSONObject();
		String authCode = null;
		for (String key : map.keySet()) {
			if (key.equals(ERROR)) {
				UserUtil.jsonPut(json, ERROR, SOCIAL_AUTH_DENIED);
				try {
					json.write(writer);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if (key.equals(CODE)) {
				authCode = map.get(key)[0];
			}
		}

		String urlParameters = "code=" + authCode + "&client_id="
				+ GOOGLE_CLIENT_ID + "&client_secret=" + GOOGLE_CLIENT_SECRET
				+ "&redirect_uri=" + GOOGLE_CALLBACK + "&grant_type="
				+ AUTHORIZATION_CODE;
		URL url = new URL(GOOGLE_TOKEN);

		String postReqResp = processPost(urlParameters, url);

		JSONObject jsonOb = null;
		String accToken = null;
		try {
			jsonOb = new JSONObject(postReqResp);
			accToken = (String) jsonOb.get(ACCESS_TOKEN);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		urlParameters = ACCESS_TOKEN + "=" + accToken;

		url = new URL(GOOGLE_PEOPLE + urlParameters);
		String getReqResp = processGet(url);

		url = new URL(
				"https://www.googleapis.com/plus/v1/people/me/people/visible?"
						+ ACCESS_TOKEN + "=" + accToken);
		String friendResp = processGet(url);
		String frinedStr = "";
		ArrayList<String> friendIds = new ArrayList<String>();
		try {
			JSONObject friendJsonOb = new JSONObject(friendResp);
			JSONArray friendArray = friendJsonOb.getJSONArray("items");
			for (int i = 0; i < friendArray.length(); i++) {
				JSONObject friendInfo = friendArray.getJSONObject(i);
				friendIds.add(friendInfo.getString("id"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String newFriends = "{\"data\":[";

		for (int i = 0; i < friendIds.size(); i++) {
			String friendId = friendIds.get(i);
			if (i == 0) {
				List<Entity> friendAsList = UserDatabaseDriver
						.queryUserByProperty(GOOGLEID, friendId);
				if (friendAsList == null || friendAsList.size() == 0)
					newFriends = newFriends + "{\"type\":\"g\",\"socialId\":"
							+ friendId + ",\"SMGId\":null}";
				else {
					long userId = friendAsList.get(0).getKey().getId();
					newFriends = newFriends + "{\"type\":\"g\",\"socialId\":"
							+ friendId + ",\"SMGId\":" + userId + "}";
				}
			} else {
				List<Entity> friendAsList = UserDatabaseDriver
						.queryUserByProperty(GOOGLEID, friendId);
				if (friendAsList == null || friendAsList.size() == 0)
					newFriends = newFriends + ",{\"type\":\"g\",\"socialId\":"
							+ friendId + ",\"SMGId\":null}";
				else {
					long userId = friendAsList.get(0).getKey().getId();
					newFriends = newFriends + ",{\"type\":\"g\",\"socialId\":"
							+ friendId + ",\"SMGId\":" + userId + "}";
				}
			}
		}
		newFriends = newFriends + "]}";
		Map<Object, Object> infoMap = getInfoMap(getReqResp);
		infoMap.put(FRIEND_LIST, new Text(newFriends));

		String emailAddress = (String) infoMap.get(EMAIL);
		List<Entity> userAsList = UserDatabaseDriver.queryUserByProperty(EMAIL,
				emailAddress);
		if (userAsList == null || userAsList.size() == 0) {
			infoMap.put(SOCIAL_AUTH, GOOGLE);
			try {
				long userId = UserDatabaseDriver.insertUser(infoMap);
				String accessSignature = AccessSignatureUtil.generate(userId);
				infoMap.put(ACCESS_SIGNATURE, accessSignature);
				UserDatabaseDriver.updateUser(userId, infoMap);
				UserUtil.jsonPut(json, USER_ID, Long.toString(userId));
				UserUtil.jsonPut(json, ACCESS_SIGNATURE, accessSignature);
				resp.sendRedirect(MAIN_PAGE + "userId=" + Long.toString(userId)
						+ "&accessSignature=" + accessSignature);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (userAsList.get(0).getProperty(SOCIAL_AUTH) != null
					&& userAsList.get(0).getProperty(SOCIAL_AUTH)
							.equals(GOOGLE)) {
				long userId = userAsList.get(0).getKey().getId();
				try {
					Map user = UserDatabaseDriver.getUserMap(userId);
					user.put(ACCESS_SIGNATURE,
							AccessSignatureUtil.generate(userId));
					user.put(FRIEND_LIST, new Text(newFriends));
					UserDatabaseDriver.updateUser(userId, user);
					json = new JSONObject(user);
					resp.sendRedirect(MAIN_PAGE + "userId="
							+ Long.toString(userId) + "&accessSignature="
							+ user.get(ACCESS_SIGNATURE));
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				UserUtil.jsonPut(json, ERROR, EMAIL_HAS_BEEN_REGISTERED);
			}

		}
		try {
			json.write(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
