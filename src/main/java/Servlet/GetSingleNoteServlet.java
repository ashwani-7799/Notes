package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.utils.DBConnection;
import org.json.JSONObject;

@WebServlet("/GetSingleNoteServlet")
public class GetSingleNoteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        // Check session first
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "User not logged in");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        int userId = (int) session.getAttribute("user_id"); // Get user ID from session

        // Get and validate note ID
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Missing note ID");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            int noteId = Integer.parseInt(idParam);

            try (Connection con = DBConnection.getConnection()) {
                String query = "SELECT title, description FROM notes WHERE id = ? AND user_id = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, noteId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("title", rs.getString("title"));
                    jsonResponse.put("description", rs.getString("description"));
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Note not found or unauthorized access");
                }

                rs.close();
                stmt.close();
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid note ID format");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Database error occurred.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
