package com.example.flyhas.controller;

import com.example.flyhas.dto.SupportRequestDTO;
import com.example.flyhas.model.SupportRequest;
import com.example.flyhas.model.SupportStatus;
import com.example.flyhas.model.Customer;
import com.example.flyhas.repository.CustomerRepository;
import com.example.flyhas.repository.SupportRequestRepository;
import com.example.flyhas.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SupportRequestController {

    private final SupportRequestRepository supportRequestRepository;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public SupportRequestController(SupportRequestRepository supportRequestRepository,
            CustomerRepository customerRepository,
            FileStorageService fileStorageService) {
        this.supportRequestRepository = supportRequestRepository;
        this.customerRepository = customerRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> createSupportRequest(
            @RequestParam("errorCode") String errorCode,
            @RequestParam("message") String message,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        Customer customer = customerRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        SupportRequest req = new SupportRequest();
        req.setCustomer(customer);
        req.setErrorCode(errorCode);
        req.setMessage(message);

        if (image != null && !image.isEmpty()) {
            String filename = fileStorageService.storeFile(image);
            req.setScreenshot("/api/support/image/" + filename);
        }

        supportRequestRepository.save(req);
        return ResponseEntity
                .created(URI.create("/api/support/" + req.getId()))
                .body("Support request created.");
    }

    @GetMapping("/my")
    public ResponseEntity<List<SupportRequest>> getMySupportRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        Customer customer = customerRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<SupportRequest> list = supportRequestRepository.findByCustomerId(customer.getId());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupportRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return supportRequestRepository.findById(id).map(req -> {

            boolean isOwner = req.getCustomer().getEmail().equals(userDetails.getUsername());
            boolean isManagerOrAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")
                            || a.getAuthority().equals("ROLE_ADMIN"));

            if (!isOwner && !isManagerOrAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only delete your own requests, or be a Manager/Admin.");
            }

            supportRequestRepository.deleteById(id);
            return ResponseEntity.ok("Deleted.");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupportRequestDTO>> getAllRequests() {
        List<SupportRequestDTO> dtos = supportRequestRepository.findAll().stream()
                .map(r -> new SupportRequestDTO(
                        r.getId(),
                        r.getErrorCode(),
                        r.getMessage(),
                        r.getScreenshot(),
                        r.getManagerResponse(),
                        r.getStatus().name(),
                        r.getCustomer().getEmail(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/response")
    public ResponseEntity<?> respondToSupportRequest(
            @PathVariable Long id,
            @RequestBody String response) {

        return supportRequestRepository.findById(id).map(req -> {
            req.setManagerResponse(response);
            req.setStatus(SupportStatus.RESPONDED);
            supportRequestRepository.save(req);
            return ResponseEntity.ok("Response saved.");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename,
            HttpServletRequest request) {

        try {
            Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = request.getServletContext()
                    .getMimeType(resource.getFilename());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}