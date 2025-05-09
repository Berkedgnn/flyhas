
package com.example.flyhas.controller;

import com.example.flyhas.model.City;
import com.example.flyhas.repository.CityRepository;
import com.example.flyhas.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CityController {

    private final CityRepository cityRepository;
    private final FileStorageService fileStorageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public CityController(CityRepository cityRepository,
            FileStorageService fileStorageService) {
        this.cityRepository = cityRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        List<City> cities = cityRepository.findAll();
        return ResponseEntity.ok(cities);
    }

    @PostMapping
    public ResponseEntity<City> addCity(
            @RequestParam("name") String name,
            @RequestParam("country") String country,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        City city = new City();
        city.setName(name);
        city.setCountry(country);

        if (image != null && !image.isEmpty()) {
            String filename = fileStorageService.storeFile(image);
            city.setImagePath("/api/cities/image/" + filename);
        }

        City saved = cityRepository.save(city);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        if (!cityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cityRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> serveImage(
            @PathVariable String filename,
            HttpServletRequest request) {
        try {
            Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = request.getServletContext().getMimeType(resource.getFilename());
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