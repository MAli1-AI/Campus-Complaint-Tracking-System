package com.campuscomplaint.model;

import com.campuscomplaint.service.ComplaintService;
import com.campuscomplaint.model.FileHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Manager extends User {

    public Manager(String userId, String name, String password) {
        super(userId, name, password, "Manager");
    }

    public List<Complaint> viewAllComplaints() {
        return ComplaintService.getAllComplaints();
    }

    public Map<String, Double> viewResolverPerformance() {
        return ComplaintService.getResolverPerformanceReport();
    }

    public Map<String, Long> viewComplaintStatistics() {
        List<Complaint> all = ComplaintService.getAllComplaints();
        return all.stream().collect(Collectors.groupingBy(
            c -> c.getCategory().getCategoryType(),
            Collectors.counting()
        ));
    }

    public void addResolver(String userId, String name, String password, String expertise) {
        List<User> users = FileHandler.readUsers();
        for (User u : users) {
            if (u.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Resolver ID already exists");
            }
        }
        ComplaintCategory specializedCategory = null;
        if (expertise.equalsIgnoreCase("IT")) {
            specializedCategory = new ITCategory();
        } else if (expertise.equalsIgnoreCase("Academic")) {
            specializedCategory = new AcademicCategory();
        } else if (expertise.equalsIgnoreCase("Administrative")) {
            specializedCategory = new AdministrativeCategory();
        } else if (expertise.equalsIgnoreCase("Infrastructure")) {
            specializedCategory = new InfrastructureCategory();
        } else if (expertise.equalsIgnoreCase("Library")) {
            specializedCategory = new LibraryCategory();
        }
        Resolver newResolver = new Resolver(userId, name, password, specializedCategory);
        users.add(newResolver);
        FileHandler.writeUsers(users);
    }

    @Override
    public boolean login(String id, String password) {
        return this.userId.equals(id) && this.password.equals(password);
    }

    @Override
    public String fileFormat() {
        return userId + "," + name + "," + password + ",Manager";
    }
}