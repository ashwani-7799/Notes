package Servlet;

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

@WebServlet("/DeleteNoteServlet")
public class DeleteNoteServlet extends HttpServlet {
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

            int userId = (int) session.getAttribute("user_id");

            // Read note ID from request
            String idParam = request.getParameter("id");
            if (idParam == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Missing note ID");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            int noteId = Integer.parseInt(idParam);

            // Delete note query
            try (Connection con = DBConnection.getConnection()) {
                String query = "DELETE FROM notes WHERE id = ? AND user_id = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, noteId);
                stmt.setInt(2, userId);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Note deleted successfully");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Note not found or access denied");
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
