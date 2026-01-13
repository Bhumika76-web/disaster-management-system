package database;
import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/disaster_management";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTablesIfNotExist() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "phone VARCHAR(15) NOT NULL," +
                    "location VARCHAR(100) NOT NULL," +
                    "user_type VARCHAR(20) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createUsersTable);

            String createDisastersTable = "CREATE TABLE IF NOT EXISTS disasters (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "type VARCHAR(50) NOT NULL," +
                    "location VARCHAR(100) NOT NULL," +
                    "severity INT NOT NULL," +
                    "description TEXT," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "status VARCHAR(20) DEFAULT 'active')";
            stmt.execute(createDisastersTable);

            String createHelpRequestsTable = "CREATE TABLE IF NOT EXISTS help_requests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "username VARCHAR(50) NOT NULL," +
                    "description TEXT NOT NULL," +
                    "location VARCHAR(100) NOT NULL," +
                    "request_type VARCHAR(50) NOT NULL," +
                    "contact_number VARCHAR(15) NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'pending'," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
            stmt.execute(createHelpRequestsTable);

            String createResponderTasksTable = "CREATE TABLE IF NOT EXISTS responder_tasks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "responder_id INT NOT NULL," +
                    "zone VARCHAR(100) NOT NULL," +
                    "task_description TEXT NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'active'," +
                    "assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (responder_id) REFERENCES users(id))";
            stmt.execute(createResponderTasksTable);

            String createNotificationAckTable = "CREATE TABLE IF NOT EXISTS notification_acknowledgments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "disaster_id INT NOT NULL," +
                    "username VARCHAR(50) NOT NULL," +
                    "alert_type VARCHAR(50) NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'acknowledged'," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (disaster_id) REFERENCES disasters(id))";
            stmt.execute(createNotificationAckTable);

            String createDisasterReportsTable = "CREATE TABLE IF NOT EXISTS disaster_reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "username VARCHAR(50) NOT NULL," +
                    "disaster_type VARCHAR(50) NOT NULL," +
                    "location VARCHAR(100) NOT NULL," +
                    "description TEXT NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'pending'," +
                    "estimated_severity INT DEFAULT 5," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
            stmt.execute(createDisasterReportsTable);

            System.out.println("✓ Database tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}