package com.campuscomplaint.model;

import java.util.List;

public interface IComplaint {
    void submitComplaint(String title, String description, ComplaintCategory category, int urgencyLevel, String imagePath);
    List<Complaint> viewMyComplaints();
    void provideFeedback(String complaintId, int feedbackPercentage);
}