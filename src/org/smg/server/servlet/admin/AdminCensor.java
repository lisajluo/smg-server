
package org.smg.server.servlet.admin;

import static org.smg.server.servlet.admin.AdminConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.MISSING_INFO;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.container.Utils;
import org.smg.server.servlet.game.GameHelper;
import org.smg.server.servlet.user.UserUtil;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AdminCensor extends HttpServlet {

  private String[] validParams = {
    AUTHORIZED,TEXT,USER_ID,ACCESS_SIGNATURE
  };

  

  

	/**
	 * doPost is called when a client tries to authorize a game
	 * POST /adminCensor/{gameId}
	 * The input json format : 
	 * {“userId”:”123456”,
     *  “accessSignature” :”abcdef”,
     *  “authorized”:true,//Indicating whether the game is authorized/unauthorized
     *  “TEXT” : “The drag and drop doesn’t work” //Optional, when the admin refuses to authorize this game, he can put the reason for that in this field
     *  }
	 * A successful response: 
	 * {“success”:”ADMIN_FINISHED”}
	 * 
	 */
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject jObj = new JSONObject();
    String gameId = req.getPathInfo().substring(1);
    Map<Object, Object> parameterMap;
    long longId;
    String buffer =new String();
    String text;
		try {
			buffer = Utils.getBody(req);
			parameterMap = AdminHelper.deleteInvalid((Map) JSONUtil.parse(buffer),
					validParams);
			if (parameterMap.containsKey(AUTHORIZED) == false) {
				String details = "Required field authorized is missing in the input json content";
				GameHelper.sendErrorMessageForJson(resp, jObj, MISSING_INFO, details,
								buffer.toString());
				return;
			}
			long userId = Long.parseLong((String) parameterMap.get(USER_ID));
			String accessSignature = (String) parameterMap
					.get(ACCESS_SIGNATURE);
			Map user = UserDatabaseDriver.getUserMap(userId);
			if (user.get(ACCESS_SIGNATURE).equals(accessSignature) == false)
				throw new Exception();
			parameterMap.put(UPDATED, false);
			longId = Long.parseLong(gameId);
			text = (String) parameterMap.get(TEXT);
			if (parameterMap.containsKey(TEXT) == true)
				parameterMap.remove(TEXT);
			GameDatabaseDriver.updateGame(longId, parameterMap);
			AdminHelper.put(jObj, SUCCESS, ADMIN_FINISHED, resp);
		} catch (Exception e) {
			String details ="Your userId and accessSignature don't match";
			GameHelper.sendErrorMessageForJson(resp, jObj, WRONG_INFO, details,
					buffer.toString());
			return;
		}
		try {
			boolean pass = (boolean) parameterMap.get(AUTHORIZED);
			AdminHelper.sendEmailToDevelopersForCensor(longId, pass, text);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
  }
  
}
