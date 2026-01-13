package models;

public class Disaster {
    private int id;
    private String type;
    private String location;
    private int severity;
    private String description;
    private String timestamp;
    private String status;

    public Disaster() {}

    public Disaster(String type, String location, int severity,
                    String description, String timestamp) {
        this.type = type;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.timestamp = timestamp;
        this.status = "active";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
