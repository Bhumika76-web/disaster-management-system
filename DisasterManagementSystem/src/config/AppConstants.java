package config;

public class AppConstants {

    public static final String APP_NAME = "Disaster Management & Alert System";
    public static final String APP_VERSION = "1.0.0";
    public static final String ORGANIZATION = "Emergency Services";


    public static final String DB_URL = "jdbc:mysql://localhost:3306/disaster_management";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";


    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int PHONE_LENGTH = 10;
    public static final int MIN_SEVERITY = 1;
    public static final int MAX_SEVERITY = 10;
    public static final int HIGH_RISK_THRESHOLD = 7;


    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 800;
    public static final int BORDER_RADIUS = 15;
    public static final int PADDING = 20;
    public static final int SPACING = 30;


    public static final String USER_TYPE_ADMIN = "admin";
    public static final String USER_TYPE_RESPONDER = "responder";
    public static final String USER_TYPE_CITIZEN = "citizen";


    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_RESOLVED = "resolved";
    public static final String STATUS_COMPLETED = "completed";
}