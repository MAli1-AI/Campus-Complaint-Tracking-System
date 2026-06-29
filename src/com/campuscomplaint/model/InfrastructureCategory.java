package com.campuscomplaint.model;

public class InfrastructureCategory extends ComplaintCategory {
    private boolean powerOutage;
    private boolean waterShortage;
    private boolean elevatorBroken;
    private boolean brokenFurniture;
    private boolean acNotWorking;
    private boolean other;
    private String otherDescription;
    private boolean fireHazard;
    public InfrastructureCategory() {
        super("Infrastructure", "Issues related to buildings, electricity, water, furniture, and AC");
        this.otherDescription = "";
    }

    public boolean isFireHazard() { return fireHazard; }
    public void setFireHazard(boolean val) { this.fireHazard = val; }
    public boolean isPowerOutage() { return powerOutage; }
    public void setPowerOutage(boolean val) { this.powerOutage = val; }
    public boolean isWaterShortage() { return waterShortage; }
    public void setWaterShortage(boolean val) { this.waterShortage = val; }
    public boolean isElevatorBroken() { return elevatorBroken; }
    public void setElevatorBroken(boolean val) { this.elevatorBroken = val; }
    public boolean isBrokenFurniture() { return brokenFurniture; }
    public void setBrokenFurniture(boolean val) { this.brokenFurniture = val; }
    public boolean isAcNotWorking() { return acNotWorking; }
    public void setAcNotWorking(boolean val) { this.acNotWorking = val; }
    public boolean isOther() { return other; }
    public void setOther(boolean val) { this.other = val; }
    public String getOtherDescription() { return otherDescription; }
    public void setOtherDescription(String desc) { this.otherDescription = desc; }

    @Override
    public String getCategoryType() { return "Infrastructure"; }

    @Override
   public int calculatePriority(Complaint complaint) {
    int points = 0;
    if (fireHazard) points = 15;
    else if (powerOutage) points = 8;
    else if (waterShortage) points = 7;
    else if (elevatorBroken) points = 5;
    else if (acNotWorking) points = 4;
    else if (brokenFurniture) points = 3;
    else if (other) points = 2;
    return complaint.getUrgencyLevel() + points;
}
}