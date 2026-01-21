package dao;

import models.DisasterReport;
import database.DatabaseConnection;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisasterReportDAO {
    private static final String CLASS_NAME = "DisasterReportDAO";


    public boolean submitReport(DisasterReport report) {
        String query = "INSERT INTO disaster_reports (user_id, username, disaster_type, location, " +
                "description, status, estimated_severity) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, report.getUserId());
            pstmt.setString(2, report.getUsername());
            pstmt.setString(3, report.getDisasterType());
            pstmt.setString(4, report.getLocation());
            pstmt.setString(5, report.getDescription());
            pstmt.setString(6, report.getStatus());
            pstmt.setInt(7, report.getEstimatedSeverity());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Disaster report submitted by: " +
                        report.getUsername() + " at " + report.getLocation());
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to submit disaster report", e);
        }
        return false;
    }


    public List<DisasterReport> getAllReports() {
        List<DisasterReport> reports = new ArrayList<>();
        String query = "SELECT * FROM disaster_reports ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + reports.size() + " disaster reports");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching all disaster reports", e);
        }
        return reports;
    }


    public List<DisasterReport> getPendingReports() {
        List<DisasterReport> reports = new ArrayList<>();
        String query = "SELECT * FROM disaster_reports WHERE status = 'pending' " +
                "ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + reports.size() + " pending reports");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching pending reports", e);
        }
        return reports;
    }


    public List<DisasterReport> getHighRiskAreas() {
        List<DisasterReport> reports = new ArrayList<>();
        String query = "SELECT * FROM disaster_reports WHERE status IN ('pending', 'investigating') " +
                "AND estimated_severity >= 7 ORDER BY estimated_severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }

            Logger.info(CLASS_NAME, "Found " + reports.size() + " high-risk areas");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching high-risk areas", e);
        }
        return reports;
    }


    public boolean updateReportStatus(int reportId, String newStatus) {
        String query = "UPDATE disaster_reports SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, reportId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Report status updated: ID=" + reportId +
                        ", Status=" + newStatus);
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to update report status", e);
        }
        return false;
    }


    private DisasterReport extractReportFromResultSet(ResultSet rs) throws SQLException {
        DisasterReport report = new DisasterReport();
        report.setId(rs.getInt("id"));
        report.setUserId(rs.getInt("user_id"));
        report.setUsername(rs.getString("username"));
        report.setDisasterType(rs.getString("disaster_type"));
        report.setLocation(rs.getString("location"));
        report.setDescription(rs.getString("description"));
        report.setStatus(rs.getString("status"));
        report.setTimestamp(rs.getString("timestamp"));
        report.setEstimatedSeverity(rs.getInt("estimated_severity"));
        return report;
    }
}