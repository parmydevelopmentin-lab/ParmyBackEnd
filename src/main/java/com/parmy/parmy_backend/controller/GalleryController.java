package com.parmy.parmy_backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.model.GalleryItem;
import com.parmy.parmy_backend.repository.GalleryItemRepository;

@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = { "https://parmytechnologies.netlify.app", "http://localhost:5173",
        "http://localhost:3000" }, allowCredentials = "true")
public class GalleryController {

    @Autowired
    private GalleryItemRepository galleryRepository;

    private final String uploadDir = "./gallery_uploads/";

    @GetMapping
    public ResponseEntity<ApiResponse<List<GalleryItem>>> list() {
        List<GalleryItem> items = galleryRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(new ApiResponse<>(true, "Gallery fetched successfully", items));
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GalleryItem>> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description) {
        
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Image file is required", null));
            }

            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String originalName = file.getOriginalFilename();
            String ext = Optional.ofNullable(originalName)
                    .filter(name -> name.contains("."))
                    .map(name -> name.substring(name.lastIndexOf('.')))
                    .orElse(".jpg");

            String filename = "gallery_" + UUID.randomUUID() + ext;
            Path dest = dir.resolve(filename);
            Files.copy(file.getInputStream(), dest);

            // Construct accessible URL (using relative /gallery_uploads/filename)
            String imageUrl = "/gallery_uploads/" + filename;

            GalleryItem item = new GalleryItem();
            item.setSrc(imageUrl);
            item.setThumb(imageUrl); // use same for thumbnail
            item.setTitle(title);
            item.setCategory(category);
            item.setDescription(description);
            item.setCreatedAt(LocalDateTime.now());

            GalleryItem saved = galleryRepository.save(item);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Image uploaded successfully", saved));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to upload image: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        Optional<GalleryItem> optional = galleryRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Gallery item not found", null));
        }

        GalleryItem item = optional.get();
        // Delete the physical file if it is a local upload
        try {
            String filepath = item.getSrc();
            if (filepath != null && !filepath.startsWith("http://") && !filepath.startsWith("https://")) {
                if (filepath.startsWith("/")) {
                    filepath = "." + filepath;
                }
                Path path = Paths.get(filepath);
                Files.deleteIfExists(path);
            }
        } catch (Exception e) {
            System.err.println("Failed to delete physical file: " + e.getMessage());
        }

        galleryRepository.delete(item);
        return ResponseEntity.ok(new ApiResponse<>(true, "Gallery item deleted successfully", id));
    }
}
