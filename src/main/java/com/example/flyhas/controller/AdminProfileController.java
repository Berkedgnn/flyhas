package com.example.flyhas.controller;

import com.example.flyhas.dto.ChangePasswordRequest;
import com.example.flyhas.model.Admin;
import com.example.flyhas.repository.AdminRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProfileController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminProfileController(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin")
    public ResponseEntity<Admin> getAdminProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        return optionalAdmin
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/admin")
    public ResponseEntity<String> updateAdminProfile(
            @Valid @RequestBody Admin updatedAdmin,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }

        Admin admin = optionalAdmin.get();
        admin.setFirstName(updatedAdmin.getFirstName());
        admin.setLastName(updatedAdmin.getLastName());
        admin.setBirthDate(updatedAdmin.getBirthDate());
        admin.setDepartment(updatedAdmin.getDepartment());
        admin.setNationalId(updatedAdmin.getNationalId());
        admin.setEmployeeNumber(updatedAdmin.getEmployeeNumber());

        adminRepository.save(admin);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PutMapping("/admin/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }

        Admin admin = optionalAdmin.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);

        return ResponseEntity.ok("Password changed successfully");
    }
}