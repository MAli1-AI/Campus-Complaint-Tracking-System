package com.campuscomplaint.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Complaint {
    private String complaintId;
    private String userId;
    private ComplaintCategory category;
    private int urgencyLevel;
    private LocalDateTime deadline;
    private String imagePath = null;
    private int statusPercent = 0;
    private String resolverId = "unassigned";
    private String title;
    private String description;
    private int feedback = -1;
    private LocalDateTime timestamp;
    private LocalDateTime resolvedTimestamp;
    // New fields for manager-resolver communication
    private String managerQuery = "";
    private String resolverResponse = "";

    public Complaint(String complaintId, String userId, ComplaintCategory category, String title, String description, int urgencyLevel) {
        if (complaintId == null || userId == null || category == null || title == null || description == null || urgencyLevel <= 0 || urgencyLevel > 5) {
            throw new IllegalArgumentException("Invalid Details!");
        }
        this.complaintId = complaintId;
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.urgencyLevel = urgencyLevel;
        this.timestamp = LocalDateTime.now();
        calculateAndSetDeadline();
    }

    // Getters and Setters (existing and new)

    public String getManagerQuery() { return managerQuery; }
    public void setManagerQuery(String managerQuery) { this.managerQuery = managerQuery; }

    public String getResolverResponse() { return resolverResponse; }
    public void setResolverResponse(String resolverResponse) { this.resolverResponse = resolverResponse; }

    public LocalDateTime getResolvedTimestamp() { return resolvedTimestamp; }
    public void setResolvedTimestamp(LocalDateTime resolvedTimestamp) { this.resolvedTimestamp = resolvedTimestamp; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(int urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getStatusPercent() { return statusPercent; }
    public void setStatusPercent(int statusPercent) { this.statusPercent = statusPercent; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getResolverId() { return resolverId; }
    public void setResolverId(String resolverId) { this.resolverId = resolverId; }

    public boolean isOverdue() {
        return statusPercent < 100 && deadline != null && deadline.isBefore(LocalDateTime.now());
    }

    public void calculateAndSetDeadline() {
        int priority = category.calculatePriority(this);
        long hours;
        if (priority <= 4) hours = 168;
        else if (priority <= 7) hours = 72;
        else if (priority <= 10) hours = 24;
        else if (priority <= 13) hours = 12;
        else if (priority <= 16) hours = 6;
        else if (priority <= 19) hours = 3;
        else hours = 1;
        this.deadline = LocalDateTime.now().plusHours(hours);
    }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public long getResolutionTimeInHours() {
        if (statusPercent == 100 && resolvedTimestamp != null) {
            return ChronoUnit.HOURS.between(timestamp, resolvedTimestamp);
        }
        return -1;
    }

    public void setFeedback(int feedback) {
        if (statusPercent == 100 && feedback >= 0 && feedback <= 100) {
            this.feedback = feedback;
        } else if (statusPercent != 100) {
            throw new IllegalStateException("Cannot give feedback: Complaint not resolved yet.");
        } else {
            throw new IllegalArgumentException("Feedback must be between 0 and 100.");
        }
    }

    public int getFeedback() { return feedback; }
    public void setFeedbackForFileLoad(int feedback) {
        this.feedback = feedback;
    }
}