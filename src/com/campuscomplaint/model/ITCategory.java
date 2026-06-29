package com.campuscomplaint.model;

public class ITCategory extends ComplaintCategory {
    private boolean internetDown;
    private boolean lmsNotAccessible;
    private boolean emailHacked;
    private boolean softwareExpired;
    private boolean hardwareFailure;
    private boolean other;
    private String otherDescription;

    public ITCategory() {
        super("IT", "Technical issues: internet, LMS, email, software, hardware");
        this.otherDescription = "";
    }

    public boolean isInternetDown() { return internetDown; }
    public void setInternetDown(boolean val) { this.internetDown = val; }
    public boolean isLmsNotAccessible() { return lmsNotAccessible; }
    public void setLmsNotAccessible(boolean val) { this.lmsNotAccessible = val; }
    public boolean isEmailHacked() { return emailHacked; }
    public void setEmailHacked(boolean val) { this.emailHacked = val; }
    public boolean isSoftwareExpired() { return softwareExpired; }
    public void setSoftwareExpired(boolean val) { this.softwareExpired = val; }
    public boolean isHardwareFailure() { return hardwareFailure; }
    public void setHardwareFailure(boolean val) { this.hardwareFailure = val; }
    public boolean isOther() { return other; }
    public void setOther(boolean val) { this.other = val; }
    public String getOtherDescription() { return otherDescription; }
    public void setOtherDescription(String desc) { this.otherDescription = desc; }

    @Override
    public String getCategoryType() { return "IT"; }

    @Override
    public int calculatePriority(Complaint complaint) {
        int points = 0;
        if (internetDown) points = 8;
        else if (lmsNotAccessible) points = 5;
        else if (emailHacked) points = 7;
        else if (hardwareFailure) points = 4;
        else if (softwareExpired) points = 3;
        else if (other) points = 3;
        return complaint.getUrgencyLevel() + points;
    }
}