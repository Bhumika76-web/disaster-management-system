package dao;

import models.DisasterReport;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisasterReportDAO {

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

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error submitting report: " + e.getMessage());
            return false;
        }
    }

    public List<DisasterReport> getAllReports() {
        List<DisasterReport> reports = new ArrayList<>();
        String query = "SELECT * FROM disaster_reports ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
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
                reports.add(report);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reports: " + e.getMessage());
        }
        return reports;
    }

    public List<DisasterReport> getPendingReports() {
        List<DisasterReport> reports = new ArrayList<>();
        String query = "SELECT * FROM disaster_reports WHERE status = 'pending' ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
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
                reports.add(report);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending reports: " + e.getMessage());
        }
        return reports;
    }

    public List<DisasterReport> getHighRiskAreas() {
        List<DisasterReport> reports = new ArrayList<>();
        String query = "SELECT * FROM disaster_reports WHERE status IN ('pending', 'investigating') " +
                "AND estimated_severity >= 5 ORDER BY estimated_severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DisasterReport report = new DisasterReport();
                report.setId(rs.getInt("id"));
                report.setLocation(rs.getString("location"));
                report.setDisasterType(rs.getString("disaster_type"));
                report.setEstimatedSeverity(rs.getInt("estimated_severity"));
                reports.add(report);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching high risk areas: " + e.getMessage());
        }
        return reports;
    }

    public boolean updateReportStatus(int reportId, String newStatus) {
        String query = "UPDATE disaster_reports SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, reportId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating report status: " + e.getMessage());
            return false;
        }
    }
}