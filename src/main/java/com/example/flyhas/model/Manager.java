package com.example.flyhas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "managers")
public class Manager extends BaseUser {

    @NotBlank(message = "Employee number is required")
    private String employeeNumber;

    @Override
    public String getRole() {
        return "MANAGER";
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
