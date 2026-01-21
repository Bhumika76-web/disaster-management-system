package dao;

import models.Disaster;
import database.DatabaseConnection;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisasterDAO {
    private static final String CLASS_NAME = "DisasterDAO";


    public boolean addDisaster(Disaster disaster) {
        String query = "INSERT INTO disasters (type, location, severity, description, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, disaster.getType());
            pstmt.setString(2, disaster.getLocation());
            pstmt.setInt(3, disaster.getSeverity());
            pstmt.setString(4, disaster.getDescription());
            pstmt.setString(5, disaster.getStatus());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Disaster added: " + disaster.getType() +
                        " at " + disaster.getLocation());
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to add disaster", e);
        }
        return false;
    }


    public List<Disaster> getAllDisasters() {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                disasters.add(extractDisasterFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + disasters.size() + " disasters");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching all disasters", e);
        }
        return disasters;
    }


    public List<Disaster> getActiveDisasters() {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters WHERE status = 'active' ORDER BY severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                disasters.add(extractDisasterFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + disasters.size() + " active disasters");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching active disasters", e);
        }
        return disasters;
    }


    public boolean updateDisasterStatus(int id, String newStatus) {
        String query = "UPDATE disasters SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "Disaster status updated: ID=" + id +
                        ", Status=" + newStatus);
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to update disaster status", e);
        }
        return false;
    }


    public List<Disaster> searchByLocation(String location) {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters WHERE location LIKE ? ORDER BY severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + location + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disasters.add(extractDisasterFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Search found " + disasters.size() +
                    " disasters at location: " + location);
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error searching disasters by location", e);
        }
        return disasters;
    }


    public List<Disaster> filterDisastersByTypeAndLocation(String type, String location) {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters WHERE type = ? AND location = ? " +
                "AND status = 'active' ORDER BY severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);
            pstmt.setString(2, location);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disasters.add(extractDisasterFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error filtering disasters", e);
        }
        return disasters;
    }


    public List<Disaster> getDisastersByLocation(String location) {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters WHERE location = ? AND status = 'active' " +
                "ORDER BY severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, location);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disasters.add(extractDisasterFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching disasters by location", e);
        }
        return disasters;
    }


    private Disaster extractDisasterFromResultSet(ResultSet rs) throws SQLException {
        Disaster disaster = new Disaster();
        disaster.setId(rs.getInt("id"));
        disaster.setType(rs.getString("type"));
        disaster.setLocation(rs.getString("location"));
        disaster.setSeverity(rs.getInt("severity"));
        disaster.setDescription(rs.getString("description"));
        disaster.setTimestamp(rs.getString("timestamp"));
        disaster.setStatus(rs.getString("status"));
        return disaster;
    }
}