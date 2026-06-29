package com.campuscomplaint.model;

public class LibraryCategory extends ComplaintCategory {
    private boolean overdueFineDispute;
    private boolean bookMissing;
    private boolean reservationSystemDown;
    private boolean staffBehavior;
    private boolean lostItem;
    private boolean other;
    private String otherDescription;

    public LibraryCategory() {
        super("Library", "Library services: fines, missing books, reservations, staff, lost items");
        this.otherDescription = "";
    }

    public boolean isOverdueFineDispute() { return overdueFineDispute; }
    public void setOverdueFineDispute(boolean val) { this.overdueFineDispute = val; }
    public boolean isBookMissing() { return bookMissing; }
    public void setBookMissing(boolean val) { this.bookMissing = val; }
    public boolean isReservationSystemDown() { return reservationSystemDown; }
    public void setReservationSystemDown(boolean val) { this.reservationSystemDown = val; }
    public boolean isStaffBehavior() { return staffBehavior; }
    public void setStaffBehavior(boolean val) { this.staffBehavior = val; }
    public boolean isLostItem() { return lostItem; }
    public void setLostItem(boolean val) { this.lostItem = val; }
    public boolean isOther() { return other; }
    public void setOther(boolean val) { this.other = val; }
    public String getOtherDescription() { return otherDescription; }
    public void setOtherDescription(String desc) { this.otherDescription = desc; }

    @Override
    public String getCategoryType() { return "Library"; }

    @Override
    public int calculatePriority(Complaint complaint) {
        int points = 0;
        if (overdueFineDispute) points = 3;
        else if (bookMissing) points = 4;
        else if (reservationSystemDown) points = 3;
        else if (staffBehavior) points = 5;
        else if (lostItem) points = 2;
        else if (other) points = 2;
        return complaint.getUrgencyLevel() + points;
    }
}