package com.example.flyhas.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String role;
    private String firstName;

    public AuthResponse(String token, String email, String role, String firstName) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
