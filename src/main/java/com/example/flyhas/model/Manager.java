package com.example.flyhas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "managers")
public class Manager extends BaseUser {

    @Column(nullable = false, unique = true)
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
