package com.campuscomplaint.ui;

import com.campuscomplaint.service.ComplaintService;
import javax.swing.*;

public class FeedbackDialog extends JDialog {
    public FeedbackDialog(JFrame parent, String complaintId, String userId, Runnable onSuccess) {
        super(parent, "Give Feedback", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel("Satisfaction Level (0-100%):");
        label.setAlignmentX(CENTER_ALIGNMENT);
        
        JSlider slider = new JSlider(0, 100, 80);
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setAlignmentX(CENTER_ALIGNMENT);
        
        JButton submitBtn = new JButton("Submit Feedback");
        ColorScheme.styleButton(submitBtn);
        submitBtn.setAlignmentX(CENTER_ALIGNMENT);
        
        submitBtn.addActionListener(e -> {
            try {
                ComplaintService.giveFeedback(complaintId, slider.getValue(), userId);
                JOptionPane.showMessageDialog(this, "Feedback submitted!");
                if (onSuccess != null) onSuccess.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        add(Box.createVerticalStrut(20));
        add(label);
        add(Box.createVerticalStrut(10));
        add(slider);
        add(Box.createVerticalStrut(20));
        add(submitBtn);
        add(Box.createVerticalStrut(20));
    }
}