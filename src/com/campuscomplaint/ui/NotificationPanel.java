package com.campuscomplaint.ui;

import com.campuscomplaint.model.FileHandler;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationPanel extends JDialog {
    private DefaultListModel<String> listModel;
    private JList<String> notificationList;
    private String userId;

    public NotificationPanel(JFrame parent, String userId) {
        super(parent, "Notifications", true);
        this.userId = userId;
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        notificationList = new JList<>(listModel);
        notificationList.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");
        ColorScheme.styleButton(refreshBtn);
        ColorScheme.styleButton(closeBtn);

        refreshBtn.addActionListener(e -> loadNotifications());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);

        add(new JScrollPane(notificationList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadNotifications();
    }

    private void loadNotifications() {
        listModel.clear();
        List<String> notifications = FileHandler.getNotifications(userId);
        String lastComplaintId = "";
        Pattern idPattern = Pattern.compile("[A-Z]+-[A-Z]+-\\d+");

        for (String raw : notifications) {
            String message = raw;
            Matcher m = idPattern.matcher(raw);
            String currentId = m.find() ? m.group() : "";

            if (!currentId.isEmpty() && !currentId.equals(lastComplaintId)) {
                if (!lastComplaintId.isEmpty()) {
                    listModel.addElement("──────────────────────────");
                }
                lastComplaintId = currentId;
            }
            listModel.addElement(raw);
        }

        if (notifications.isEmpty()) {
            listModel.addElement("No notifications");
        }
    }
}