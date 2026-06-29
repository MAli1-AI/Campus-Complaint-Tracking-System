package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.service.ComplaintService;
import com.campuscomplaint.model.FileHandler;
import com.campuscomplaint.util.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManagerDashboard extends JPanel {
    private MainFrame parent;
    private String managerId;
    private JTabbedPane tabbedPane;
    private DefaultTableModel submittedModel, resolvedModel, feedbackModel, overdueModel;
    private JTable submittedTable, resolvedTable, feedbackTable, overdueTable;

    public ManagerDashboard(MainFrame parent, String managerId, String name) {
        this.parent = parent;
        this.managerId = managerId;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        TopStripPanel topStrip = new TopStripPanel("Manager Dashboard - " + name);
        add(topStrip, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createButtonPanel(), createTabPanel());
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton logoutBtn = new JButton("Logout");
        ColorScheme.styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> logout());

        JButton statsBtn = new JButton("Show Statistics");
        ColorScheme.styleButton(statsBtn);
        statsBtn.addActionListener(e -> showStatistics());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(statsBtn);
        btnPanel.add(logoutBtn);
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        loadComplaints();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton refreshBtn = new JButton("Refresh");
        JButton reportBtn = new JButton("Resolver Performance");
        JButton addResolverBtn = new JButton("Add Resolver");
        JButton queryBtn = new JButton("Query Resolver");
        JButton reassignBtn = new JButton("Reassign");
        JButton flagBtn = new JButton("Flag/Unflag");
        JButton viewResponsesBtn = new JButton("View Responses");   // NEW

        ColorScheme.styleButton(refreshBtn);
        ColorScheme.styleButton(reportBtn);
        ColorScheme.styleButton(addResolverBtn);
        ColorScheme.styleButton(queryBtn);
        ColorScheme.styleButton(reassignBtn);
        ColorScheme.styleButton(flagBtn);
        ColorScheme.styleButton(viewResponsesBtn);

        refreshBtn.addActionListener(e -> loadComplaints());
        reportBtn.addActionListener(e -> showResolverPerformance());
        addResolverBtn.addActionListener(e -> showAddResolverDialog());
        queryBtn.addActionListener(e -> showQueryDialog());
        reassignBtn.addActionListener(e -> showReassignDialog());
        flagBtn.addActionListener(e -> showFlagDialog());
        viewResponsesBtn.addActionListener(e -> showResponsesDialog());   // NEW

        panel.add(refreshBtn);
        panel.add(reportBtn);
        panel.add(addResolverBtn);
        panel.add(queryBtn);
        panel.add(reassignBtn);
        panel.add(flagBtn);
        panel.add(viewResponsesBtn);   // NEW

        return panel;
    }

    private JPanel createTabPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        submittedModel = new DefaultTableModel(new String[]{"ID","User","Category","Title","Urgency","Status","Deadline","Resolver"}, 0);
        submittedTable = new JTable(submittedModel);
        ColorScheme.styleTable(submittedTable);
        ColorScheme.styleTableHeader(submittedTable);
        tabbedPane.addTab("Submitted", new JScrollPane(submittedTable));

        resolvedModel = new DefaultTableModel(new String[]{"ID","User","Category","Title","Resolver","Resolved Date","Resolution Time"}, 0);
        resolvedTable = new JTable(resolvedModel);
        ColorScheme.styleTable(resolvedTable);
        ColorScheme.styleTableHeader(resolvedTable);
        tabbedPane.addTab("Resolved", new JScrollPane(resolvedTable));

        feedbackModel = new DefaultTableModel(new String[]{"ID","User","Category","Resolver","Feedback %","Resolved Date"}, 0);
        feedbackTable = new JTable(feedbackModel);
        ColorScheme.styleTable(feedbackTable);
        ColorScheme.styleTableHeader(feedbackTable);
        tabbedPane.addTab("Feedback", new JScrollPane(feedbackTable));

        overdueModel = new DefaultTableModel(new String[]{"ID","User","Category","Title","Status","Deadline","Resolver"}, 0);
        overdueTable = new JTable(overdueModel);
        ColorScheme.styleTable(overdueTable);
        ColorScheme.styleTableHeader(overdueTable);
        tabbedPane.addTab("Overdue", new JScrollPane(overdueTable));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadComplaints() {
        submittedModel.setRowCount(0);
        resolvedModel.setRowCount(0);
        feedbackModel.setRowCount(0);
        overdueModel.setRowCount(0);

        List<Complaint> all = ComplaintService.getAllComplaints();
        System.out.println("ManagerDashboard: Loaded " + all.size() + " complaints.");

        for (Complaint c : all) {
            System.out.println("  Complaint: " + c.getComplaintId() + " | status: " + c.getStatusPercent() + " | overdue: " + c.isOverdue());
            String deadline = DateUtils.formatDateTime(c.getDeadline());
            String resolvedDate = (c.getResolvedTimestamp() != null) ? DateUtils.formatDateTime(c.getResolvedTimestamp()) : "N/A";
            long hours = c.getResolutionTimeInHours();
            String timeStr = (hours >= 0) ? String.format("%.1f h", (double) hours) : "N/A";

            if (c.isOverdue()) {
                overdueModel.addRow(new Object[]{
                    c.getComplaintId(), c.getUserId(), c.getCategory().getCategoryType(),
                    c.getTitle(), c.getStatusPercent() + "%", deadline, c.getResolverId()
                });
                continue;
            }

            if (c.getStatusPercent() < 100) {
                submittedModel.addRow(new Object[]{
                    c.getComplaintId(), c.getUserId(), c.getCategory().getCategoryType(),
                    c.getTitle(), c.getUrgencyLevel(), c.getStatusPercent() + "%",
                    deadline, c.getResolverId()
                });
            } else {
                resolvedModel.addRow(new Object[]{
                    c.getComplaintId(), c.getUserId(), c.getCategory().getCategoryType(),
                    c.getTitle(), c.getResolverId(), resolvedDate, timeStr
                });
                if (c.getFeedback() != -1) {
                    feedbackModel.addRow(new Object[]{
                        c.getComplaintId(), c.getUserId(), c.getCategory().getCategoryType(),
                        c.getResolverId(), c.getFeedback() + "%", resolvedDate
                    });
                }
            }
        }
    }

    private void showStatistics() {
        JDialog statsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Complaint Statistics", true);
        statsDialog.setSize(500, 300);
        statsDialog.setLocationRelativeTo(this);

        String[] cols = {"Category", "Submitted", "Resolved", "Feedback Received"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        List<Complaint> all = ComplaintService.getAllComplaints();

        Map<String, Long> counts = all.stream().collect(Collectors.groupingBy(c -> c.getCategory().getCategoryType(), Collectors.counting()));
        Map<String, Long> resolvedCounts = all.stream().filter(c -> c.getStatusPercent() == 100).collect(Collectors.groupingBy(c -> c.getCategory().getCategoryType(), Collectors.counting()));
        Map<String, Long> feedbackCounts = all.stream().filter(c -> c.getFeedback() != -1).collect(Collectors.groupingBy(c -> c.getCategory().getCategoryType(), Collectors.counting()));

        String[] categories = {"Academic", "Administrative", "IT", "Infrastructure", "Library"};
        for (String cat : categories) {
            long submitted = counts.getOrDefault(cat, 0L);
            long resolved = resolvedCounts.getOrDefault(cat, 0L);
            long feedback = feedbackCounts.getOrDefault(cat, 0L);
            model.addRow(new Object[]{
                cat,
                String.valueOf(submitted),
                String.valueOf(resolved),
                String.valueOf(feedback)
            });
        }

        JTable statTable = new JTable(model);
        ColorScheme.styleTable(statTable);
        ColorScheme.styleTableHeader(statTable);
        statsDialog.add(new JScrollPane(statTable), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        ColorScheme.styleButton(closeBtn);
        closeBtn.addActionListener(e -> statsDialog.dispose());
        statsDialog.add(closeBtn, BorderLayout.SOUTH);
        statsDialog.setVisible(true);
    }

    private void showResolverPerformance() {
        ResolverPerformancePanel perfPanel = new ResolverPerformancePanel((JFrame) SwingUtilities.getWindowAncestor(this));
        perfPanel.setVisible(true);
    }

    private void showAddResolverDialog() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JPasswordField passField = new JPasswordField(10);
        JComboBox<String> expertiseCombo = new JComboBox<>(new String[]{"IT", "Academic", "Administrative", "Infrastructure", "Library"});

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Resolver ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel("Expertise:"));
        panel.add(expertiseCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Resolver", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Manager tempManager = new Manager(managerId, "", "");
                tempManager.addResolver(idField.getText().trim(), nameField.getText().trim(), new String(passField.getPassword()), (String) expertiseCombo.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Resolver added successfully!");
                loadComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showQueryDialog() {
        int selectedTab = tabbedPane.getSelectedIndex();
        int row;
        if (selectedTab == 3) {
            row = overdueTable.getSelectedRow();
        } else if (selectedTab == 0) {
            row = submittedTable.getSelectedRow();
        } else {
            JOptionPane.showMessageDialog(this, "Select a complaint from Submitted or Overdue tab.");
            return;
        }
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint.");
            return;
        }
        String complaintId = (selectedTab == 3) ? (String) overdueModel.getValueAt(row, 0) : (String) submittedModel.getValueAt(row, 0);
        String query = JOptionPane.showInputDialog(this, "Enter query for resolver:");
        if (query != null && !query.trim().isEmpty()) {
            try {
                ComplaintService.sendManagerQuery(complaintId, managerId, query);
                JOptionPane.showMessageDialog(this, "Query sent to resolver.");
                loadComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showReassignDialog() {
        int selectedTab = tabbedPane.getSelectedIndex();
        int row;
        if (selectedTab == 3) {
            row = overdueTable.getSelectedRow();
        } else if (selectedTab == 0) {
            row = submittedTable.getSelectedRow();
        } else {
            JOptionPane.showMessageDialog(this, "Select a complaint from Submitted or Overdue tab.");
            return;
        }
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint.");
            return;
        }
        String complaintId = (selectedTab == 3) ? (String) overdueModel.getValueAt(row, 0) : (String) submittedModel.getValueAt(row, 0);

        List<Complaint> all = ComplaintService.getAllComplaints();
        Complaint target = null;
        for (Complaint c : all) {
            if (c.getComplaintId().equals(complaintId)) {
                target = c;
                break;
            }
        }
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Complaint not found.");
            return;
        }

        final ComplaintCategory category = target.getCategory();

        List<User> users = FileHandler.readUsers();
        List<String> resolverIds = users.stream()
            .filter(u -> u instanceof Resolver)
            .map(u -> (Resolver) u)
            .filter(r -> !r.isFlagged() && r.canHandle(category))
            .map(User::getUserId)
            .collect(Collectors.toList());

        if (resolverIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available resolvers for this category.");
            return;
        }

        String newResolver = (String) JOptionPane.showInputDialog(this,
                "Select new resolver:", "Reassign",
                JOptionPane.QUESTION_MESSAGE, null,
                resolverIds.toArray(), resolverIds.get(0));

        if (newResolver != null) {
            try {
                ComplaintService.reassignComplaint(complaintId, newResolver);
                JOptionPane.showMessageDialog(this, "Complaint reassigned.");
                loadComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

   private void showFlagDialog() {
    List<User> users = FileHandler.readUsers();
    List<Resolver> resolvers = users.stream()
        .filter(u -> u instanceof Resolver)
        .map(u -> (Resolver) u)
        .collect(Collectors.toList());

    if (resolvers.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No resolvers available.");
        return;
    }

    // Create custom dialog
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Flag/Unflag Resolver", true);
    dialog.setSize(400, 500);  // width=400, height=500 (taller than wide)
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    DefaultListModel<String> model = new DefaultListModel<>();
    for (Resolver r : resolvers) {
        model.addElement(r.getUserId() + " (" + r.getExpertise() + ") - " + (r.isFlagged() ? "Flagged" : "Active"));
    }
    JList<String> list = new JList<>(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dialog.add(new JScrollPane(list), BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new FlowLayout());
    JButton toggleBtn = new JButton("Toggle Flag");
    ColorScheme.styleButton(toggleBtn);
    toggleBtn.addActionListener(e -> {
        int idx = list.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(dialog, "Select a resolver.");
            return;
        }
        Resolver r = resolvers.get(idx);
        try {
            if (r.isFlagged()) {
                ComplaintService.unflagResolver(r.getUserId());
            } else {
                ComplaintService.flagResolver(r.getUserId());
            }
            JOptionPane.showMessageDialog(dialog, "Resolver flag toggled.");
            dialog.dispose();
            loadComplaints();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
        }
    });
    bottomPanel.add(toggleBtn);

    JButton closeBtn = new JButton("Close");
    ColorScheme.styleButton(closeBtn);
    closeBtn.addActionListener(e -> dialog.dispose());
    bottomPanel.add(closeBtn);

    dialog.add(bottomPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}
    // NEW: View Resolver Responses
    private void showResponsesDialog() {
        List<Complaint> all = ComplaintService.getAllComplaints();
        List<Complaint> withResponses = all.stream()
                .filter(c -> c.getResolverResponse() != null && !c.getResolverResponse().isEmpty())
                .collect(Collectors.toList());

        if (withResponses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No resolver responses yet.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Resolver Responses", true);
        dialog.setSize(900 ,600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"Complaint ID", "Resolver", "Response"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Complaint c : withResponses) {
            model.addRow(new Object[]{
                c.getComplaintId(),
                c.getResolverId(),
                c.getResolverResponse()
            });
        }
        JTable table = new JTable(model);
        ColorScheme.styleTable(table);
        ColorScheme.styleTableHeader(table);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        ColorScheme.styleButton(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.add(closeBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
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