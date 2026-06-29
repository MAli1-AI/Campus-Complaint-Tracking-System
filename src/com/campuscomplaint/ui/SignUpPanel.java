package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.model.FileHandler;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SignUpPanel extends JPanel {
    private MainFrame parent;
    private JComboBox<String> userTypeCombo;
    private JTextField userIdField, nameField, deptField, semesterField, designationField, officeField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JPanel dynamicPanel;
    
    public SignUpPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);
        
        TopStripPanel topStrip = new TopStripPanel("Sign Up");
        add(topStrip, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> parent.showLogin());
        add(backBtn, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        
        JLabel typeLabel = new JLabel("User Type:");
        userTypeCombo = new JComboBox<>(new String[]{"Student", "Professor", "Staff"});
        userTypeCombo.addActionListener(e -> updateDynamicFields());
        
        JLabel idLabel = new JLabel("User ID:");
        userIdField = new JTextField(20);
        
        JLabel nameLabel = new JLabel("Full Name:");
        nameField = new JTextField(20);
        
        JLabel passLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        dynamicPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        dynamicPanel.setBackground(ColorScheme.BACKGROUND);
        
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        
        JButton signUpBtn = new JButton("Create Account");
        ColorScheme.styleButton(signUpBtn);
        signUpBtn.addActionListener(e -> performSignUp());
        
        updateDynamicFields();
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(typeLabel, gbc);
        gbc.gridx = 1; panel.add(userTypeCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(idLabel, gbc);
        gbc.gridx = 1; panel.add(userIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(nameLabel, gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(passLabel, gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(dynamicPanel, gbc);
        gbc.gridy = 5; panel.add(errorLabel, gbc);
        gbc.gridy = 6; panel.add(signUpBtn, gbc);
        
        return panel;
    }
    
    private void updateDynamicFields() {
        dynamicPanel.removeAll();
        String type = (String) userTypeCombo.getSelectedItem();
        if ("Student".equals(type)) {
            dynamicPanel.add(new JLabel("Department:"));
            deptField = new JTextField(15);
            dynamicPanel.add(deptField);
            dynamicPanel.add(new JLabel("Semester (1-8):"));
            semesterField = new JTextField(15);
            dynamicPanel.add(semesterField);
            designationField = null;
            officeField = null;
        } else if ("Professor".equals(type)) {
            dynamicPanel.add(new JLabel("Department:"));
            deptField = new JTextField(15);
            dynamicPanel.add(deptField);
            dynamicPanel.add(new JLabel("Designation:"));
            designationField = new JTextField(15);
            dynamicPanel.add(designationField);
            semesterField = null;
            officeField = null;
        } else {
            dynamicPanel.add(new JLabel("Office Location:"));
            officeField = new JTextField(15);
            dynamicPanel.add(officeField);
            deptField = null;
            semesterField = null;
            designationField = null;
        }
        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }
    
    private void performSignUp() {
        String type = (String) userTypeCombo.getSelectedItem();
        String userId = userIdField.getText().trim();
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (userId.isEmpty() || name.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }
        
        List<User> users = FileHandler.readUsers();
        for (User u : users) {
            if (u.getUserId().equals(userId)) {
                errorLabel.setText("User ID already exists! Please login.");
                return;
            }
        }
        
        try {
            User newUser = null;
            if ("Student".equals(type)) {
                int semester = Integer.parseInt(semesterField.getText().trim());
                newUser = new Student(userId, name, password, deptField.getText().trim(), semester);
            } else if ("Professor".equals(type)) {
                newUser = new Professor(userId, name, password, deptField.getText().trim(), designationField.getText().trim());
            } else {
                newUser = new StaffMember(userId, name, password, officeField.getText().trim());
            }
            users.add(newUser);
            FileHandler.writeUsers(users);
            JOptionPane.showMessageDialog(this, "Account created successfully! Please login.");
            parent.showLogin();
        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }
}