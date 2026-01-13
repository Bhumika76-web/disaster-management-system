package dao;

import models.NotificationAcknowledgment;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationAcknowledgmentDAO {

    public boolean recordNotification(NotificationAcknowledgment notif) {
        String query = "INSERT INTO notification_acknowledgments (user_id, disaster_id, username, alert_type, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, notif.getUserId());
            pstmt.setInt(2, notif.getDisasterId());
            pstmt.setString(3, notif.getUsername());
            pstmt.setString(4, notif.getAlertType());
            pstmt.setString(5, notif.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error recording notification: " + e.getMessage());
            return false;
        }
    }

    public int getAcknowledgedCount() {
        String query = "SELECT COUNT(*) as count FROM notification_acknowledgments WHERE status = 'acknowledged'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error counting acknowledged: " + e.getMessage());
        }
        return 0;
    }

    public int getIgnoredCount() {
        String query = "SELECT COUNT(*) as count FROM notification_acknowledgments WHERE status = 'ignored'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error counting ignored: " + e.getMessage());
        }
        return 0;
    }

    public double getAcknowledgmentPercentage() {
        int acknowledged = getAcknowledgedCount();
        int ignored = getIgnoredCount();
        int total = acknowledged + ignored;

        if (total == 0) return 0;
        return (acknowledged * 100.0) / total;
    }

    public List<NotificationAcknowledgment> getUserNotifications(int userId) {
        List<NotificationAcknowledgment> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_acknowledgments WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NotificationAcknowledgment notif = new NotificationAcknowledgment();
                notif.setId(rs.getInt("id"));
                notif.setUserId(rs.getInt("user_id"));
                notif.setDisasterId(rs.getInt("disaster_id"));
                notif.setUsername(rs.getString("username"));
                notif.setAlertType(rs.getString("alert_type"));
                notif.setStatus(rs.getString("status"));
                notif.setTimestamp(rs.getString("timestamp"));
                notifications.add(notif);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user notifications: " + e.getMessage());
        }
        return notifications;
    }
}