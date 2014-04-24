package org.smg.server.servlet.test;

import java.io.IOException;
import javax.servlet.http.*;
import org.owasp.esapi.ESAPI;

  @SuppressWarnings("serial")
public class ESAPIServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");

    // Simulate an attack by entering the string below on a textbox
    // <div>hello <script>alert("This prank should be displayed in a message box.");</script> world</div>
    String userInput = "<div>hello <script>alert(\"This prank should be displayed in a message box.\");</script> world</div>";
    String input2 = "%252E%252E%252F";
    // Your job is to filter the input, it might be an attack
    String content = 
        "<input type=\"text\" value=\"" 
    + ESAPI.encoder().encodeForHTMLAttribute(userInput) 
    + "\" />"
    + "<br>"
    + ESAPI.encoder().encodeForHTMLAttribute(userInput)
    + "<br><input type=\"text\" value=\"" 
    + ESAPI.encoder().encodeForHTMLAttribute(input2) 
    + "\" />"
    + "<br>"
    + ESAPI.encoder().encodeForHTMLAttribute(input2) ;

    resp.getWriter().println(content);
    }
}

