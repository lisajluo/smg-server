package org.smg.server.servlet.admin;

import static org.smg.server.servlet.admin.AdminConstants.*;
import static org.smg.server.servlet.developer.DeveloperConstants.MISSING_INFO;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.container.Utils;
import org.smg.server.servlet.game.GameHelper;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AdminPromote extends HttpServlet{
	private String[] validParams = {ADMIN,USER_ID,ACCESS_SIGNATURE};
	
	
	/**
	 * doPost is called when a client tries to promote a normal user to an administrator
	 * POST /adminPromote/{userId}
	 * The input json :
	 * {“userId”:”123456”,
     * “accessSignature” :”abcdef”,
     * “admin”:true}
     * A successful response: {“success”:”ADMIN_FINISHED”}
     *
	 */
	 public void doPost(HttpServletRequest req, HttpServletResponse resp)
		      throws IOException {
		CORSUtil.addCORSHeader(resp);
		JSONObject jObj = new JSONObject();
		String userId = req.getPathInfo().substring(1);
		boolean pass;
		Map<Object, Object> parameterMap;
		long longId;
		String buffer = new String();
		try {
			 buffer = Utils.getBody(req);
			parameterMap = AdminHelper.deleteInvalid((Map) JSONUtil.parse(buffer),
					validParams);
			if (parameterMap.containsKey(ADMIN) == false) {
				String details = "Required field admin is missing in the input json content";
				GameHelper.sendErrorMessageForJson(resp, jObj, MISSING_INFO, details,
								buffer.toString());
				return;
			}
			pass = (boolean) parameterMap.get(ADMIN);
			String accessSignature = (String) parameterMap
					.get(ACCESS_SIGNATURE);
			String adminId = (String) parameterMap.get(USER_ID);
			Map user = UserDatabaseDriver.getUserMap(Long.parseLong(adminId));
			if (user.get(ACCESS_SIGNATURE).equals(accessSignature) == false)
				throw new Exception();
			longId = Long.parseLong(userId);
			Map userTemp = UserDatabaseDriver.getUserMap(longId);
			userTemp.putAll(parameterMap);
			boolean updated = UserDatabaseDriver.updateUserWithoutPassWord(
					longId, userTemp);
			AdminHelper.put(jObj, SUCCESS, ADMIN_FINISHED, resp);
		} catch (Exception e) {
			String details ="Your target user does not exist in the dataStore";
			GameHelper.sendErrorMessageForJson(resp, jObj, WRONG_USER_ID, details,
					buffer.toString());
			return;
		}
		try {
			pass = (boolean) parameterMap.get(ADMIN);
			AdminHelper.sendEmailToUsersForPromote(longId, pass);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
