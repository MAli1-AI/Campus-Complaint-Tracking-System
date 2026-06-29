package com.campuscomplaint.model;

public abstract class User {
    protected String userId;
    protected String name;
    protected String password;
    protected String role;

    public User(String userId, String name, String password, String role) {
        if (userId == null || name == null || password == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public abstract boolean login(String id, String password);

    protected boolean isValid(String data) {
        return data != null && !data.trim().isEmpty();
    }

    public boolean validateBaseData() {
        return isValid(userId) && isValid(name) && isValid(password);
    }

    @Override
    public String toString() {
        return "ID: " + userId + " | Name: " + name + " | Role: " + role;
    }

    public String fileFormat() {
        return userId + "," + name + "," + password + "," + role;
    }
}