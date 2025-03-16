package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import com.utils.DBConnection;

@WebServlet("/AddNoteServlet")
public class AddNoteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "User not logged in");
            out.print(jsonResponse);
            return;
        }

        int userId = (int) session.getAttribute("user_id");

        // Read request body safely
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        try {
            JSONObject requestData = new JSONObject(requestBody.toString()); // Parse JSON data
            String title = requestData.getString("title");
            String description = requestData.getString("description");

            try (Connection con = DBConnection.getConnection()) {
                String query = "INSERT INTO notes (user_id, title, description, date) VALUES (?, ?, ?, NOW())";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, title);
                stmt.setString(3, description);

                int rowsInserted = stmt.executeUpdate();
                stmt.close();

                if (rowsInserted > 0) {
                    jsonResponse.put("status", "success");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Failed to add note");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Database error occurred.");
        }

        out.print(jsonResponse);
        out.flush();
    }
}
