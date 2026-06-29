package com.campuscomplaint.util;

import com.campuscomplaint.exception.InvalidInputException;
import javax.swing.*;

public class InputValidator {
    
    public static boolean isValidUserId(String userId) {
        return userId != null && !userId.trim().isEmpty() && userId.trim().length() >= 3;
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }
    
    public static boolean isValidTitle(String title) {
        return title != null && title.trim().length() >= 5;
    }
    
    public static boolean isValidDescription(String description) {
        return description != null && description.trim().length() >= 10;
    }
    
    public static boolean isValidUrgency(int urgency) {
        return urgency >= 1 && urgency <= 5;
    }
    
    public static boolean isValidSemester(int semester) {
        return semester >= 1 && semester <= 8;
    }
    
    public static boolean isValidFeedback(int feedback) {
        return feedback >= 0 && feedback <= 100;
    }
    
    public static boolean isValidStatus(int status) {
        return status == 0 || status == 25 || status == 50 || status == 75 || status == 100;
    }
    
    public static String validateAndGetUserId(String input, JComponent parent) throws InvalidInputException {
        if (!isValidUserId(input)) {
            throw new InvalidInputException("User ID must be at least 3 characters and not empty");
        }
        return input.trim();
    }
    
    public static String validateAndGetPassword(String input, JComponent parent) throws InvalidInputException {
        if (!isValidPassword(input)) {
            throw new InvalidInputException("Password must be at least 4 characters");
        }
        return input;
    }
    
    public static String validateAndGetTitle(String input, JComponent parent) throws InvalidInputException {
        if (!isValidTitle(input)) {
            throw new InvalidInputException("Title must be at least 5 characters");
        }
        return input.trim();
    }
    
    public static String validateAndGetDescription(String input, JComponent parent) throws InvalidInputException {
        if (!isValidDescription(input)) {
            throw new InvalidInputException("Description must be at least 10 characters");
        }
        return input.trim();
    }
}