package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.util.CORSUtil;
import org.smg.util.JSONUtil;

public class NewMatchServlet extends HttpServlet {

  @Override 
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {  
    CORSUtil.addCORSHeader(resp);
//    {"accessSignature": ..., 
//      "playerIds": [1234,5679], 
//      "gameId}: 12312}
    Map<String,Object> paraMap = req.getParameterMap();
    String[] jsonData = (String[])paraMap.get("jsonData");
    Map<String,Object> jsonMap = JSONUtil.parse(jsonData[0]);
//    Map<String,Object> jsonMap = JSONUtil.parse(jsonData[0]);
    System.out.println("here");
  }
  
}
