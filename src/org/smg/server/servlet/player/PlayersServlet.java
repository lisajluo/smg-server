package org.smg.server.servlet.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.util.AccessSignatureUtil;
import org.smg.util.CORSUtil;
import org.smg.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * player servlet to handle player request
 * Insert:
 *    method: POST
 *    url:/players
 *    required input:email, password
 *    return:  {"playerId": 1234, "accessSignature": ...}
 *             {"error": "EMAIL_EXISTS"}
 *             {"error": "PASSWORD_TOO_SHORT"} (<6)
 * Login:
 *    method: GET
 *    url:/players/[playerId]?password=
 *    return: {"email": ..., "accessSignature": ...}
 *            {"error": "WRONG_PASSWORD"}
 *            {"error": "WRONG_PLAYER_ID"}
 * Update:
 *    method PUT
 *    url:/players/[playerId]
 *    required input: accessSignature, password (password to be updated)
 *    return: {"success": "UPDATED_PLAYER"}
 *            {"error": "WRONG_ACCESS_SIGNATURE"}
 *            {"error": "WRONG_PLAYER_ID"}
 * Delete:
 *    method DELETE
 *    url:/players/[playerId]?accessSignature=
 *    return: {"success": "DELETED_PLAYER"}
 *            {"error": "WRONG_ACCESS_SIGNATURE"}
 *            {"error": "WRONG_PLAYER_ID"}
 * @author Archer
 * 
 *         TODO combine this with /players/{playerId}
 */
@SuppressWarnings("serial")
public class PlayersServlet extends HttpServlet {
  /**
   *  * Login:
   *    method: GET
   *    url:/players/[playerId]?password=
   *    return: {"email": ..., "accessSignature": ...}
   *            {"error": "WRONG_PASSWORD"}
   *            {"error": "WRONG_PLAYER_ID"}
 *
   */
	@SuppressWarnings("unchecked")
  @Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		resp.setContentType("text/plain");
		String player = null;
		if (req.getPathInfo().length() != 0 && req.getPathInfo() != null)
			player = req.getPathInfo().substring(1);
		else
			player = null;
		//getParameterMap() return String
		Map<String, Object> map = req.getParameterMap();
		if (map.containsKey("password")) {
		  //Login
		  System.out.println(req.getPathInfo().split("/")[1]);
		  System.out.println(req.getParameter("password"));
		  //System.out.println(password);
		  //String [] result = DatabaseDriver.loginPlayer(playerId, password);
		  return;
		} else {
		  //cannot find password
		  //output {"error": "WRONG_PASSWORD"}
		  return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter res = resp.getWriter();
		CORSUtil.addCORSHeader(resp);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				req.getInputStream()));
		String json = new String();
		String line = "";
		StringBuffer buffer = new StringBuffer();
		while ((line = br.readLine()) != null)
			buffer.append(line);
		json = buffer.toString();
		System.out.println(json);
		JSONObject returnValue = new JSONObject();
		if (json != null && json.length() != 0) {
			Map<String, Object> jsonMap = JSONUtil.parse(json);
			String email = (String) jsonMap
					.get(PlayerProperty.EMAIL.toString());
			String password = (String) jsonMap.get("PASSWORD");
			String firstName = (String) jsonMap.get(PlayerProperty.FIRSTNAME
					.toString());
			String lastName = (String) jsonMap.get(PlayerProperty.LASTNAME
					.toString());
			String nickName = (String) jsonMap.get(PlayerProperty.NICKNAME
					.toString());
			try {
				returnValue.put(PlayerProperty.EMAIL.toString(), email);
				returnValue.put(PlayerProperty.HASHEDPASSWORD.toString(),
						AccessSignatureUtil.getHashedPassword(password));
				returnValue.put(PlayerProperty.FIRSTNAME.toString(), firstName);
				returnValue.put(PlayerProperty.LASTNAME.toString(), lastName);
				returnValue.put(PlayerProperty.NICKNAME.toString(), nickName);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(returnValue);
			try {
				returnValue.write(resp.getWriter());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
	}
}