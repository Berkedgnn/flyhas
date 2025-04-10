package com.example.flyhas.controller;

import com.example.flyhas.model.Customer;
import com.example.flyhas.repository.CustomerRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok(customers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
        customerRepository.deleteById(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }
}
