package com.example.flyhas.controller;

import com.example.flyhas.dto.ChangePasswordRequest;
import com.example.flyhas.model.Customer;
import com.example.flyhas.repository.CustomerRepository;
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
public class CustomerProfileController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerProfileController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/customer")
    public ResponseEntity<Customer> getCustomerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        return optionalCustomer
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/customer")
    public ResponseEntity<String> updateCustomerProfile(
            @Valid @RequestBody Customer updatedCustomer,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Customer customer = optionalCustomer.get();
        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setNationalId(updatedCustomer.getNationalId());
        customer.setBirthDate(updatedCustomer.getBirthDate());

        customerRepository.save(customer);
        return ResponseEntity.ok("Customer profile updated successfully");
    }

    @PutMapping("/customer/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Customer customer = optionalCustomer.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);

        return ResponseEntity.ok("Password changed successfully");
    }
}
