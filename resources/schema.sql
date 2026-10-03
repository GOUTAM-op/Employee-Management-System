-- Employee Management & Payroll System — database schema
-- Run this once, or let the app create it for you automatically on first launch.

CREATE DATABASE IF NOT EXISTS employee_payroll_db;
USE employee_payroll_db;

CREATE TABLE IF NOT EXISTS employees (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    full_name        VARCHAR(120) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    phone            VARCHAR(20)  NOT NULL,
    department       VARCHAR(80)  NOT NULL,
    designation      VARCHAR(80)  NOT NULL,
    date_of_joining  DATE         NOT NULL,
    basic_salary     DECIMAL(12,2) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS payroll_records (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    employee_id         INT NOT NULL,
    month               VARCHAR(15) NOT NULL,
    year                INT NOT NULL,
    basic_salary        DECIMAL(12,2) NOT NULL,
    hra                 DECIMAL(12,2) NOT NULL,
    da                  DECIMAL(12,2) NOT NULL,
    medical_allowance   DECIMAL(12,2) NOT NULL,
    gross_earnings      DECIMAL(12,2) NOT NULL,
    provident_fund      DECIMAL(12,2) NOT NULL,
    professional_tax    DECIMAL(12,2) NOT NULL,
    income_tax          DECIMAL(12,2) NOT NULL,
    total_deductions    DECIMAL(12,2) NOT NULL,
    net_pay             DECIMAL(12,2) NOT NULL,
    generated_on        DATE NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE KEY unique_payroll_run (employee_id, month, year)
);
