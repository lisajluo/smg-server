
package org.smg.server.servlet.container;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ChannelConnectServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    String body = Utils.getBody(req);
    System.out.println(req.getContextPath());
    System.out.println(req.getPathInfo());
    System.out.println(req.getPathTranslated());
    System.out.println(req.getQueryString());
    System.out.println(req.getServletPath());
    System.out.println(body);
    ChannelService channelService = ChannelServiceFactory.getChannelService();
    // String channelKey = getChannelKey(user);
    // channelService.sendMessage(new ChannelMessage(channelKey,
    // getMessageString()));
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    System.out.println(req.getContextPath());
    System.out.println(req.getPathInfo());
    System.out.println(req.getPathTranslated());
    System.out.println(req.getQueryString());
    System.out.println(req.getServletPath());
    doPost(req, resp);
  }

}
