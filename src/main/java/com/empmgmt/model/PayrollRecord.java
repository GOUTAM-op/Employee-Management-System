package com.empmgmt.model;

import java.time.LocalDate;
import java.time.Month;

/**
 * A single computed payroll run for one employee, for one month/year.
 */
public class PayrollRecord {

    private int id;
    private int employeeId;
    private String employeeName;   // denormalised for easy display
    private Month month;
    private int year;

    private double basicSalary;
    private double hra;
    private double da;
    private double medicalAllowance;
    private double grossEarnings;

    private double providentFund;
    private double professionalTax;
    private double incomeTax;
    private double totalDeductions;

    private double netPay;
    private LocalDate generatedOn;

    public PayrollRecord() { this.generatedOn = LocalDate.now(); }

    // --- getters / setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Month getMonth() { return month; }
    public void setMonth(Month month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getHra() { return hra; }
    public void setHra(double hra) { this.hra = hra; }

    public double getDa() { return da; }
    public void setDa(double da) { this.da = da; }

    public double getMedicalAllowance() { return medicalAllowance; }
    public void setMedicalAllowance(double medicalAllowance) { this.medicalAllowance = medicalAllowance; }

    public double getGrossEarnings() { return grossEarnings; }
    public void setGrossEarnings(double grossEarnings) { this.grossEarnings = grossEarnings; }

    public double getProvidentFund() { return providentFund; }
    public void setProvidentFund(double providentFund) { this.providentFund = providentFund; }

    public double getProfessionalTax() { return professionalTax; }
    public void setProfessionalTax(double professionalTax) { this.professionalTax = professionalTax; }

    public double getIncomeTax() { return incomeTax; }
    public void setIncomeTax(double incomeTax) { this.incomeTax = incomeTax; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }

    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }

    public LocalDate getGeneratedOn() { return generatedOn; }
    public void setGeneratedOn(LocalDate generatedOn) { this.generatedOn = generatedOn; }
}
