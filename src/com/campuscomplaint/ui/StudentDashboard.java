package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.service.ComplaintService;
import com.campuscomplaint.util.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JPanel {
    private MainFrame parent;
    private String userId;
    private String userName;
    private JTabbedPane tabbedPane;
    private DefaultTableModel pendingModel, resolvedModel;
    private JTable pendingTable, resolvedTable;  // ← ADDED as fields

    public StudentDashboard(MainFrame parent, String userId, String userName) {
        this.parent = parent;
        this.userId = userId;
        this.userName = userName;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        TopStripPanel topStrip = new TopStripPanel("Student Dashboard");
        add(topStrip, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.WEST);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        ColorScheme.styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> logout());
        add(logoutBtn, BorderLayout.SOUTH);

        loadComplaints();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton submitBtn = new JButton("Submit Complaint");
        JButton refreshBtn = new JButton("Refresh");
        JButton feedbackBtn = new JButton("Give Feedback");
        JButton notifBtn = new JButton("Notifications");

        ColorScheme.styleButton(submitBtn);
        ColorScheme.styleButton(refreshBtn);
        ColorScheme.styleButton(feedbackBtn);
        ColorScheme.styleButton(notifBtn);

        submitBtn.addActionListener(e -> new SubmitComplaintDialog(parent, userId, this::loadComplaints).setVisible(true));
        refreshBtn.addActionListener(e -> loadComplaints());
        feedbackBtn.addActionListener(e -> showFeedbackDialog());
        notifBtn.addActionListener(e -> showNotifications());

        panel.add(submitBtn);
        panel.add(refreshBtn);
        panel.add(feedbackBtn);
        panel.add(notifBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        pendingModel = new DefaultTableModel(new String[]{"ID", "Category", "Title", "Status", "Deadline", "Resolver"}, 0);
        pendingTable = new JTable(pendingModel);  // ← assign to field
        ColorScheme.styleTable(pendingTable);
        ColorScheme.styleTableHeader(pendingTable);
        tabbedPane.addTab("Pending", new JScrollPane(pendingTable));

        resolvedModel = new DefaultTableModel(new String[]{"ID", "Category", "Title", "Resolver", "Resolved Date", "Feedback"}, 0);
        resolvedTable = new JTable(resolvedModel);  // ← assign to field
        ColorScheme.styleTable(resolvedTable);
        ColorScheme.styleTableHeader(resolvedTable);
        tabbedPane.addTab("Resolved", new JScrollPane(resolvedTable));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadComplaints() {
        pendingModel.setRowCount(0);
        resolvedModel.setRowCount(0);
        List<Complaint> complaints = ComplaintService.getComplaintsByUser(userId);
        for (Complaint c : complaints) {
            String deadline = DateUtils.formatDateTime(c.getDeadline());
            String resolvedDate = (c.getResolvedTimestamp() != null) ? DateUtils.formatDateTime(c.getResolvedTimestamp()) : "N/A";
            String feedbackStr = c.getFeedback() == -1 ? "Pending" : c.getFeedback() + "%";

            if (c.getStatusPercent() < 100) {
                pendingModel.addRow(new Object[]{
                    c.getComplaintId(), c.getCategory().getCategoryType(), c.getTitle(),
                    c.getStatusPercent() + "%", deadline, c.getResolverId()
                });
            } else {
                resolvedModel.addRow(new Object[]{
                    c.getComplaintId(), c.getCategory().getCategoryType(), c.getTitle(),
                    c.getResolverId(), resolvedDate, feedbackStr
                });
            }
        }
    }

    private void showFeedbackDialog() {
        int row = pendingTable.getSelectedRow();
        String complaintId = null;
        if (row != -1) {
            complaintId = (String) pendingModel.getValueAt(row, 0);
        } else {
            row = resolvedTable.getSelectedRow();
            if (row != -1) {
                complaintId = (String) resolvedModel.getValueAt(row, 0);
            }
        }
        if (complaintId == null) {
            JOptionPane.showMessageDialog(this, "Select a complaint first");
            return;
        }

        // Get the actual complaint object to verify status
        List<Complaint> all = ComplaintService.getAllComplaints();
        Complaint target = null;
        for (Complaint c : all) {
            if (c.getComplaintId().equals(complaintId)) {
                target = c;
                break;
            }
        }
        if (target == null || target.getStatusPercent() != 100) {
            JOptionPane.showMessageDialog(this, "Cannot give feedback. Complaint not resolved yet!");
            return;
        }

        FeedbackDialog dialog = new FeedbackDialog(parent, complaintId, userId, () -> {
            loadComplaints();
            JOptionPane.showMessageDialog(this, "Feedback submitted and table refreshed!");
        });
        dialog.setVisible(true);
    }

    private void showNotifications() {
        new NotificationPanel(parent, userId).setVisible(true);
    }

    private void logout() {
        parent.showLogin();
        Component[] comps = parent.getContentPane().getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.getLayout() instanceof CardLayout) {
                    p.remove(this);
                    break;
                }
            }
        }
        parent.revalidate();
        parent.repaint();
    }
}