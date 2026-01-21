package dao;

import models.HelpRequest;
import database.DatabaseConnection;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HelpRequestDAO {
    private static final String CLASS_NAME = "HelpRequestDAO";


    public boolean submitHelpRequest(HelpRequest request) {
        String query = "INSERT INTO help_requests (user_id, username, description, location, " +
                "request_type, contact_number, status, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, request.getUserId());
            pstmt.setString(2, request.getUsername());
            pstmt.setString(3, request.getDescription());
            pstmt.setString(4, request.getLocation());
            pstmt.setString(5, request.getRequestType());
            pstmt.setString(6, request.getContactNumber());
            pstmt.setString(7, request.getStatus());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Help request submitted by: " + request.getUsername());
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to submit help request", e);
        }
        return false;
    }


    public List<HelpRequest> getAllHelpRequests() {
        List<HelpRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM help_requests ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                requests.add(extractRequestFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + requests.size() + " help requests");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching all help requests", e);
        }
        return requests;
    }


    public List<HelpRequest> getPendingHelpRequests() {
        List<HelpRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM help_requests WHERE status = 'pending' ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                requests.add(extractRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching pending requests", e);
        }
        return requests;
    }


    public List<HelpRequest> getUserHelpRequests(int userId) {
        List<HelpRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM help_requests WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(extractRequestFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + requests.size() +
                    " help requests for user ID: " + userId);
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching user help requests for user: " + userId, e);
        }
        return requests;
    }


    public boolean updateRequestStatus(int requestId, String newStatus) {
        String query = "UPDATE help_requests SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Help request status updated: ID=" + requestId +
                        ", Status=" + newStatus);
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to update help request status", e);
        }
        return false;
    }


    private HelpRequest extractRequestFromResultSet(ResultSet rs) throws SQLException {
        HelpRequest request = new HelpRequest();
        request.setId(rs.getInt("id"));
        request.setUserId(rs.getInt("user_id"));
        request.setUsername(rs.getString("username"));
        request.setDescription(rs.getString("description"));
        request.setLocation(rs.getString("location"));
        request.setRequestType(rs.getString("request_type"));
        request.setContactNumber(rs.getString("contact_number"));
        request.setStatus(rs.getString("status"));
        request.setTimestamp(rs.getString("timestamp"));
        return request;
    }
}