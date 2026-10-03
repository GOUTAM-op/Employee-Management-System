package com.empmgmt.model;

import java.time.LocalDate;

/**
 * Represents an employee record.
 * Pure data holder (POJO) — validation happens in the service layer,
 * never inside the model itself.
 */
public class Employee {

    public enum Status { ACTIVE, ON_LEAVE, TERMINATED }

    private int id;                 // 0 => not yet persisted
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private double basicSalary;
    private Status status;

    public Employee() {
        this.status = Status.ACTIVE;
    }

    public Employee(int id, String fullName, String email, String phone, String department,
                     String designation, LocalDate dateOfJoining, double basicSalary, Status status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.dateOfJoining = dateOfJoining;
        this.basicSalary = basicSalary;
        this.status = status == null ? Status.ACTIVE : status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return fullName + "  (#" + id + ")";
    }
}
