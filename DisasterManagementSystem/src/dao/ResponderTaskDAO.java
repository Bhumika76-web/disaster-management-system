package dao;

import models.ResponderTask;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponderTaskDAO {

    public boolean assignTask(ResponderTask task) {
        String query = "INSERT INTO responder_tasks (responder_id, zone, task_description, status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, task.getResponderId());
            pstmt.setString(2, task.getZone());
            pstmt.setString(3, task.getTaskDescription());
            pstmt.setString(4, task.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error assigning task: " + e.getMessage());
            return false;
        }
    }

    public List<ResponderTask> getTasksByZone(String zone) {
        List<ResponderTask> tasks = new ArrayList<>();
        String query = "SELECT * FROM responder_tasks WHERE zone = ? AND status = 'active' ORDER BY assigned_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, zone);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ResponderTask task = new ResponderTask();
                task.setId(rs.getInt("id"));
                task.setResponderId(rs.getInt("responder_id"));
                task.setZone(rs.getString("zone"));
                task.setTaskDescription(rs.getString("task_description"));
                task.setStatus(rs.getString("status"));
                task.setAssignedAt(rs.getString("assigned_at"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching tasks by zone: " + e.getMessage());
        }
        return tasks;
    }

    public int getResponderCountByZone(String zone) {
        String query = "SELECT COUNT(DISTINCT responder_id) as count FROM responder_tasks WHERE zone = ? AND status = 'active'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, zone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error counting responders: " + e.getMessage());
        }
        return 0;
    }

    public List<String> getActiveZones() {
        List<String> zones = new ArrayList<>();
        String query = "SELECT DISTINCT zone FROM responder_tasks WHERE status = 'active'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                zones.add(rs.getString("zone"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching zones: " + e.getMessage());
        }
        return zones;
    }

    public boolean updateTaskStatus(int taskId, String newStatus) {
        String query = "UPDATE responder_tasks SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating task status: " + e.getMessage());
            return false;
        }
    }
}