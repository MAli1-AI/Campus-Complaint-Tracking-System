package com.campuscomplaint.ui;
import java.awt.BorderLayout;
public class ProfessorDashboard extends StudentDashboard {
    public ProfessorDashboard(MainFrame parent, String userId, String userName) {
        super(parent, userId, userName);
        TopStripPanel topStrip = new TopStripPanel("Professor Dashboard");
        remove(0);
        add(topStrip, BorderLayout.NORTH);
        revalidate();
    }
}