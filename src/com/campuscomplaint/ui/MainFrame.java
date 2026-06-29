package com.campuscomplaint.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;

    public MainFrame() {
        setTitle("Campus Complaint Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 550));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(signUpPanel, "SIGNUP");

        add(mainPanel);
    }

    public void showLogin() {
        loginPanel.refresh();
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showSignUp() {
        cardLayout.show(mainPanel, "SIGNUP");
    }

    public void showDashboard(String role, String userId, String name) {
        // Remove previous dashboard if exists
        Component[] comps = mainPanel.getComponents();
        for (Component c : comps) {
            if (c.getName() != null && c.getName().equals("DASHBOARD")) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel dashboard = null;
        switch (role.toLowerCase()) {
            case "student":
                dashboard = new StudentDashboard(this, userId, name);
                break;
            case "professor":
                dashboard = new ProfessorDashboard(this, userId, name);
                break;
            case "staff":
                dashboard = new StaffDashboard(this, userId, name);
                break;
            case "manager":
                dashboard = new ManagerDashboard(this, userId, name);
                break;
            case "resolver":
                dashboard = new ResolverDashboard(this, userId, name);
                break;
        }
        if (dashboard != null) {
            dashboard.setName("DASHBOARD");
            mainPanel.add(dashboard, "DASHBOARD");
            cardLayout.show(mainPanel, "DASHBOARD");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            SplashScreen splash = new SplashScreen();
            splash.showSplash(() -> frame.setVisible(true));
        });
    }
}