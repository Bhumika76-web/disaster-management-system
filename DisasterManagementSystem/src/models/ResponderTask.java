package models;

public class ResponderTask {
    private int id;
    private int responderId;
    private String zone;
    private String taskDescription;
    private String status;
    private String assignedAt;

    public ResponderTask() {}

    public ResponderTask(int responderId, String zone, String taskDescription) {
        this.responderId = responderId;
        this.zone = zone;
        this.taskDescription = taskDescription;
        this.status = "active";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResponderId() { return responderId; }
    public void setResponderId(int responderId) { this.responderId = responderId; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedAt() { return assignedAt; }
    public void setAssignedAt(String assignedAt) { this.assignedAt = assignedAt; }
}