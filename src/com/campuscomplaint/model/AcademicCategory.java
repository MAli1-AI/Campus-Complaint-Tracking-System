package com.campuscomplaint.model;

public class AcademicCategory extends ComplaintCategory {
    private boolean courseRegistrationBlocked;
    private boolean examConflict;
    private boolean professorUnresponsive;
    private boolean cheatingAccusation;
    private boolean thesisIssue;
    private boolean other;
    private String otherDescription;

    public AcademicCategory() {
        super("Academic", "Issues related to courses, exams, grading, and supervision");
        this.otherDescription = "";
    }

    public boolean isCourseRegistrationBlocked() { return courseRegistrationBlocked; }
    public void setCourseRegistrationBlocked(boolean val) { this.courseRegistrationBlocked = val; }
    public boolean isExamConflict() { return examConflict; }
    public void setExamConflict(boolean val) { this.examConflict = val; }
    public boolean isProfessorUnresponsive() { return professorUnresponsive; }
    public void setProfessorUnresponsive(boolean val) { this.professorUnresponsive = val; }
    public boolean isCheatingAccusation() { return cheatingAccusation; }
    public void setCheatingAccusation(boolean val) { this.cheatingAccusation = val; }
    public boolean isThesisIssue() { return thesisIssue; }
    public void setThesisIssue(boolean val) { this.thesisIssue = val; }
    public boolean isOther() { return other; }
    public void setOther(boolean val) { this.other = val; }
    public String getOtherDescription() { return otherDescription; }
    public void setOtherDescription(String desc) { this.otherDescription = desc; }

    @Override
    public String getCategoryType() { return "Academic"; }

    @Override
    public int calculatePriority(Complaint complaint) {
        int points = 0;
        if (courseRegistrationBlocked) points = 5;
        else if (examConflict) points = 4;
        else if (professorUnresponsive) points = 3;
        else if (cheatingAccusation) points = 8;
        else if (thesisIssue) points = 5;
        else if (other) points = 2;
        return complaint.getUrgencyLevel() + points;
    }
}