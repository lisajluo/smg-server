package org.smg.server.servlet.admin;

import static org.smg.server.servlet.admin.AdminConstants.ADMIN_EMAIL;
import static org.smg.server.servlet.admin.AdminConstants.ADMIN_NAME;
import static org.smg.server.servlet.admin.AdminConstants.EMAIL;
import static org.smg.server.servlet.admin.AdminConstants.FIRST_NAME;
import static org.smg.server.servlet.admin.AdminConstants.MAIL_SUBJECT;
import static org.smg.server.servlet.admin.AdminConstants.approve;
import static org.smg.server.servlet.admin.AdminConstants.degrade;
import static org.smg.server.servlet.admin.AdminConstants.disapprove;
import static org.smg.server.servlet.admin.AdminConstants.promote;

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
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AdminHelper {
	/**<p>
	 * Filter out all the keys that are not contained in the validParams in the
	 * JSON input when the client is doing a POST request
	 * 
	 * @param params a Map of the json input
	 * @param validParams an array of the desired keywords
	 * @return a map with all its keywords coming from the validParams
	 */
	public static Map<Object, Object> deleteInvalid(Map<Object, Object> params,
		      String[] validParams) {
		    Map<Object, Object> returnMap = new HashMap<Object, Object>();
		    for (Map.Entry<Object, Object> entry : params.entrySet()) {
		      if (Arrays.asList(validParams).contains(entry.getKey())) {
		        if (entry.getKey() instanceof String) {
		          returnMap.put(entry.getKey(), entry.getValue());
		        }
		      }
		    }
		    return returnMap;
		  }
	/** <p> 
	 * Put the json-formatted data into response
	 * 
	 * @param jObj
	 * @param key
	 * @param value
	 * @param resp
	 * @return 
	 */
	public static void put(JSONObject jObj, String key, String value, HttpServletResponse resp) {
	    try {
	      jObj.put(key, value);
	      resp.setContentType("text/plain");
	      jObj.write(resp.getWriter());
	    } catch (Exception e) {
	      return;
	    }
	  }
	/**
	 * Put the text template into the actual email
	 * @param userId
	 * @param pass
	 * @throws Exception
	 */
	private static void sendEmailHelperForPromote(long userId, boolean pass)
			throws Exception {
		try {
			Map userInfo = UserDatabaseDriver.getUserMap(userId);
			String firstName = (String) userInfo.get(FIRST_NAME);

			String emailAddress = (String) userInfo.get(EMAIL);

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			StringBuffer msgBodyBuffer = new StringBuffer();
			msgBodyBuffer.append("Hello " + firstName + ":\n");
			if (pass == true) {
				msgBodyBuffer.append(promote());

			} else {
				msgBodyBuffer.append(degrade());
			}

			String msgBody = msgBodyBuffer.toString();
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(ADMIN_EMAIL, ADMIN_NAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					emailAddress, firstName));
			msg.setSubject(MAIL_SUBJECT);
			msg.setText(msgBody);
			Transport.send(msg);
			return;

		} catch (Exception e) {
			throw new Exception();
		}
	}
    /**
     * send an email to the user notifying his/her admin status
     * @param userId
     * @param pass
     * @throws Exception
     */
	public static void sendEmailToUsersForPromote(long userId, boolean pass)
			throws Exception {
		try {
			sendEmailHelperForPromote(userId, pass);

		} catch (Exception e) {
			throw new Exception();
		}
	}
    /**
     * Put the text template into the actual email
     * @param gameName
     * @param developerId
     * @param pass
     * @param Text
     * @throws Exception
     */
	private static void sendEmailHelperForCensor(String gameName,
			long developerId, boolean pass, String Text) throws Exception {
		try {
			Map developerInfo = UserDatabaseDriver.getUserMap(developerId);
			String firstName = (String) developerInfo.get(FIRST_NAME);
			String emailAddress = (String) developerInfo.get(EMAIL);
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			StringBuffer msgBodyBuffer = new StringBuffer();
			msgBodyBuffer.append("Hello " + firstName + ":\n");
			if (pass == true) {
				msgBodyBuffer.append(approve(gameName));

			} else {
				msgBodyBuffer.append(disapprove(gameName));
				if (Text != null)
					msgBodyBuffer.append(Text);
			}

			String msgBody = msgBodyBuffer.toString();
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(ADMIN_EMAIL, ADMIN_NAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					emailAddress, firstName));
			msg.setSubject(MAIL_SUBJECT);
			msg.setText(msgBody);
			Transport.send(msg);
			return;

		} catch (Exception e) {
			throw new Exception();
		}
	}
    /**
     * send an email to the developer of the game notifying the decision of the admin
     * @param gameId
     * @param pass
     * @param Text
     * @throws Exception
     */
	public static void sendEmailToDevelopersForCensor(long gameId,
			boolean pass, String Text) throws Exception {
		try {
			List<String> developerIdList = GameDatabaseDriver
					.getDeveloperList(gameId);
			String gameName = GameDatabaseDriver.getGameName(gameId);
			for (int i = 0; i < developerIdList.size(); i++) {
				Long developerIdLong = Long.parseLong(developerIdList.get(i));
				sendEmailHelperForCensor(gameName, developerIdLong, pass, Text);
			}

		} catch (Exception e) {
			throw new Exception();
		}
	}

}
