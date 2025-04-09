package com.example.flyhas.controller;

import com.example.flyhas.dto.AuthRequest;
import com.example.flyhas.dto.AuthResponse;
import com.example.flyhas.dto.CustomerRequest;
import com.example.flyhas.model.Admin;
import com.example.flyhas.model.BaseUser;
import com.example.flyhas.model.Customer;
import com.example.flyhas.model.Manager;
import com.example.flyhas.repository.AdminRepository;
import com.example.flyhas.repository.CustomerRepository;
import com.example.flyhas.repository.ManagerRepository;
import com.example.flyhas.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            CustomerRepository customerRepository,
            ManagerRepository managerRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.customerRepository = customerRepository;
        this.managerRepository = managerRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        BaseUser baseUser = findBaseUserByEmail(authRequest.getEmail());
        if (baseUser == null) {
            throw new RuntimeException("User not found");
        }

        String token = jwtTokenUtil.generateToken(baseUser);
        String role = jwtTokenUtil.extractRole(token);
        String firstName = baseUser.getFirstName();

        AuthResponse authResponse = new AuthResponse(token, baseUser.getEmail(), role, firstName);
        return ResponseEntity.ok(authResponse);
    }

    private BaseUser findBaseUserByEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent())
            return customerOpt.get();

        Optional<Manager> managerOpt = managerRepository.findByEmail(email);
        if (managerOpt.isPresent())
            return managerOpt.get();

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent())
            return adminOpt.get();

        return null;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponse> registerCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        customerRepository.save(customer);

        String token = jwtTokenUtil.generateToken(customer);
        AuthResponse response = new AuthResponse(token, customer.getEmail(), "CUSTOMER", customer.getFirstName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/manager")
    public ResponseEntity<String> registerManager(@Valid @RequestBody Manager manager) {
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        managerRepository.save(manager);
        return ResponseEntity.ok("Manager registered successfully!");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return ResponseEntity.ok("Admin registered successfully!");
    }
}