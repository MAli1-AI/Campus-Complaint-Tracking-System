package com.campuscomplaint.model;

public class AdministrativeCategory extends ComplaintCategory {
    private boolean feePaymentError;
    private boolean transcriptDelay;
    private boolean hostelAllotmentUnfair;
    private boolean scholarshipNotDisbursed;
    private boolean idCardNotIssued;
    private boolean other;
    private String otherDescription;

    public AdministrativeCategory() {
        super("Administrative", "Issues related to fees, transcripts, hostels, scholarships, ID cards");
        this.otherDescription = "";
    }

    public boolean isFeePaymentError() { return feePaymentError; }
    public void setFeePaymentError(boolean val) { this.feePaymentError = val; }
    public boolean isTranscriptDelay() { return transcriptDelay; }
    public void setTranscriptDelay(boolean val) { this.transcriptDelay = val; }
    public boolean isHostelAllotmentUnfair() { return hostelAllotmentUnfair; }
    public void setHostelAllotmentUnfair(boolean val) { this.hostelAllotmentUnfair = val; }
    public boolean isScholarshipNotDisbursed() { return scholarshipNotDisbursed; }
    public void setScholarshipNotDisbursed(boolean val) { this.scholarshipNotDisbursed = val; }
    public boolean isIdCardNotIssued() { return idCardNotIssued; }
    public void setIdCardNotIssued(boolean val) { this.idCardNotIssued = val; }
    public boolean isOther() { return other; }
    public void setOther(boolean val) { this.other = val; }
    public String getOtherDescription() { return otherDescription; }
    public void setOtherDescription(String desc) { this.otherDescription = desc; }

    @Override
    public String getCategoryType() { return "Administrative"; }

    @Override
    public int calculatePriority(Complaint complaint) {
        int points = 0;
        if (feePaymentError) points = 5;
        else if (transcriptDelay) points = 4;
        else if (hostelAllotmentUnfair) points = 6;
        else if (scholarshipNotDisbursed) points = 7;
        else if (idCardNotIssued) points = 3;
        else if (other) points = 2;
        return complaint.getUrgencyLevel() + points;
    }
}