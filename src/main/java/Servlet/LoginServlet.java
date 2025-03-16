package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.utils.DBConnection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Email and password are required.");
                out.print(jsonResponse);
                out.flush();
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                String query = "SELECT id , name FROM userx WHERE email=? AND password=?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String name = rs.getString("name");
                    HttpSession session = request.getSession();
                    session.setAttribute("user_id", userId);
                    session.setAttribute("email", email);
                    session.setAttribute("name", name);

                    jsonResponse.put("status", "success");
                    jsonResponse.put("user_id", userId);
                    jsonResponse.put("email", email);
                    jsonResponse.put("name", name);
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Invalid credentials.");
                }

                rs.close();
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Database error.");
        } finally {
            out.print(jsonResponse);
            out.flush();
            out.close();
        }
    }

    // New GET method to check session
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user_id") != null) {
            jsonResponse.put("status", "success");
            jsonResponse.put("email", session.getAttribute("email"));
        } else {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Not logged in.");
        }

        out.print(jsonResponse);
        out.flush();
        out.close();
    }
}
