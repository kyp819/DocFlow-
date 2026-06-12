package com.clinic.patientapp.data.model;

import java.util.Map;

public class ErrorResponse {
    private String message;
    private int status;
    private Map<String, String> errors;

    public ErrorResponse() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
