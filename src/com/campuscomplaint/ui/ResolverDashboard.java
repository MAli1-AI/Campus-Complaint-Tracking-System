package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.service.ComplaintService;
import com.campuscomplaint.util.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ResolverDashboard extends JPanel {
    private MainFrame parent;
    private String resolverId;
    private JTabbedPane tabbedPane;
    private DefaultTableModel activeModel, resolvedModel;
    private JTable activeTable, resolvedTable;
    private JButton btn25, btn50, btn75, btn100;
    private String selectedComplaintId;

    public ResolverDashboard(MainFrame parent, String resolverId, String name) {
        this.parent = parent;
        this.resolverId = resolverId;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        TopStripPanel topStrip = new TopStripPanel("Resolver Dashboard - " + name);
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
        // Increased rows to 7 (added Queries button)
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton refreshBtn = new JButton("Refresh");
        btn25 = new JButton("25% - Read");
        btn50 = new JButton("50% - In Progress");
        btn75 = new JButton("75% - Near Completion");
        btn100 = new JButton("100% - Resolved");
        JButton viewScreenshotBtn = new JButton("View Screenshot");
        JButton queriesBtn = new JButton("Queries");

        ColorScheme.styleButton(refreshBtn);
        ColorScheme.styleButton(btn25);
        ColorScheme.styleButton(btn50);
        ColorScheme.styleButton(btn75);
        ColorScheme.styleButton(btn100);
        ColorScheme.styleButton(viewScreenshotBtn);
        ColorScheme.styleButton(queriesBtn);

        refreshBtn.addActionListener(e -> loadComplaints());
        btn25.addActionListener(e -> updateStatus(25));
        btn50.addActionListener(e -> updateStatus(50));
        btn75.addActionListener(e -> updateStatus(75));
        btn100.addActionListener(e -> updateStatus(100));
        viewScreenshotBtn.addActionListener(e -> viewScreenshot());
        queriesBtn.addActionListener(e -> showQueriesDialog());

        panel.add(refreshBtn);
        panel.add(btn25);
        panel.add(btn50);
        panel.add(btn75);
        panel.add(btn100);
        panel.add(viewScreenshotBtn);
        panel.add(queriesBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        // Active Tab – only complaints not overdue and not resolved
        activeModel = new DefaultTableModel(new String[]{"ID", "Category", "Title", "Status", "Deadline"}, 0);
        activeTable = new JTable(activeModel);
        ColorScheme.styleTable(activeTable);
        ColorScheme.styleTableHeader(activeTable);

        activeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = activeTable.getSelectedRow();
                if (row != -1) {
                    selectedComplaintId = (String) activeModel.getValueAt(row, 0);
                    updateStatusButtons();
                } else {
                    selectedComplaintId = null;
                    updateStatusButtons();
                }
            }
        });

        tabbedPane.addTab("Active", new JScrollPane(activeTable));

        // Resolved Tab (unchanged)
        resolvedModel = new DefaultTableModel(new String[]{"ID", "Category", "Title", "User", "Resolved Date", "Resolution Time", "Feedback"}, 0);
        resolvedTable = new JTable(resolvedModel);
        ColorScheme.styleTable(resolvedTable);
        ColorScheme.styleTableHeader(resolvedTable);
        tabbedPane.addTab("Resolved", new JScrollPane(resolvedTable));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadComplaints() {
        activeModel.setRowCount(0);
        resolvedModel.setRowCount(0);
        List<Complaint> allAssigned = ComplaintService.getComplaintsByResolver(resolverId);
        System.out.println("ResolverDashboard: Loaded " + allAssigned.size() + " assigned complaints.");

        for (Complaint c : allAssigned) {
            // Resolved complaints go to Resolved tab
            if (c.getStatusPercent() == 100) {
                String resolvedDate = (c.getResolvedTimestamp() != null) ? DateUtils.formatDateTime(c.getResolvedTimestamp()) : "N/A";
                long hours = c.getResolutionTimeInHours();
                String timeStr = (hours >= 0) ? String.format("%.1f h", (double) hours) : "N/A";
                String feedback = c.getFeedback() == -1 ? "Pending" : c.getFeedback() + "%";
                resolvedModel.addRow(new Object[]{
                    c.getComplaintId(), c.getCategory().getCategoryType(), c.getTitle(),
                    c.getUserId(), resolvedDate, timeStr, feedback
                });
            } else if (!c.isOverdue()) {
                // Active – only if not overdue
                String deadline = DateUtils.formatDateTime(c.getDeadline());
                activeModel.addRow(new Object[]{
                    c.getComplaintId(), c.getCategory().getCategoryType(), c.getTitle(),
                    c.getStatusPercent() + "%", deadline
                });
            }
            // Overdue complaints are excluded from resolver view (they go to Manager Overdue tab)
        }

        selectedComplaintId = null;
        updateStatusButtons();
        if (activeModel.getRowCount() > 0) {
            activeTable.setRowSelectionInterval(0, 0);
        }
    }

    // New method: show pending queries and allow response
    private void showQueriesDialog() {
        List<Complaint> allAssigned = ComplaintService.getComplaintsByResolver(resolverId);
        List<Complaint> queries = allAssigned.stream()
            .filter(c -> c.getManagerQuery() != null && !c.getManagerQuery().isEmpty() && c.getResolverResponse().isEmpty())
            .collect(Collectors.toList());

        if (queries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending queries.");
            return;
        }

        JDialog queryDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pending Queries", true);
        queryDialog.setSize(600, 400);
        queryDialog.setLocationRelativeTo(this);
        queryDialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        for (Complaint c : queries) {
            listModel.addElement("Complaint: " + c.getComplaintId() + " | Query: " + c.getManagerQuery());
        }
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        queryDialog.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton respondBtn = new JButton("Respond");
        ColorScheme.styleButton(respondBtn);
        respondBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(queryDialog, "Select a query.");
                return;
            }
            String complaintId = selected.split("\\|")[0].replace("Complaint: ", "").trim();
            String response = JOptionPane.showInputDialog(queryDialog, "Your response:");
            if (response != null && !response.trim().isEmpty()) {
                try {
                    ComplaintService.sendResolverResponse(complaintId, resolverId, response);
                    JOptionPane.showMessageDialog(queryDialog, "Response sent.");
                    queryDialog.dispose();
                    loadComplaints();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(queryDialog, "Error: " + ex.getMessage());
                }
            }
        });
        bottom.add(respondBtn);
        JButton closeBtn = new JButton("Close");
        ColorScheme.styleButton(closeBtn);
        closeBtn.addActionListener(e -> queryDialog.dispose());
        bottom.add(closeBtn);
        queryDialog.add(bottom, BorderLayout.SOUTH);
        queryDialog.setVisible(true);
    }

    // Existing methods (updateStatusButtons, updateStatus, viewScreenshot, logout) unchanged
    // (they are already correct and don't need modification)

    private void updateStatusButtons() {
        if (tabbedPane.getSelectedIndex() != 0) {
            btn25.setEnabled(false);
            btn50.setEnabled(false);
            btn75.setEnabled(false);
            btn100.setEnabled(false);
            return;
        }

        if (selectedComplaintId == null) {
            btn25.setEnabled(false);
            btn50.setEnabled(false);
            btn75.setEnabled(false);
            btn100.setEnabled(false);
            return;
        }

        List<Complaint> complaints = ComplaintService.getComplaintsByResolver(resolverId);
        int currentStatus = 0;
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(selectedComplaintId)) {
                currentStatus = c.getStatusPercent();
                break;
            }
        }

        btn25.setEnabled(currentStatus == 0);
        btn50.setEnabled(currentStatus == 25);
        btn75.setEnabled(currentStatus == 50);
        btn100.setEnabled(currentStatus == 75);
    }

    private void updateStatus(int newStatus) {
        if (selectedComplaintId == null) {
            JOptionPane.showMessageDialog(this, "Please select a complaint first.");
            return;
        }

        List<Complaint> complaints = ComplaintService.getComplaintsByResolver(resolverId);
        int currentStatus = 0;
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(selectedComplaintId)) {
                currentStatus = c.getStatusPercent();
                break;
            }
        }

        if (newStatus != currentStatus + 25 && !(currentStatus == 0 && newStatus == 0)) {
            JOptionPane.showMessageDialog(this, "You must follow the sequence: 0% → 25% → 50% → 75% → 100%");
            return;
        }

        try {
            ComplaintService.updateComplaintStatus(selectedComplaintId, newStatus, resolverId);
            JOptionPane.showMessageDialog(this, "Status updated to " + newStatus + "%");
            loadComplaints();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void viewScreenshot() {
        int row = activeTable.getSelectedRow();
        String complaintId = null;
        if (row != -1) {
            complaintId = (String) activeModel.getValueAt(row, 0);
        } else {
            row = resolvedTable.getSelectedRow();
            if (row != -1) {
                complaintId = (String) resolvedModel.getValueAt(row, 0);
            }
        }

        if (complaintId == null) {
            JOptionPane.showMessageDialog(this, "Select a complaint first.");
            return;
        }

        List<Complaint> all = ComplaintService.getAllComplaints();
        for (Complaint c : all) {
            if (c.getComplaintId().equals(complaintId)) {
                String path = c.getImagePath();
                if (path == null || path.isEmpty() || !new java.io.File(path).exists()) {
                    JOptionPane.showMessageDialog(this, "No image/screenshot available.");
                    return;
                }
                JDialog imgDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Screenshot", true);
                ImageIcon icon = new ImageIcon(path);
                JLabel label = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
                imgDialog.add(label);
                imgDialog.setSize(450, 350);
                imgDialog.setLocationRelativeTo(this);
                imgDialog.setVisible(true);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Complaint not found.");
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