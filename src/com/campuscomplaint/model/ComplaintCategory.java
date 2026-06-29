package com.campuscomplaint.model;

public abstract class ComplaintCategory {
    protected String name;
    protected String description;

    public ComplaintCategory(String name, String description) {
        if (name == null || description == null || name.trim().isEmpty() || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name and description cannot be null or empty");
        }
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public abstract String getCategoryType();
    public abstract int calculatePriority(Complaint complaint);
}