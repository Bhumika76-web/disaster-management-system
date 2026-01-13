package models;

public class NotificationAcknowledgment {
    private int id;
    private int userId;
    private int disasterId;
    private String username;
    private String alertType;
    private String status;
    private String timestamp;

    public NotificationAcknowledgment() {}

    public NotificationAcknowledgment(int userId, int disasterId, String username, String alertType, String status) {
        this.userId = userId;
        this.disasterId = disasterId;
        this.username = username;
        this.alertType = alertType;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDisasterId() { return disasterId; }
    public void setDisasterId(int disasterId) { this.disasterId = disasterId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}