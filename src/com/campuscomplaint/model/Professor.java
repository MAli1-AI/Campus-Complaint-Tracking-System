package com.campuscomplaint.model;

import com.campuscomplaint.service.ComplaintService;
import java.util.List;

public class Professor extends User implements IComplaint {
    private String department;
    private String designation;

    public Professor(String userId, String name, String password, String department, String designation) {
        super(userId, name, password, "Professor");
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        this.department = department;
        this.designation = designation;
    }

    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }

    @Override
    public boolean login(String id, String password) {
        return this.userId.equals(id) && this.password.equals(password);
    }

    @Override
    public void submitComplaint(String title, String description, ComplaintCategory category, int urgencyLevel, String imagePath) {
        if (title == null || title.trim().length() < 5) {
            throw new IllegalArgumentException("Title must be at least 5 characters");
        }
        if (description == null || description.trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters");
        }
        if (urgencyLevel < 1 || urgencyLevel > 5) {
            throw new IllegalArgumentException("Urgency level must be 1-5");
        }

        List<Complaint> existing = ComplaintService.getAllComplaints();
        String complaintId = FileHandler.generateComplaintId("PROF", category, existing);

        Complaint complaint = new Complaint(complaintId, this.userId, category, title, description, urgencyLevel);
        if (imagePath != null) complaint.setImagePath(imagePath);
        ComplaintService.submitComplaint(complaint);
    }

    @Override
    public List<Complaint> viewMyComplaints() {
        return ComplaintService.getComplaintsByUser(this.userId);
    }

    @Override
    public void provideFeedback(String complaintId, int feedbackPercentage) {
        ComplaintService.giveFeedback(complaintId, feedbackPercentage, this.userId);
    }

    @Override
    public String fileFormat() {
        return userId + "," + name + "," + password + ",Professor," + department + "," + designation;
    }
}