package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.service.ComplaintService;
import com.campuscomplaint.model.FileHandler;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SubmitComplaintDialog extends JDialog {
    private String userId;
    private Runnable onSuccess;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> issueCombo;
    private JTextField titleField;
    private JTextArea descArea;
    private JSlider urgencySlider;
    private JLabel urgencyLabel;
    private JLabel imagePreviewLabel;
    private String selectedImagePath = null;
    private ComplaintCategory[] categories;

    public SubmitComplaintDialog(JFrame parent, String userId, Runnable onSuccess) {
        super(parent, "Submit Complaint", true);
        this.userId = userId;
        this.onSuccess = onSuccess;
        setSize(550, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        categories = new ComplaintCategory[]{
            new ITCategory(), new AcademicCategory(), new AdministrativeCategory(),
            new InfrastructureCategory(), new LibraryCategory()
        };

        add(createFormPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");
        ColorScheme.styleButton(submitBtn);
        ColorScheme.styleButton(cancelBtn);

        submitBtn.addActionListener(e -> submitComplaint());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String getCurrentUserRole() {
        List<User> users = FileHandler.readUsers();
        for (User u : users) {
            if (u.getUserId().equals(userId)) {
                return u.getRole();
            }
        }
        return "Student";
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] categoryNames = {"IT", "Academic", "Administrative", "Infrastructure", "Library"};
        categoryCombo = new JComboBox<>(categoryNames);
        categoryCombo.addActionListener(e -> updateIssues());

        issueCombo = new JComboBox<>();
        updateIssues();

        titleField = new JTextField();
        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);

        urgencySlider = new JSlider(1, 5, 3);
        urgencySlider.setMajorTickSpacing(1);
        urgencySlider.setPaintTicks(true);
        urgencySlider.setPaintLabels(true);
        urgencyLabel = new JLabel("Urgency Level: 3/5");
        urgencySlider.addChangeListener(e -> urgencyLabel.setText("Urgency Level: " + urgencySlider.getValue() + "/5"));

        JButton attachBtn = new JButton("Attach Screenshot (optional)");
        ColorScheme.styleButton(attachBtn);
        attachBtn.addActionListener(e -> chooseImage());

        imagePreviewLabel = new JLabel("No image selected");
        imagePreviewLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(100, 60));

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; panel.add(categoryCombo, gbc);
        gbc.gridy = 1; panel.add(new JLabel("Issue Type:"), gbc);
        gbc.gridx = 1; panel.add(issueCombo, gbc);
        gbc.gridy = 2; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; panel.add(titleField, gbc);
        gbc.gridy = 3; panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(descArea), gbc);
        gbc.gridy = 4; panel.add(new JLabel("Urgency:"), gbc);
        gbc.gridx = 1; panel.add(urgencySlider, gbc);
        gbc.gridy = 5; panel.add(urgencyLabel, gbc);
        gbc.gridy = 6; panel.add(attachBtn, gbc);
        gbc.gridy = 7; gbc.gridwidth = 2; panel.add(imagePreviewLabel, gbc);

        return panel;
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser("screenshots");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "png", "jpg", "jpeg", "gif"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File src = chooser.getSelectedFile();
            File destDir = new File("screenshots");
            if (!destDir.exists()) destDir.mkdirs();
            String destPath = "screenshots/" + System.currentTimeMillis() + "_" + src.getName();
            try {
                Files.copy(src.toPath(), new File(destPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = destPath;
                ImageIcon icon = new ImageIcon(destPath);
                Image scaled = icon.getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaled));
                imagePreviewLabel.setText("Screenshot attached: " + src.getName());
                imagePreviewLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save image: " + ex.getMessage());
            }
        }
    }

    private void updateIssues() {
        String role = getCurrentUserRole();
        int idx = categoryCombo.getSelectedIndex();
        String[] issues = getIssuesForCategory(idx, role);
        issueCombo.removeAllItems();
        for (String issue : issues) {
            issueCombo.addItem(issue);
        }
    }

    private String[] getIssuesForCategory(int idx, String role) {
        switch (idx) {
            case 0: // IT
                if (role.equals("Student")) {
                    return new String[]{"Internet Down (8)", "LMS Not Accessible (5)", "Email Hacked (7)",
                        "Hardware Failure (4)", "Software Expired (3)", "Other (3)"};
                } else if (role.equals("Professor")) {
                    return new String[]{"LMS Server Slow (6)", "Online Exam System Crash (7)", "Research Data Loss (8)",
                        "Conference Portal Down (5)", "Online Gradebook Error (5)", "Other (3)"};
                } else {
                    return new String[]{"Email Not Working (5)", "Printer Not Working (3)", "Software License Expired (4)",
                        "VPN Connection Issue (4)", "HR Portal Down (5)", "Other (3)"};
                }
            case 1: // Academic
                if (role.equals("Student")) {
                    return new String[]{"Course Registration Blocked (5)", "Exam Conflict (4)", "Professor Unresponsive (3)",
                        "Cheating Accusation (8)", "Thesis Issue (5)", "Other (2)"};
                } else if (role.equals("Professor")) {
                    return new String[]{"Grade Dispute (6)", "Research Funding Delay (7)", "Lab Equipment Broken (5)",
                        "Plagiarism Case (8)", "Publication Journal Delay (4)", "Other (2)"};
                } else {
                    return new String[]{"Curriculum Change Request (3)", "Department Meeting Scheduling (2)",
                        "Student Record Access Issue (4)", "Exam Duty Assignment (3)", "Faculty Recruitment (2)", "Other (2)"};
                }
            case 2: // Administrative
                if (role.equals("Student")) {
                    return new String[]{"Fee Payment Error (5)", "Transcript Delay (4)", "Hostel Allotment Unfair (6)",
                        "Scholarship Not Disbursed (7)", "ID Card Not Issued (3)", "Other (2)"};
                } else if (role.equals("Professor")) {
                    return new String[]{"Salary Delay (8)", "Travel Reimbursement Pending (5)", "Research Grant Approval (7)",
                        "Promotion Application Delay (6)", "Conference Registration (4)", "Other (2)"};
                } else {
                    return new String[]{"Leave Application Delay (5)", "Overtime Payment Issue (6)", "Transfer Request (4)",
                        "Annual Increment Delay (5)", "Medical Claim Pending (4)", "Other (2)"};
                }
            case 3: // Infrastructure
                if (role.equals("Student")) {
                    return new String[]{"Fire Hazard (15)", "Power Outage (8)", "Water Shortage (7)",
                        "Elevator Broken (5)", "Broken Furniture (3)", "AC Not Working (4)", "Other (2)"};
                } else if (role.equals("Professor")) {
                    return new String[]{"Fire Hazard (15)", "Classroom AC Not Working (5)", "Projector Malfunction (4)",
                        "Office Door Lock Issue (3)", "Cabin Lighting Problem (3)", "Washroom Unclean (4)", "Other (2)"};
                } else {
                    return new String[]{"Fire Hazard (15)", "Staff Room AC Issue (4)", "Parking Space Problem (3)",
                        "Cafeteria Cleanliness (3)", "Office Furniture Broken (3)", "Water Cooler Not Working (4)", "Other (2)"};
                }
            case 4: // Library
                if (role.equals("Student")) {
                    return new String[]{"Overdue Fine Dispute (3)", "Book Missing (4)", "Reservation System Down (3)",
                        "Staff Behavior (5)", "Lost Item (2)", "Other (2)"};
                } else if (role.equals("Professor")) {
                    return new String[]{"Journal Access Denied (5)", "Reference Book Not Available (4)", "Research Section Noise (3)",
                        "Digital Library Down (5)", "Inter-Library Loan Delay (3)", "Other (2)"};
                } else {
                    return new String[]{"Staff Area Noise (3)", "Catalog System Slow (4)", "Photocopier Not Working (3)",
                        "Study Room Booking Issue (3)", "Newspaper Section Unorganized (2)", "Other (2)"};
                }
            default: return new String[]{"Other (2)"};
        }
    }

    private void submitComplaint() {
        try {
            int catIdx = categoryCombo.getSelectedIndex();
            ComplaintCategory category = categories[catIdx];
            String issue = (String) issueCombo.getSelectedItem();
            String role = getCurrentUserRole();
            setCategoryFlag(category, catIdx, issue, role);

            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            int urgency = urgencySlider.getValue();

            if (title.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Description required");
                return;
            }

            User currentUser = null;
            List<User> users = FileHandler.readUsers();
            for (User u : users) {
                if (u.getUserId().equals(userId)) {
                    currentUser = u;
                    break;
                }
            }

            if (currentUser instanceof IComplaint) {
                ((IComplaint) currentUser).submitComplaint(title, desc, category, urgency, selectedImagePath);
                JOptionPane.showMessageDialog(this, "Complaint submitted successfully!");
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "User cannot submit complaints");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void setCategoryFlag(ComplaintCategory cat, int catIdx, String issueWithPoints, String role) {
        String issue = issueWithPoints.replaceAll(" \\(\\d+\\)$", "");

        switch (catIdx) {
            case 0:
                ITCategory it = (ITCategory) cat;
                if (role.equals("Student")) {
                    if (issue.equals("Internet Down")) it.setInternetDown(true);
                    else if (issue.equals("LMS Not Accessible")) it.setLmsNotAccessible(true);
                    else if (issue.equals("Email Hacked")) it.setEmailHacked(true);
                    else if (issue.equals("Hardware Failure")) it.setHardwareFailure(true);
                    else if (issue.equals("Software Expired")) it.setSoftwareExpired(true);
                    else it.setOther(true);
                } else if (role.equals("Professor")) {
                    if (issue.equals("LMS Server Slow") || issue.equals("Online Exam System Crash") || issue.equals("Online Gradebook Error"))
                        it.setLmsNotAccessible(true);
                    else if (issue.equals("Research Data Loss")) it.setEmailHacked(true);
                    else if (issue.equals("Conference Portal Down")) it.setInternetDown(true);
                    else it.setOther(true);
                } else {
                    if (issue.equals("Email Not Working") || issue.equals("VPN Connection Issue")) it.setInternetDown(true);
                    else if (issue.equals("Printer Not Working")) it.setHardwareFailure(true);
                    else if (issue.equals("Software License Expired")) it.setSoftwareExpired(true);
                    else if (issue.equals("HR Portal Down")) it.setLmsNotAccessible(true);
                    else it.setOther(true);
                }
                break;
            case 1:
                AcademicCategory ac = (AcademicCategory) cat;
                if (role.equals("Student")) {
                    if (issue.equals("Course Registration Blocked")) ac.setCourseRegistrationBlocked(true);
                    else if (issue.equals("Exam Conflict")) ac.setExamConflict(true);
                    else if (issue.equals("Professor Unresponsive")) ac.setProfessorUnresponsive(true);
                    else if (issue.equals("Cheating Accusation")) ac.setCheatingAccusation(true);
                    else if (issue.equals("Thesis Issue")) ac.setThesisIssue(true);
                    else ac.setOther(true);
                } else if (role.equals("Professor")) {
                    if (issue.equals("Grade Dispute") || issue.equals("Plagiarism Case")) ac.setCheatingAccusation(true);
                    else if (issue.equals("Research Funding Delay") || issue.equals("Publication Journal Delay")) ac.setThesisIssue(true);
                    else if (issue.equals("Lab Equipment Broken")) ac.setOther(true);
                    else ac.setOther(true);
                } else {
                    if (issue.equals("Curriculum Change Request")) ac.setCourseRegistrationBlocked(true);
                    else if (issue.equals("Department Meeting Scheduling")) ac.setExamConflict(true);
                    else if (issue.equals("Student Record Access Issue")) ac.setThesisIssue(true);
                    else if (issue.equals("Exam Duty Assignment")) ac.setProfessorUnresponsive(true);
                    else ac.setOther(true);
                }
                break;
            case 2:
                AdministrativeCategory adm = (AdministrativeCategory) cat;
                if (role.equals("Student")) {
                    if (issue.equals("Fee Payment Error")) adm.setFeePaymentError(true);
                    else if (issue.equals("Transcript Delay")) adm.setTranscriptDelay(true);
                    else if (issue.equals("Hostel Allotment Unfair")) adm.setHostelAllotmentUnfair(true);
                    else if (issue.equals("Scholarship Not Disbursed")) adm.setScholarshipNotDisbursed(true);
                    else if (issue.equals("ID Card Not Issued")) adm.setIdCardNotIssued(true);
                    else adm.setOther(true);
                } else if (role.equals("Professor")) {
                    if (issue.equals("Salary Delay")) adm.setFeePaymentError(true);
                    else if (issue.equals("Travel Reimbursement Pending")) adm.setTranscriptDelay(true);
                    else if (issue.equals("Research Grant Approval")) adm.setScholarshipNotDisbursed(true);
                    else if (issue.equals("Promotion Application Delay")) adm.setHostelAllotmentUnfair(true);
                    else adm.setOther(true);
                } else {
                    if (issue.equals("Leave Application Delay")) adm.setTranscriptDelay(true);
                    else if (issue.equals("Overtime Payment Issue")) adm.setFeePaymentError(true);
                    else if (issue.equals("Transfer Request")) adm.setHostelAllotmentUnfair(true);
                    else if (issue.equals("Annual Increment Delay")) adm.setScholarshipNotDisbursed(true);
                    else if (issue.equals("Medical Claim Pending")) adm.setIdCardNotIssued(true);
                    else adm.setOther(true);
                }
                break;
            case 3:
                InfrastructureCategory inf = (InfrastructureCategory) cat;
                if (issue.equals("Fire Hazard")) inf.setFireHazard(true);
                else if (role.equals("Student")) {
                    if (issue.equals("Power Outage")) inf.setPowerOutage(true);
                    else if (issue.equals("Water Shortage")) inf.setWaterShortage(true);
                    else if (issue.equals("Elevator Broken")) inf.setElevatorBroken(true);
                    else if (issue.equals("Broken Furniture")) inf.setBrokenFurniture(true);
                    else if (issue.equals("AC Not Working")) inf.setAcNotWorking(true);
                    else inf.setOther(true);
                } else if (role.equals("Professor")) {
                    if (issue.equals("Classroom AC Not Working")) inf.setAcNotWorking(true);
                    else if (issue.equals("Projector Malfunction")) inf.setOther(true);
                    else if (issue.equals("Office Door Lock Issue")) inf.setBrokenFurniture(true);
                    else if (issue.equals("Cabin Lighting Problem")) inf.setPowerOutage(true);
                    else if (issue.equals("Washroom Unclean")) inf.setWaterShortage(true);
                    else inf.setOther(true);
                } else {
                    if (issue.equals("Staff Room AC Issue")) inf.setAcNotWorking(true);
                    else if (issue.equals("Parking Space Problem")) inf.setOther(true);
                    else if (issue.equals("Cafeteria Cleanliness")) inf.setWaterShortage(true);
                    else if (issue.equals("Office Furniture Broken")) inf.setBrokenFurniture(true);
                    else if (issue.equals("Water Cooler Not Working")) inf.setWaterShortage(true);
                    else inf.setOther(true);
                }
                break;
            case 4:
                LibraryCategory lib = (LibraryCategory) cat;
                if (role.equals("Student")) {
                    if (issue.equals("Overdue Fine Dispute")) lib.setOverdueFineDispute(true);
                    else if (issue.equals("Book Missing")) lib.setBookMissing(true);
                    else if (issue.equals("Reservation System Down")) lib.setReservationSystemDown(true);
                    else if (issue.equals("Staff Behavior")) lib.setStaffBehavior(true);
                    else if (issue.equals("Lost Item")) lib.setLostItem(true);
                    else lib.setOther(true);
                } else if (role.equals("Professor")) {
                    if (issue.equals("Journal Access Denied") || issue.equals("Reference Book Not Available")) lib.setBookMissing(true);
                    else if (issue.equals("Research Section Noise")) lib.setStaffBehavior(true);
                    else if (issue.equals("Digital Library Down")) lib.setReservationSystemDown(true);
                    else if (issue.equals("Inter-Library Loan Delay")) lib.setOverdueFineDispute(true);
                    else lib.setOther(true);
                } else {
                    if (issue.equals("Staff Area Noise")) lib.setStaffBehavior(true);
                    else if (issue.equals("Catalog System Slow") || issue.equals("Study Room Booking Issue")) lib.setReservationSystemDown(true);
                    else if (issue.equals("Photocopier Not Working") || issue.equals("Newspaper Section Unorganized")) lib.setOther(true);
                    else lib.setOther(true);
                }
                break;
        }
    }
}