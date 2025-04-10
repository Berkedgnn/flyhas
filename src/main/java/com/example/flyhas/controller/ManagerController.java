package com.example.flyhas.controller;

import com.example.flyhas.model.Manager;
import com.example.flyhas.repository.ManagerRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/managers")
@CrossOrigin(origins = "http://localhost:5173")
public class ManagerController {

    private final ManagerRepository managerRepository;

    public ManagerController(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerRepository.findAll();
        return ResponseEntity.ok(managers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteManager(@PathVariable Long id) {
        if (!managerRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager not found");
        }
        managerRepository.deleteById(id);
        return ResponseEntity.ok("Manager deleted successfully");
    }
}
