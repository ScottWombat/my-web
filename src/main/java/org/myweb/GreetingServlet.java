package org.myweb;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    


@WebServlet(urlPatterns = "/GreetingServlet")
public class GreetingServlet extends HttpServlet {
  private static final String PAGE_HEADER = "<html><head><title>Jakarta Servlet Example</title></head><body>";
  private static final String PAGE_FOOTER = "</body></html>";
  
  @Inject
  private GreetingService greetingService;

  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var name = req.getParameter("name");
    resp.setContentType("text/html");
    var writer = resp.getWriter();
    writer.println(PAGE_HEADER);

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now();  
    
    // write message to http response
    writer.println("<h1> Hello Servlet </h1>");
    writer.println("<p>" + greetingService.buildGreetingMessage(name) + "</p>");
    writer.println("<p>" + dtf.format(now) +  "</p>");
    writer.println(PAGE_FOOTER);
    writer.close();
}
}
