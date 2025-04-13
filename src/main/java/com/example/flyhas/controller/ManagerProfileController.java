package com.example.flyhas.controller;

import com.example.flyhas.dto.ChangePasswordRequest;
import com.example.flyhas.model.Manager;
import com.example.flyhas.repository.ManagerRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class ManagerProfileController {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerProfileController(ManagerRepository managerRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // (GET /api/profile/manager)
    @GetMapping("/manager")
    public ResponseEntity<Manager> getManagerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Manager> optionalManager = managerRepository.findByEmail(email);

        return optionalManager
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // (PUT /api/profile/manager)
    @PutMapping("/manager")
    public ResponseEntity<String> updateManagerProfile(
            @Valid @RequestBody Manager updatedManager,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Manager> optionalManager = managerRepository.findByEmail(email);

        if (optionalManager.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager not found");
        }

        Manager manager = optionalManager.get();
        manager.setFirstName(updatedManager.getFirstName());
        manager.setLastName(updatedManager.getLastName());
        manager.setBirthDate(updatedManager.getBirthDate());
        manager.setEmployeeNumber(updatedManager.getEmployeeNumber());

        managerRepository.save(manager);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // (PUT /api/profile/manager/password)
    @PutMapping("/manager/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Manager> optionalManager = managerRepository.findByEmail(email);

        if (optionalManager.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager not found");
        }

        Manager manager = optionalManager.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), manager.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        manager.setPassword(passwordEncoder.encode(request.getNewPassword()));
        managerRepository.save(manager);

        return ResponseEntity.ok("Password changed successfully");
    }
}
