package com.campuscomplaint.model;

import com.campuscomplaint.service.ComplaintService;
import java.util.ArrayList;
import java.util.List;

public class Resolver extends User {
    private ComplaintCategory specializedCategory;
    private List<Integer> taskQueue;
    private boolean flagged = false;

    public Resolver(String userId, String name, String password, ComplaintCategory specializedCategory) {
        super(userId, name, password, "Resolver");
        this.specializedCategory = specializedCategory;
        this.taskQueue = new ArrayList<>();
    }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public ComplaintCategory getSpecializedCategory() { return specializedCategory; }
    public void setSpecializedCategory(ComplaintCategory cat) { this.specializedCategory = cat; }

    public boolean canHandle(ComplaintCategory category) {
        if (specializedCategory == null) return true;
        return specializedCategory.getCategoryType().equals(category.getCategoryType());
    }

    public List<Complaint> viewAssignedComplaints() {
        return ComplaintService.getComplaintsByResolver(this.userId);
    }

    public void updateStatus(String complaintId, int newStatus) {
        ComplaintService.updateComplaintStatus(complaintId, newStatus, this.userId);
    }

    public double getAverageResolutionTime() {
        return ComplaintService.getAverageResolutionTimeForResolver(this.userId);
    }

    public double getAverageRating() {
        return ComplaintService.getAverageFeedbackForResolver(this.userId);
    }

    public void assignTask(int complaintId) {
        taskQueue.add(complaintId);
    }

    public void completeTask(int complaintId) {
        taskQueue.remove(Integer.valueOf(complaintId));
    }

    public int getTaskCount() {
        return taskQueue.size();
    }

    public String getExpertise() {
        if (specializedCategory == null) return "General";
        String type = specializedCategory.getCategoryType();
        if (type.equalsIgnoreCase("IT")) return "IT Support";
        return type;
    }

    @Override
    public boolean login(String id, String password) {
        return this.userId.equals(id) && this.password.equals(password);
    }

    @Override
    public String fileFormat() {
        String catName = (specializedCategory == null) ? "General" : specializedCategory.getCategoryType();
        return userId + "," + name + "," + password + ",Resolver," + catName + "," + flagged;
    }
}