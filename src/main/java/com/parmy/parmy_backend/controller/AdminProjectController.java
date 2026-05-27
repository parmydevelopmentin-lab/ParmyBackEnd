package com.parmy.parmy_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.ProjectDtos.ProjectRequest;
import com.parmy.parmy_backend.dto.ProjectDtos.ProjectResponse;
import com.parmy.parmy_backend.service.ProjectService;
import com.parmy.parmy_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/projects")
@CrossOrigin(origins = { "https://parmytechnologies.netlify.app", "http://localhost:5173",
        "http://localhost:3000" }, allowCredentials = "true")
public class AdminProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectResponse>> create(@Valid @RequestBody ProjectRequest req,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        String email = jwtUtil.extractEmail(token);
        ApiResponse<ProjectResponse> resp = projectService.create(req, email);
        return ResponseEntity.status(resp.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(resp);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> list() {
        ApiResponse<List<ProjectResponse>> resp = projectService.adminList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectResponse>> update(@PathVariable String id, @RequestBody ProjectRequest req,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        String email = jwtUtil.extractEmail(token);
        ApiResponse<ProjectResponse> resp = projectService.update(id, req, email);
        return ResponseEntity.status(resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(resp);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        ApiResponse<String> resp = projectService.delete(id);
        return ResponseEntity.status(resp.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(resp);
    }

    /**
     * Upload abstract file for a project
     */
    @PostMapping("/{id}/abstract")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadAbstract(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        String email = jwtUtil.extractEmail(token);
        ApiResponse<String> resp = projectService.uploadAbstractFile(id, file, email);
        return ResponseEntity.status(resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(resp);
    }

    /**
     * Delete abstract file for a project
     */
    @DeleteMapping("/{id}/abstract")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAbstract(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        String email = jwtUtil.extractEmail(token);
        ApiResponse<String> resp = projectService.deleteAbstractFile(id, email);
        return ResponseEntity.status(resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(resp);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
