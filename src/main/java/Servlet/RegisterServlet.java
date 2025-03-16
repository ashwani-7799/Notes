package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.DBConnection;
import org.json.JSONObject;

@WebServlet("/RegisterServlet")
@MultipartConfig
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        JSONObject jsonResponse = new JSONObject();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String gender = request.getParameter("gender");
        String dob = request.getParameter("dob");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO userx(name, email, password, gender, dob) VALUES (?, ?, ?, ?, ?)")) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, password);
            pst.setString(4, gender);
            pst.setString(5, dob);

            int result = pst.executeUpdate();
            if (result > 0) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Information saved. Please login.");
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Registration failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}
