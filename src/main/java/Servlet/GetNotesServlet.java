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
import org.json.JSONArray;
import org.json.JSONObject;
import com.utils.DBConnection;

@WebServlet("/GetNotesServlet")
public class GetNotesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        JSONArray notesArray = new JSONArray();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "User not logged in");
            out.print(jsonResponse);
            return;
        }

        int userId = (int) session.getAttribute("user_id"); // Get user ID from session

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT id, title, description, date FROM notes WHERE user_id = ? ORDER BY date DESC";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, userId); // Only fetch notes of the logged-in user
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject note = new JSONObject();
                note.put("id", rs.getInt("id"));
                note.put("title", rs.getString("title"));
                note.put("description", rs.getString("description"));
                note.put("date", rs.getTimestamp("date").toString());
                notesArray.put(note);
            }

            jsonResponse.put("status", "success");
            jsonResponse.put("notes", notesArray);

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Database error occurred.");
        }

        out.print(jsonResponse);
    }
}
