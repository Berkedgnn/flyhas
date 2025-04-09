package com.example.flyhas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends BaseUser {

    private String nationalId;

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    // Getter and Setter
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
}
