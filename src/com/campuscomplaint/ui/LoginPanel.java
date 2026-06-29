package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.model.FileHandler;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginPanel extends JPanel {
    private MainFrame parent;
    private JComboBox<String> roleCombo;
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    
    public LoginPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);
        
        TopStripPanel topStrip = new TopStripPanel("Login");
        add(topStrip, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo = new JComboBox<>(new String[]{"Manager", "Resolver", "Complainant"});
        
        JLabel userLabel = new JLabel("User ID:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userIdField = new JTextField(20);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField = new JPasswordField(20);
        
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        
        JButton loginBtn = new JButton("Login");
        ColorScheme.styleButton(loginBtn);
        loginBtn.addActionListener(e -> performLogin());
        
        JButton signUpBtn = new JButton("New User? Sign Up");
        signUpBtn.addActionListener(e -> parent.showSignUp());
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(roleLabel, gbc);
        gbc.gridx = 1; panel.add(roleCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(userLabel, gbc);
        gbc.gridx = 1; panel.add(userIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(passLabel, gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(errorLabel, gbc);
        gbc.gridy = 4; panel.add(loginBtn, gbc);
        gbc.gridy = 5; panel.add(signUpBtn, gbc);
        
        return panel;
    }
    
    private void performLogin() {
        String role = (String) roleCombo.getSelectedItem();
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (userId.isEmpty()) {
            errorLabel.setText("Please enter User ID");
            return;
        }
        
        List<User> users = FileHandler.readUsers();
        for (User user : users) {
            if (user.getUserId().equals(userId) && user.login(userId, password)) {
                String userRole = user.getRole();
                if (role.equals("Manager") && !userRole.equals("Manager")) {
                    errorLabel.setText("Invalid role selection for this user");
                    return;
                }
                if (role.equals("Resolver") && !userRole.equals("Resolver")) {
                    errorLabel.setText("Invalid role selection for this user");
                    return;
                }
                if (role.equals("Complainant") && (userRole.equals("Manager") || userRole.equals("Resolver"))) {
                    errorLabel.setText("Invalid role selection for this user");
                    return;
                }
                parent.showDashboard(userRole, userId, user.getName());
                return;
            }
        }
        errorLabel.setText("Invalid User ID or Password");
    }
    
    public void refresh() {
        userIdField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}