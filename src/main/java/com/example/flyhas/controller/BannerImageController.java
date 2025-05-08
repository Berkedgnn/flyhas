package com.example.flyhas.controller;

import com.example.flyhas.model.BannerImage;
import com.example.flyhas.repository.BannerImageRepository;
import com.example.flyhas.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BannerImageController {

    private final BannerImageRepository bannerRepo;
    private final FileStorageService fileStorageService;

    public BannerImageController(BannerImageRepository bannerRepo,
            FileStorageService fileStorageService) {
        this.bannerRepo = bannerRepo;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/all")
    public List<BannerImage> listAll() {
        return bannerRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }
        String filename = fileStorageService.storeFile(image);
        BannerImage banner = new BannerImage();
        banner.setFilename(filename);
        banner.setIsHome(false);
        bannerRepo.save(banner);
        return ResponseEntity.status(HttpStatus.CREATED).body(banner);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return bannerRepo.findById(id).map(b -> {
            fileStorageService.deleteFile(b.getFilename());
            bannerRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename,
            HttpServletRequest request) {
        try {
            Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = request.getServletContext()
                    .getMimeType(resource.getFilename());
            if (contentType == null)
                contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/toggle-home")
    public ResponseEntity<?> toggleHome(@PathVariable Long id) {
        return bannerRepo.findById(id).map(banner -> {
            banner.setIsHome(!banner.isHome());
            bannerRepo.save(banner);
            return ResponseEntity.ok(banner);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/visible")
    public List<BannerImage> getVisibleBanners() {
        return bannerRepo.findAll().stream()
                .filter(BannerImage::isHome)
                .collect(Collectors.toList());
    }
}
