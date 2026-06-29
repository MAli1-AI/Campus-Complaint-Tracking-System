package com.campuscomplaint.exception;

public class ComplaintNotFoundException extends Exception {
    public ComplaintNotFoundException(String message) {
        super(message);
    }
    
    public ComplaintNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}