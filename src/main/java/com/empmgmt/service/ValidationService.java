package com.empmgmt.service;

import com.empmgmt.exception.ValidationException;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * All input-validation rules in one place, so the UI layer never has to
 * guess what "valid" means and the rules stay testable in isolation.
 */
public final class ValidationService {

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");

    private ValidationService() { }

    public static void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("fullName", "Full name is required.");
        }
        if (name.trim().length() < 2) {
            throw new ValidationException("fullName", "Full name must be at least 2 characters.");
        }
        if (!name.matches("[A-Za-z .'-]+")) {
            throw new ValidationException("fullName", "Full name may only contain letters, spaces, and . ' -");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", "Email is required.");
        }
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new ValidationException("email", "Enter a valid email address (e.g. name@example.com).");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("phone", "Phone number is required.");
        }
        if (!PHONE.matcher(phone.trim()).matches()) {
            throw new ValidationException("phone", "Phone number must be exactly 10 digits.");
        }
    }

    public static void validateRequired(String value, String fieldLabel, String fieldKey) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldKey, fieldLabel + " is required.");
        }
    }

    public static void validateDateOfJoining(LocalDate date) throws ValidationException {
        if (date == null) {
            throw new ValidationException("dateOfJoining", "Date of joining is required.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("dateOfJoining", "Date of joining cannot be in the future.");
        }
        if (date.isBefore(LocalDate.of(1970, 1, 1))) {
            throw new ValidationException("dateOfJoining", "Date of joining looks invalid.");
        }
    }

    public static void validateSalary(String rawSalary) throws ValidationException {
        if (rawSalary == null || rawSalary.trim().isEmpty()) {
            throw new ValidationException("basicSalary", "Basic salary is required.");
        }
        double value;
        try {
            value = Double.parseDouble(rawSalary.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("basicSalary", "Basic salary must be a number.");
        }
        if (value <= 0) {
            throw new ValidationException("basicSalary", "Basic salary must be greater than zero.");
        }
        if (value > 10_000_000) {
            throw new ValidationException("basicSalary", "Basic salary looks unrealistically high — please check it.");
        }
    }
}
