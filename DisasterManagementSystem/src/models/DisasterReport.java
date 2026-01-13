package models;

public class DisasterReport {
    private int id;
    private int userId;
    private String username;
    private String disasterType;
    private String location;
    private String description;
    private String status;
    private String timestamp;
    private int estimatedSeverity;

    public DisasterReport() {}

    public DisasterReport(int userId, String username, String disasterType, String location,
                          String description, int estimatedSeverity) {
        this.userId = userId;
        this.username = username;
        this.disasterType = disasterType;
        this.location = location;
        this.description = description;
        this.estimatedSeverity = estimatedSeverity;
        this.status = "pending";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisasterType() { return disasterType; }
    public void setDisasterType(String disasterType) { this.disasterType = disasterType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getEstimatedSeverity() { return estimatedSeverity; }
    public void setEstimatedSeverity(int estimatedSeverity) { this.estimatedSeverity = estimatedSeverity; }
}