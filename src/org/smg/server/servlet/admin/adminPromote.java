package org.smg.server.servlet.admin;

import static org.smg.server.servlet.admin.adminConstants.*;

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.GameDatabaseDriver;
import org.smg.server.database.UserDatabaseDriver;
import org.smg.server.servlet.container.Utils;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class adminPromote {
	private String[] validParams = {ADMIN};
	private void sendEmailHelper(long userId, boolean pass)
		      throws Exception
		  {
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

		  private void sendEmailToUsers(long userId, boolean pass) throws Exception
		  {
		    try {		    		      
	        sendEmailHelper(userId, pass);
		    
		    } catch (Exception e) {
		      throw new Exception();
		    }
		  }
	private Map<Object, Object> deleteInvalid(Map<Object, Object> params,
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
	private void put(JSONObject jObj, String key, String value, HttpServletResponse resp) {
	    try {
	      jObj.put(key, value);
	      resp.setContentType("text/plain");
	      jObj.write(resp.getWriter());
	    } catch (Exception e) {
	      return;
	    }
	  }
	 public void doPost(HttpServletRequest req, HttpServletResponse resp)
		      throws IOException {
		    CORSUtil.addCORSHeader(resp);
		    JSONObject jObj = new JSONObject();
		    String userId = req.getPathInfo().substring(1);
		    try
		    {
		      String buffer = Utils.getBody(req);
		      Map<Object, Object> parameterMap = deleteInvalid(
		          (Map) JSONUtil.parse(buffer), validParams);
		      if (parameterMap.containsKey(ADMIN) == false)
		      {
		        put(jObj, ERROR, MISSING_INFO, resp);
		        return;
		      }
		      long longId = Long.parseLong(userId);
		      boolean updated = UserDatabaseDriver.updateUserWithoutPassWord(longId, parameterMap);
		      put(jObj, SUCCESS, ADMIN_FINISHED, resp);
		      boolean pass = (boolean) parameterMap.get(ADMIN);
		      sendEmailToUsers(longId, pass);
		      return;
		    } catch (Exception e)
		    {
		      put(jObj, ERROR, WRONG_GAME_ID, resp);
		      return;
		    }
	 }

}
