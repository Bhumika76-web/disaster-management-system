package models;

public class HelpRequest {
    private int id;
    private int userId;
    private String username;
    private String description;
    private String location;
    private String requestType;
    private String status;
    private String timestamp;
    private String contactNumber;

    public HelpRequest() {}

    public HelpRequest(int userId, String username, String description, String location,
                       String requestType, String contactNumber) {
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.location = location;
        this.requestType = requestType;
        this.contactNumber = contactNumber;
        this.status = "pending";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}