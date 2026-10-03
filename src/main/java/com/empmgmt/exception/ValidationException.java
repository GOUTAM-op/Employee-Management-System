package com.empmgmt.exception;

/**
 * Thrown when user-supplied input fails business validation rules
 * (bad email, negative salary, empty required field, etc).
 * Checked, because callers are expected to catch it and show the
 * message to the user rather than let it bubble up as a crash.
 */
public class ValidationException extends Exception {

    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() { return fieldName; }
}
