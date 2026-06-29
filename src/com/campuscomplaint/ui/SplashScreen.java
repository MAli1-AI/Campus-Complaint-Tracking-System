package com.campuscomplaint.ui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(13, 71, 161));
        
        JLabel titleLabel = new JLabel("CAMPUS COMPLAINT TRACKING SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        
        JLabel loadingLabel = new JLabel("Initializing system...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(new Color(200, 200, 200));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(0, 200, 0));
        
        JLabel footerLabel = new JLabel("Moeen Ali Abdullah Awan & Mohammad Ali Mughal | COMSATS Islamabad", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(180, 180, 180));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 30, 10));
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(loadingLabel, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);
        mainPanel.add(footerLabel, BorderLayout.AFTER_LAST_LINE);
        
        add(mainPanel);
        setSize(550, 320);
        setLocationRelativeTo(null);
    }
    
    public void showSplash(Runnable onComplete) {
        setVisible(true);
        Timer timer = new Timer(5000, e -> {
            dispose();
            if (onComplete != null) onComplete.run();
        });
        timer.setRepeats(false);
        timer.start();
    }
}