package org.smg.server.servlet.user;

import static org.smg.server.servlet.user.UserConstants.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.game.GameUtil;
import org.smg.server.util.AccessSignatureUtil;
import org.smg.server.util.CORSUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UserInfoRetrieve extends HttpServlet{
	/**
	 * doGet is called when the user tries to retrive the passWord
	 * If this operation is successful, an email will be sent to the email specified in the URL 
	 * containing a URL where the user can change his/her password
	 * 
	 * A successful response:
	 * {“success” : “EMAIL_SENT”}
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CORSUtil.addCORSHeader(resp);
		PrintWriter writer = resp.getWriter();
		String emailAddress = req.getPathInfo().substring(1);
		JSONObject json = new JSONObject();
		List<Entity> userAsList = null;
		userAsList = UserDatabaseDriver
				.queryUserByProperty(EMAIL, emailAddress);
		if (userAsList == null || userAsList.size() == 0) {
			String url = GameUtil.getFullURL(req); 
			String details = "Your email address doesn't exist in our dataStore";
			UserHelper.sendErrorMessageForUrl(resp, json, WRONG_EMAIL, details,
							url);
			return;
			

		}
		if (userAsList.get(0).getProperty(SOCIAL_AUTH) != null) {
		    String url = GameUtil.getFullURL(req); 
			String details = "You are a social-auth user, please retrieve your password via your social-auth site";
			UserHelper.sendErrorMessageForUrl(resp, json, SOCIAL_AUTH_ACCOUNT, details,
							url);
			return;
			
			
		}
		long userId = userAsList.get(0).getKey().getId();
		String accessSignature = (String) userAsList.get(0).getProperty(
				ACCESS_SIGNATURE);
		String firstName = (String) userAsList.get(0).getProperty(FIRST_NAME);
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		StringBuffer msgBodyBuffer = new StringBuffer();
		msgBodyBuffer.append("Hello " + firstName + ":\n");
		msgBodyBuffer.append(GREETINGS);
		msgBodyBuffer.append(RETRIEVE_URL + "userId=" + String.valueOf(userId)
				+ "&accessSignature=" + accessSignature);
		String msgBody = msgBodyBuffer.toString();
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(ADMIN_EMAIL, ADMIN_NAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					emailAddress, firstName));
			msg.setSubject(MAIL_SUBJECT);
			msg.setText(msgBody);
			Transport.send(msg);
			UserUtil.jsonPut(json, SUCCESS, EMAIL_SENT);
			json.write(writer);
			return;

		} catch (AddressException e) {
			UserUtil.jsonPut(json, ERROR, EMAIL_UNABLE_TO_REACH);
			try {
				json.write(writer);
			} catch (Exception t) {
				t.printStackTrace();
			}
			return;
		} catch (MessagingException e) {
			UserUtil.jsonPut(json, ERROR, MESSAGE_ERROR);
			try {
				json.write(writer);
			} catch (Exception t) {
				t.printStackTrace();
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	

}
