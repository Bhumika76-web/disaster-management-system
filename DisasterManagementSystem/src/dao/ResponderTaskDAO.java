package dao;

import models.ResponderTask;
import database.DatabaseConnection;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponderTaskDAO {
    private static final String CLASS_NAME = "ResponderTaskDAO";


    public boolean assignTask(ResponderTask task) {
        String query = "INSERT INTO responder_tasks (responder_id, zone, task_description, status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, task.getResponderId());
            pstmt.setString(2, task.getZone());
            pstmt.setString(3, task.getTaskDescription());
            pstmt.setString(4, task.getStatus());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Task assigned: Responder=" + task.getResponderId() +
                        ", Zone=" + task.getZone());
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to assign task", e);
        }
        return false;
    }


    public List<ResponderTask> getTasksByZone(String zone) {
        List<ResponderTask> tasks = new ArrayList<>();
        String query = "SELECT * FROM responder_tasks WHERE zone = ? AND status = 'active' " +
                "ORDER BY assigned_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, zone);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + tasks.size() +
                    " active tasks for zone: " + zone);
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching tasks for zone: " + zone, e);
        }
        return tasks;
    }


    public int getResponderCountByZone(String zone) {
        String query = "SELECT COUNT(DISTINCT responder_id) as count FROM responder_tasks " +
                "WHERE zone = ? AND status = 'active'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, zone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                Logger.debug(CLASS_NAME, "Zone: " + zone + ", Active Responders: " + count);
                return count;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error counting responders for zone: " + zone, e);
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

            Logger.debug(CLASS_NAME, "Found " + zones.size() + " active zones");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching active zones", e);
        }
        return zones;
    }


    public boolean updateTaskStatus(int taskId, String newStatus) {
        String query = "UPDATE responder_tasks SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Task status updated: ID=" + taskId +
                        ", Status=" + newStatus);
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to update task status", e);
        }
        return false;
    }


    private ResponderTask extractTaskFromResultSet(ResultSet rs) throws SQLException {
        ResponderTask task = new ResponderTask();
        task.setId(rs.getInt("id"));
        task.setResponderId(rs.getInt("responder_id"));
        task.setZone(rs.getString("zone"));
        task.setTaskDescription(rs.getString("task_description"));
        task.setStatus(rs.getString("status"));
        task.setAssignedAt(rs.getString("assigned_at"));
        return task;
    }
}