package org.smg.server.servlet.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.EntityNotFoundException;
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
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  
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
		JSONObject returnValue = new JSONObject();
		String playerId = null;
		if (req.getPathInfo() != null && req.getPathInfo().length() > 0 ) {
			playerId = req.getPathInfo().substring(1);
		}
		else {
		  try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return;
		}
		long playerIdLong;
		try {
      playerIdLong = Long.parseLong(playerId);
    } catch (NumberFormatException e) {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e2) {
        e2.printStackTrace();
      }
      return;
    }
		Map<String, Object> map = req.getParameterMap();
		if (!map.containsKey("password")){
		  try {
        returnValue.put("error", "WRONG_PASSWORD");
        returnValue.write(resp.getWriter());
      } catch (JSONException e2) {
        e2.printStackTrace();
      }
      return;
		}
		String originalPassword = req.getParameter("password");
		try {
      String[] result = DatabaseDriverPlayer.loginPlayer(playerIdLong, originalPassword);
      if (result[0].equals("WRONG_PASSWORD")){
        try {
          returnValue.put("error", "WRONG_PASSWORD");
          returnValue.write(resp.getWriter());
        } catch (JSONException e2) {
          e2.printStackTrace();
        }
      } else {
        try {
          returnValue.put("email", result[0]);
          returnValue.put("accessSignature", result[1]);
          returnValue.write(resp.getWriter());
        } catch (JSONException e2) {
          e2.printStackTrace();
        }
      }
      return;
    } catch (EntityNotFoundException e) {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e2) {
        e2.printStackTrace();
      }
      return;
    }
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		CORSUtil.addCORSHeader(resp);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				req.getInputStream()));
		String json = new String();
		String line = "";
		StringBuffer buffer = new StringBuffer();
		while ((line = br.readLine()) != null)
			buffer.append(line);
		json = buffer.toString();
		Map<String, Object> map = JSONUtil.parse(json);
		String originalString = (String) map.get("password");
		JSONObject returnValue = new JSONObject();
		if (originalString == null || originalString.length() < 6) {
			try {
				returnValue.put("error", "PASSWORD_TOO_SHORT");
				returnValue.write(resp.getWriter());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		Player player = JSONUtil.jSON2Player(json);
		String saveResult = DatabaseDriverPlayer.savePlayer(player);
		if (saveResult.equals("EMAIL_EXISTS")) {
		  try {
				returnValue.put("error", "EMAIL_EXISTS");
				returnValue.write(resp.getWriter());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		} else if (saveResult.startsWith("SUCCESS:")) {
			try {
				returnValue.put("playerId", saveResult.split(":")[1]);
				returnValue.put("accessSignature", saveResult.split(":")[2]);
				returnValue.write(resp.getWriter());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		} else {
		  System.err.println("doPost error occur: unknown state for insert new player");
		}
		return;

	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	  CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    JSONObject returnValue = new JSONObject();
    String playerId = null;
    if (req.getPathInfo() != null && req.getPathInfo().length() > 0 ) {
      playerId = req.getPathInfo().substring(1);
    }
    else {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return;
    }
    long playerIdLong;
    try {
      playerIdLong = Long.parseLong(playerId);
    } catch (NumberFormatException e) {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e2) {
        e2.printStackTrace();
      }
      return;
    }
    @SuppressWarnings("unchecked")
    Map<String, Object> map = req.getParameterMap();
    if (!map.containsKey("accessSignature")){
      try {
        returnValue.put("error", "WRONG_ACCESS_SIGNATURE");
        returnValue.write(resp.getWriter());
      } catch (JSONException e2) {
        e2.printStackTrace();
      }
      return;
    }
    String accessSignature = req.getParameter("accessSignature");
    try {
      String result = DatabaseDriverPlayer.deletePlayer(playerIdLong, accessSignature);
      if (result.equals("WRONG_ACCESS_SIGNATURE")){
        try {
          returnValue.put("error", "WRONG_ACCESS_SIGNATURE");
          returnValue.write(resp.getWriter());
        } catch (JSONException e2) {
          e2.printStackTrace();
        }
      } else {
        try {
          returnValue.put("success", "DELETED_PLAYER");
          returnValue.write(resp.getWriter());
        } catch (JSONException e2) {
          e2.printStackTrace();
        }
      }
      return;
    } catch (EntityNotFoundException e) {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e2) {
        e2.printStackTrace();
      }
      return;
    }
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	  CORSUtil.addCORSHeader(resp);

    BufferedReader br = new BufferedReader(new InputStreamReader(
        req.getInputStream()));
    String json = new String();
    String line = "";
    StringBuffer buffer = new StringBuffer();
    while ((line = br.readLine()) != null)
      buffer.append(line);
    json = buffer.toString();
    JSONObject returnValue = new JSONObject();
    Player player = JSONUtil.jSON2Player(json);
    String playerId = null;
    if (req.getPathInfo() != null && req.getPathInfo().length() > 0 ) {
      playerId = req.getPathInfo().substring(1);
    }
    if (playerId == null) {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    }
    player.setProperty(PlayerProperty.PLAYERID, playerId);
    String saveResult = DatabaseDriverPlayer.savePlayer(player);
    if (saveResult.equals("UPDATED_PLAYER")) {
      try {
        returnValue.put("success", "UPDATED_PLAYER");
//        returnValue.put("PLAYERID",
//            player.getProperty(PlayerProperty.PLAYERID));
//        returnValue.put("ACCESSSIGNATURE",
//            player.getProperty(PlayerProperty.ACCESSSIGNATURE));
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    } else if (saveResult.equals("WRONG_ACCESS_SIGNATURE")) {
      try {
        returnValue.put("error", "WRONG_ACCESS_SIGNATURE");
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    } else if (saveResult.equals("WRONG_PLAYER_ID")) {
      try {
        returnValue.put("error", "WRONG_PLAYER_ID");
        returnValue.write(resp.getWriter());
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    } else {
      System.err.println("doPut error occur: unknown state");
    }
    return;
	}
}
