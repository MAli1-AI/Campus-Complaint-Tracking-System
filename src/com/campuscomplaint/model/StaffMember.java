package com.campuscomplaint.model;

import com.campuscomplaint.service.ComplaintService;
import java.util.List;

public class StaffMember extends User implements IComplaint {
    private String officeLocation;

    public StaffMember(String userId, String name, String password, String officeLocation) {
        super(userId, name, password, "Staff");
        if (officeLocation == null || officeLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Office location required");
        }
        this.officeLocation = officeLocation;
    }

    public String getOfficeLocation() { return officeLocation; }

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
        String complaintId = FileHandler.generateComplaintId("STAFF", category, existing);

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
        return userId + "," + name + "," + password + ",Staff," + officeLocation;
    }
}