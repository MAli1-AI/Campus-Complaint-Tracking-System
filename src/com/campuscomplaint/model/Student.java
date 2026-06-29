package com.campuscomplaint.model;

import com.campuscomplaint.service.ComplaintService;
import java.util.List;

public class Student extends User implements IComplaint {
    private String department;
    private int semester;

    public Student(String userId, String name, String password, String department, int semester) {
        super(userId, name, password, "Student");
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        }
        this.department = department;
        this.semester = semester;
    }

    public String getDepartment() { return department; }
    public int getSemester() { return semester; }

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
        String complaintId = FileHandler.generateComplaintId("STU", category, existing);

        Complaint complaint = new Complaint(complaintId, this.userId, category, title, description, urgencyLevel);
        if (imagePath != null && !imagePath.isEmpty()) {
            complaint.setImagePath(imagePath);
        }
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
        return userId + "," + name + "," + password + ",Student," + department + "," + semester;
    }
}