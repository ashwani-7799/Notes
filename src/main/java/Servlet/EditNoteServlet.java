package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
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

@WebServlet("/EditNoteServlet")
public class EditNoteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        try {
            // Validate session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user_id") == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "User not logged in");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            int userId = (int) session.getAttribute("user_id"); // Ensure user is editing only their notes

            // Read JSON request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());

            // Extract parameters
            int noteId = jsonRequest.optInt("id", -1);
            String title = jsonRequest.optString("title", "").trim();
            String description = jsonRequest.optString("description", "").trim();

            // Validate input
            if (noteId == -1 || title.isEmpty() || description.isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Missing required parameters");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Update query with user validation
            try (Connection con = DBConnection.getConnection()) {
                String query = "UPDATE notes SET title = ?, description = ? WHERE id = ? AND user_id = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, title);
                stmt.setString(2, description);
                stmt.setInt(3, noteId);
                stmt.setInt(4, userId);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Note updated successfully");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Failed to update note or note not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Internal server error");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
