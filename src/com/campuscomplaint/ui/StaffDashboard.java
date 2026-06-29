package com.campuscomplaint.ui;
import java.awt.BorderLayout;
public class StaffDashboard extends StudentDashboard {
    public StaffDashboard(MainFrame parent, String userId, String userName) {
        super(parent, userId, userName);
        TopStripPanel topStrip = new TopStripPanel("Staff Dashboard");
        remove(0);
        add(topStrip, BorderLayout.NORTH);
        revalidate();
    }
}