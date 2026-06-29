package com.campuscomplaint;

import com.campuscomplaint.ui.MainFrame;
import com.campuscomplaint.ui.SplashScreen;
import com.campuscomplaint.model.*;
import com.campuscomplaint.model.FileHandler;
import javax.swing.*;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        // Set System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Ensure data folder exists
        ensureDataFolder();
        
        // Initialize default data if needed
        initializeDefaultData();
        
        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            SplashScreen splash = new SplashScreen();
            splash.showSplash(() -> mainFrame.setVisible(true));
        });
    }
    
    private static void ensureDataFolder() {
        java.io.File dataFolder = new java.io.File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
            System.out.println("Created data folder");
        }
    }
    
    private static void initializeDefaultData() {
        List<User> users = FileHandler.readUsers();
        
        // If no users exist, create default accounts
        if (users.isEmpty()) {
            System.out.println("Initializing default data...");
            
            // Default Manager
            Manager defaultManager = new Manager("MGR-01", "Admin Overseer", "admin123");
            users.add(defaultManager);
            
            // Default Resolvers (5 categories)
            users.add(new Resolver("RES-IT", "IT Support", "respass", new ITCategory()));
            users.add(new Resolver("RES-ACAD", "Academic Support", "respass", new AcademicCategory()));
            users.add(new Resolver("RES-ADMIN", "Admin Support", "respass", new AdministrativeCategory()));
            users.add(new Resolver("RES-INFRA", "Infrastructure Support", "respass", new InfrastructureCategory()));
            users.add(new Resolver("RES-LIB", "Library Support", "respass", new LibraryCategory()));
            users.add(new Resolver("RES-GEN", "General Support", "respass", null));
            
            // Default Student
            users.add(new Student("STU-001", "Ali Student", "pass123", "Computer Science", 3));
            users.add(new Student("STU-002", "Sara Khan", "pass123", "Software Engineering", 5));
            
            // Default Professor
            users.add(new Professor("PROF-001", "Dr. Ahmed", "pass123", "Computer Science", "Associate Professor"));
            
            // Default Staff
            users.add(new StaffMember("STF-001", "John Staff", "pass123", "Admin Office"));
            
            FileHandler.writeUsers(users);
            System.out.println("Default accounts created successfully!");
        }
    }
}