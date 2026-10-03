package com.empmgmt.service;

import com.empmgmt.model.Employee;
import com.empmgmt.model.PayrollRecord;

import java.time.LocalDate;
import java.time.Month;

/**
 * Pure business logic for turning an Employee's basic salary into a full
 * payroll breakdown. No database or UI code lives here on purpose —
 * it can be unit-tested with nothing but plain objects.
 *
 * Simplified Indian-payroll style rules, easy to retune in one place:
 *   HRA                = 40% of basic
 *   DA (Dearness Allow) = 10% of basic
 *   Medical allowance   = flat ₹1,250
 *   Provident Fund      = 12% of basic
 *   Professional Tax    = flat ₹200/month
 *   Income Tax          = simplified monthly slab on gross earnings
 */
public class PayrollService {

    private static final double HRA_RATE = 0.40;
    private static final double DA_RATE = 0.10;
    private static final double MEDICAL_ALLOWANCE = 1250.0;
    private static final double PF_RATE = 0.12;
    private static final double PROFESSIONAL_TAX = 200.0;

    public PayrollRecord calculate(Employee employee, Month month, int year) {
        double basic = employee.getBasicSalary();
        double hra = round(basic * HRA_RATE);
        double da = round(basic * DA_RATE);
        double medical = MEDICAL_ALLOWANCE;
        double gross = round(basic + hra + da + medical);

        double pf = round(basic * PF_RATE);
        double professionalTax = PROFESSIONAL_TAX;
        double incomeTax = round(estimateMonthlyIncomeTax(gross));
        double totalDeductions = round(pf + professionalTax + incomeTax);
        double net = round(gross - totalDeductions);

        PayrollRecord r = new PayrollRecord();
        r.setEmployeeId(employee.getId());
        r.setEmployeeName(employee.getFullName());
        r.setMonth(month);
        r.setYear(year);
        r.setBasicSalary(basic);
        r.setHra(hra);
        r.setDa(da);
        r.setMedicalAllowance(medical);
        r.setGrossEarnings(gross);
        r.setProvidentFund(pf);
        r.setProfessionalTax(professionalTax);
        r.setIncomeTax(incomeTax);
        r.setTotalDeductions(totalDeductions);
        r.setNetPay(net);
        r.setGeneratedOn(LocalDate.now());
        return r;
    }

    /** Very simplified monthly slab, purely illustrative — not real tax advice. */
    private double estimateMonthlyIncomeTax(double monthlyGross) {
        double annualGross = monthlyGross * 12;
        double annualTax;
        if (annualGross <= 500_000) {
            annualTax = 0;
        } else if (annualGross <= 1_000_000) {
            annualTax = (annualGross - 500_000) * 0.10;
        } else if (annualGross <= 1_500_000) {
            annualTax = 50_000 + (annualGross - 1_000_000) * 0.20;
        } else {
            annualTax = 150_000 + (annualGross - 1_500_000) * 0.30;
        }
        return annualTax / 12.0;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
