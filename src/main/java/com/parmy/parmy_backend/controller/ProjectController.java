package com.parmy.parmy_backend.controller;

import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.ProjectDtos.ProjectResponse;
import com.parmy.parmy_backend.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = { "https://parmytechnologies.netlify.app", "http://localhost:5173",
        "http://localhost:3000" }, allowCredentials = "true")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> list() {
        return ResponseEntity.ok(projectService.publicList());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(projectService.getPublicBySlug(slug));
    }

    /**
     * Download abstract file for a project (authenticated users only)
     */
    @GetMapping("/{id}/abstract/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadAbstract(@PathVariable String id) {
        try {
            ApiResponse<Path> pathResponse = projectService.getAbstractFilePath(id);
            if (!pathResponse.isSuccess()) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = pathResponse.getData();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Determine content type
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                // Get filename from path
                String filename = filePath.getFileName().toString();

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
