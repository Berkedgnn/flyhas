package com.example.flyhas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "admins")
public class Admin extends BaseUser {

    @NotBlank(message = "Employee number is required")
    private String employeeNumber;

    @NotBlank(message = "National ID is required")
    @Column(unique = true)
    private String nationalId;

    @NotBlank(message = "Department is required")
    private String department;

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
