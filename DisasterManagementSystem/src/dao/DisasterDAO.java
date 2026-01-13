package dao;

import models.Disaster;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisasterDAO {
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

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding disaster: " + e.getMessage());
            return false;
        }
    }

    public List<Disaster> getAllDisasters() {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Disaster disaster = new Disaster();
                disaster.setId(rs.getInt("id"));
                disaster.setType(rs.getString("type"));
                disaster.setLocation(rs.getString("location"));
                disaster.setSeverity(rs.getInt("severity"));
                disaster.setDescription(rs.getString("description"));
                disaster.setTimestamp(rs.getString("timestamp"));
                disaster.setStatus(rs.getString("status"));
                disasters.add(disaster);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching disasters: " + e.getMessage());
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
                Disaster disaster = new Disaster();
                disaster.setId(rs.getInt("id"));
                disaster.setType(rs.getString("type"));
                disaster.setLocation(rs.getString("location"));
                disaster.setSeverity(rs.getInt("severity"));
                disaster.setDescription(rs.getString("description"));
                disaster.setTimestamp(rs.getString("timestamp"));
                disaster.setStatus(rs.getString("status"));
                disasters.add(disaster);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching active disasters: " + e.getMessage());
        }
        return disasters;
    }

    public boolean updateDisasterStatus(int id, String newStatus) {
        String query = "UPDATE disasters SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating disaster: " + e.getMessage());
            return false;
        }
    }

    public List<Disaster> searchByLocation(String location) {
        List<Disaster> disasters = new ArrayList<>();
        String query = "SELECT * FROM disasters WHERE location LIKE ? " +
                "ORDER BY severity DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + location + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Disaster d = new Disaster();
                d.setId(rs.getInt("id"));
                d.setType(rs.getString("type"));
                d.setLocation(rs.getString("location"));
                d.setSeverity(rs.getInt("severity"));
                d.setDescription(rs.getString("description"));
                d.setTimestamp(rs.getString("timestamp"));
                d.setStatus(rs.getString("status"));
                disasters.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Error searching disasters: " + e.getMessage());
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
                Disaster d = new Disaster();
                d.setId(rs.getInt("id"));
                d.setType(rs.getString("type"));
                d.setLocation(rs.getString("location"));
                d.setSeverity(rs.getInt("severity"));
                d.setDescription(rs.getString("description"));
                d.setTimestamp(rs.getString("timestamp"));
                d.setStatus(rs.getString("status"));
                disasters.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Error filtering disasters: " + e.getMessage());
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
                Disaster d = new Disaster();
                d.setId(rs.getInt("id"));
                d.setType(rs.getString("type"));
                d.setLocation(rs.getString("location"));
                d.setSeverity(rs.getInt("severity"));
                d.setDescription(rs.getString("description"));
                d.setTimestamp(rs.getString("timestamp"));
                d.setStatus(rs.getString("status"));
                disasters.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching disasters by location: " + e.getMessage());
        }
        return disasters;
    }
}
