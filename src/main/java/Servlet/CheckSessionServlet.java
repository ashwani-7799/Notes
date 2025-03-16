package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet("/CheckSessionServlet") // New servlet for session validation
public class CheckSessionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false); // Get existing session, don't create new one
        if (session != null && session.getAttribute("name") != null) {
            jsonResponse.put("status", "success");
            jsonResponse.put("name", session.getAttribute("name"));
        } else {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "User not logged in");
        }

        out.print(jsonResponse);
        out.flush();
    }
}
