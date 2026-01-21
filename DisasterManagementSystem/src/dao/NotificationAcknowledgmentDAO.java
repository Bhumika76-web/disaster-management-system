package dao;

import models.NotificationAcknowledgment;
import database.DatabaseConnection;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationAcknowledgmentDAO {
    private static final String CLASS_NAME = "NotificationAcknowledgmentDAO";


    public boolean recordNotification(NotificationAcknowledgment notif) {
        String query = "INSERT INTO notification_acknowledgments " +
                "(user_id, disaster_id, username, alert_type, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, notif.getUserId());
            pstmt.setInt(2, notif.getDisasterId());
            pstmt.setString(3, notif.getUsername());
            pstmt.setString(4, notif.getAlertType());
            pstmt.setString(5, notif.getStatus());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Notification recorded: User=" + notif.getUsername() +
                        ", Status=" + notif.getStatus());
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to record notification", e);
        }
        return false;
    }


    public int getAcknowledgedCount() {
        String query = "SELECT COUNT(*) as count FROM notification_acknowledgments " +
                "WHERE status = 'acknowledged'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int count = rs.getInt("count");
                Logger.debug(CLASS_NAME, "Total acknowledged notifications: " + count);
                return count;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error counting acknowledged notifications", e);
        }
        return 0;
    }


    public int getIgnoredCount() {
        String query = "SELECT COUNT(*) as count FROM notification_acknowledgments " +
                "WHERE status = 'ignored'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int count = rs.getInt("count");
                Logger.debug(CLASS_NAME, "Total ignored notifications: " + count);
                return count;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error counting ignored notifications", e);
        }
        return 0;
    }


    public double getAcknowledgmentPercentage() {
        int acknowledged = getAcknowledgedCount();
        int ignored = getIgnoredCount();
        int total = acknowledged + ignored;

        if (total == 0) {
            Logger.debug(CLASS_NAME, "No notifications to calculate percentage");
            return 0.0;
        }

        double percentage = (acknowledged * 100.0) / total;
        Logger.debug(CLASS_NAME, "Acknowledgment percentage: " + String.format("%.2f", percentage) + "%");
        return percentage;
    }


    public List<NotificationAcknowledgment> getUserNotifications(int userId) {
        List<NotificationAcknowledgment> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_acknowledgments WHERE user_id = ? " +
                "ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + notifications.size() +
                    " notifications for user ID: " + userId);
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching notifications for user: " + userId, e);
        }
        return notifications;
    }


    private NotificationAcknowledgment extractNotificationFromResultSet(ResultSet rs)
            throws SQLException {
        NotificationAcknowledgment notif = new NotificationAcknowledgment();
        notif.setId(rs.getInt("id"));
        notif.setUserId(rs.getInt("user_id"));
        notif.setDisasterId(rs.getInt("disaster_id"));
        notif.setUsername(rs.getString("username"));
        notif.setAlertType(rs.getString("alert_type"));
        notif.setStatus(rs.getString("status"));
        notif.setTimestamp(rs.getString("timestamp"));
        return notif;
    }
}