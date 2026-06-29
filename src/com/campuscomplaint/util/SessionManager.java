package com.campuscomplaint.util;

import com.campuscomplaint.model.User;
import com.campuscomplaint.model.FileHandler;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private String currentRole;
    private String currentUserId;
    private String currentUserName;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public boolean login(String userId, String password) {
        for (User user : FileHandler.readUsers()) {
            if (user.getUserId().equals(userId) && user.login(userId, password)) {
                currentUser = user;
                currentRole = user.getRole();
                currentUserId = user.getUserId();
                currentUserName = user.getName();
                return true;
            }
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
        currentRole = null;
        currentUserId = null;
        currentUserName = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public String getCurrentRole() {
        return currentRole;
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    public String getCurrentUserName() {
        return currentUserName;
    }
    
    public boolean isManager() {
        return "Manager".equals(currentRole);
    }
    
    public boolean isResolver() {
        return "Resolver".equals(currentRole);
    }
    
    public boolean isComplainant() {
        return "Student".equals(currentRole) || "Professor".equals(currentRole) || "Staff".equals(currentRole);
    }
    
    public boolean isStudent() {
        return "Student".equals(currentRole);
    }
    
    public boolean isProfessor() {
        return "Professor".equals(currentRole);
    }
    
    public boolean isStaff() {
        return "Staff".equals(currentRole);
    }
}