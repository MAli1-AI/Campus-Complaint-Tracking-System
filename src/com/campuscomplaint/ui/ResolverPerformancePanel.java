package com.campuscomplaint.ui;

import com.campuscomplaint.model.*;
import com.campuscomplaint.service.ComplaintService;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;   // ← ADDED THIS IMPORT
import java.util.List;
import java.util.stream.Collectors;

public class ResolverPerformancePanel extends JDialog {
    private JComboBox<String> resolverCombo;
    private JLabel nameVal, idVal, expertiseVal;
    private JLabel assignedVal, pendingVal, resolvedVal;
    private JLabel avgTimeVal, avgFeedbackVal;
    private JLabel avatarLabel;
    private List<Resolver> resolvers;

    public ResolverPerformancePanel(JFrame parent) {
        super(parent, "Resolver Performance", true);
        setSize(550, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ColorScheme.BACKGROUND);

        List<User> users = FileHandler.readUsers();
        resolvers = users.stream()
                .filter(u -> u instanceof Resolver)
                .map(u -> (Resolver) u)
                .collect(Collectors.toList());

        if (resolvers.isEmpty()) {
            add(new JLabel("No resolvers found.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorScheme.BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboPanel.setBackground(ColorScheme.BACKGROUND);
        comboPanel.add(new JLabel("Select Resolver:"));
        resolverCombo = new JComboBox<>();
        for (Resolver r : resolvers) {
            resolverCombo.addItem(r.getUserId() + " (" + r.getExpertise() + ")");
        }
        resolverCombo.addActionListener(e -> updateStats());
        comboPanel.add(resolverCombo);
        topPanel.add(comboPanel, BorderLayout.WEST);

        avatarLabel = new JLabel(createDefaultAvatar(60));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        topPanel.add(avatarLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(ColorScheme.BACKGROUND);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel personalPanel = createSectionPanel("Personal Information", 3);
        addLabelValuePair(personalPanel, "Name:", nameVal = new JLabel("-"));
        addLabelValuePair(personalPanel, "ID:", idVal = new JLabel("-"));
        addLabelValuePair(personalPanel, "Expertise:", expertiseVal = new JLabel("-"));
        detailsPanel.add(personalPanel);
        detailsPanel.add(Box.createVerticalStrut(15));

        JPanel workloadPanel = createSectionPanel("Workload Statistics", 3);
        addLabelValuePair(workloadPanel, "Assigned:", assignedVal = new JLabel("-"));
        addLabelValuePair(workloadPanel, "Pending:", pendingVal = new JLabel("-"));
        addLabelValuePair(workloadPanel, "Resolved:", resolvedVal = new JLabel("-"));
        detailsPanel.add(workloadPanel);
        detailsPanel.add(Box.createVerticalStrut(15));

        JPanel metricsPanel = createSectionPanel("Performance Metrics", 2);
        addLabelValuePair(metricsPanel, "Avg Resolution Time:", avgTimeVal = new JLabel("-"));
        addLabelValuePair(metricsPanel, "Avg Feedback:", avgFeedbackVal = new JLabel("-"));
        detailsPanel.add(metricsPanel);

        add(new JScrollPane(detailsPanel), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        ColorScheme.styleButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(ColorScheme.BACKGROUND);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        updateStats();
    }

    private JPanel createSectionPanel(String title, int rows) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        heading.setForeground(Color.BLACK);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(rows, 2, 10, 5));
        content.setBackground(Color.WHITE);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void addLabelValuePair(JPanel sectionPanel, String labelText, JLabel valueLabel) {
        JPanel content = (JPanel) ((BorderLayout) sectionPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Color.BLACK);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(Color.BLACK);
        content.add(label);
        content.add(valueLabel);
    }

    private ImageIcon createDefaultAvatar(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(100, 149, 237));
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, size / 2));
        String text = "R";
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, (size - textWidth) / 2, (size + textHeight) / 2 - 2);
        g2.dispose();
        return new ImageIcon(img);
    }

    private void updateStats() {
        if (resolverCombo.getSelectedIndex() == -1) return;
        String selected = (String) resolverCombo.getSelectedItem();
        String resolverId = selected.split(" ")[0];

        Resolver resolver = resolvers.stream().filter(r -> r.getUserId().equals(resolverId)).findFirst().orElse(null);
        if (resolver == null) return;

        nameVal.setText(resolver.getName());
        idVal.setText(resolver.getUserId());
        expertiseVal.setText(resolver.getExpertise());

        List<Complaint> assigned = ComplaintService.getComplaintsByResolver(resolverId);
        long resolvedCount = assigned.stream().filter(c -> c.getStatusPercent() == 100).count();
        long pendingCount = assigned.size() - resolvedCount;

        assignedVal.setText(String.valueOf(assigned.size()));
        pendingVal.setText(String.valueOf(pendingCount));
        resolvedVal.setText(String.valueOf(resolvedCount));

        double avgTime = resolver.getAverageResolutionTime();
        avgTimeVal.setText(avgTime >= 0 ? String.format("%.1f hours", avgTime) : "No resolved complaints");

        double avgFeedback = resolver.getAverageRating();
        avgFeedbackVal.setText(avgFeedback >= 0 ? String.format("%.1f %%", avgFeedback) : "No feedback yet");
    }
}