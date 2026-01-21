package dao;

import models.User;
import database.DatabaseConnection;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String CLASS_NAME = "UserDAO";


    public boolean registerUser(User user) {
        String query = "INSERT INTO users (username, email, password, phone, location, user_type) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getLocation());
            pstmt.setString(6, user.getUserType());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                Logger.info(CLASS_NAME, "User registered successfully: " + user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Failed to register user: " + user.getUsername(), e);
        }
        return false;
    }


    public User loginUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Logger.info(CLASS_NAME, "User logged in: " + email);
                return extractUserFromResultSet(rs);
            }

            Logger.warn(CLASS_NAME, "Login failed for email: " + email);
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error during login for: " + email, e);
        }
        return null;
    }


    public List<User> getAllResponders() {
        List<User> responders = new ArrayList<>();
        String query = "SELECT * FROM users WHERE user_type = 'responder'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                responders.add(extractUserFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + responders.size() + " responders");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching responders", e);
        }
        return responders;
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }

            Logger.debug(CLASS_NAME, "Retrieved " + users.size() + " users");
        } catch (SQLException e) {
            Logger.error(CLASS_NAME, "Error fetching all users", e);
        }
        return users;
    }


    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setLocation(rs.getString("location"));
        user.setUserType(rs.getString("user_type"));
        return user;
    }
}

