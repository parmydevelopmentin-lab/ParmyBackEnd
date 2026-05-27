package com.parmy.parmy_backend.service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.ProjectDtos.ProjectRequest;
import com.parmy.parmy_backend.dto.ProjectDtos.ProjectResponse;
import com.parmy.parmy_backend.model.Project;
import com.parmy.parmy_backend.repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Value("${project.abstract.storage-dir:project_abstracts}")
    private String abstractStorageDir;

    public ApiResponse<ProjectResponse> create(ProjectRequest req, String createdBy) {
        try {
            String slug = (req.slug == null || req.slug.isBlank()) ? slugify(req.title) : slugify(req.slug);
            if (projectRepository.existsBySlug(slug)) {
                return ApiResponse.error("Slug already exists");
            }
            Project project = new Project();
            project.setTitle(req.title);
            project.setSlug(slug);
            project.setShortDescription(req.shortDescription);
            project.setDescription(req.description);
            project.setPrice(req.price != null ? req.price : 0);
            if (req.currency != null)
                project.setCurrency(req.currency);
            project.setCategory(req.category);
            project.setTags(req.tags);
            project.setThumbnailUrl(req.thumbnailUrl);
            project.setActive(req.active == null ? true : req.active);
            project.setCreatedBy(createdBy);
            project.setUpdatedBy(createdBy);
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());
            projectRepository.save(project);
            return ApiResponse.success("Project created", toResponse(project));
        } catch (Exception e) {
            return ApiResponse.error("Failed to create project: " + e.getMessage());
        }
    }

    public ApiResponse<List<ProjectResponse>> adminList() {
        try {
            List<ProjectResponse> list = projectRepository.findAllByOrderByCreatedAtDesc()
                    .stream().map(this::toResponse).collect(Collectors.toList());
            return ApiResponse.success("Projects retrieved", list);
        } catch (Exception e) {
            return ApiResponse.error("Failed to list projects: " + e.getMessage());
        }
    }

    public ApiResponse<List<ProjectResponse>> publicList() {
        try {
            List<ProjectResponse> list = projectRepository.findAllByIsActiveTrueOrderByCreatedAtDesc()
                    .stream().map(this::toResponse).collect(Collectors.toList());
            return ApiResponse.success("Projects retrieved", list);
        } catch (Exception e) {
            return ApiResponse.error("Failed to list projects: " + e.getMessage());
        }
    }

    public ApiResponse<ProjectResponse> update(String id, ProjectRequest req, String updatedBy) {
        try {
            Optional<Project> opt = projectRepository.findById(id);
            if (opt.isEmpty())
                return ApiResponse.error("Project not found");
            Project p = opt.get();
            if (req.title != null)
                p.setTitle(req.title);
            if (req.slug != null && !req.slug.isBlank()) {
                String newSlug = slugify(req.slug);
                if (!newSlug.equals(p.getSlug()) && projectRepository.existsBySlug(newSlug)) {
                    return ApiResponse.error("Slug already exists");
                }
                p.setSlug(newSlug);
            }
            if (req.shortDescription != null)
                p.setShortDescription(req.shortDescription);
            if (req.description != null)
                p.setDescription(req.description);
            if (req.price != null)
                p.setPrice(req.price);
            if (req.currency != null)
                p.setCurrency(req.currency);
            if (req.category != null)
                p.setCategory(req.category);
            if (req.tags != null)
                p.setTags(req.tags);
            if (req.thumbnailUrl != null)
                p.setThumbnailUrl(req.thumbnailUrl);
            if (req.active != null)
                p.setActive(req.active);
            p.setUpdatedBy(updatedBy);
            p.setUpdatedAt(LocalDateTime.now());
            projectRepository.save(p);
            return ApiResponse.success("Project updated", toResponse(p));
        } catch (Exception e) {
            return ApiResponse.error("Failed to update project: " + e.getMessage());
        }
    }

    public ApiResponse<String> delete(String id) {
        try {
            if (!projectRepository.existsById(id)) {
                return ApiResponse.error("Project not found");
            }
            projectRepository.deleteById(id);
            return ApiResponse.success("Project deleted", id);
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete project: " + e.getMessage());
        }
    }

    public ApiResponse<ProjectResponse> getPublicBySlug(String slug) {
        try {
            Optional<Project> opt = projectRepository.findBySlug(slug);
            if (opt.isEmpty() || !opt.get().isActive())
                return ApiResponse.error("Project not found");
            return ApiResponse.success("Project retrieved", toResponse(opt.get()));
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve project: " + e.getMessage());
        }
    }

    private ProjectResponse toResponse(Project p) {
        ProjectResponse r = new ProjectResponse();
        r.id = p.getId();
        r.slug = p.getSlug();
        r.title = p.getTitle();
        r.shortDescription = p.getShortDescription();
        r.description = p.getDescription();
        r.price = p.getPrice();
        r.currency = p.getCurrency();
        r.category = p.getCategory();
        r.tags = p.getTags();
        r.thumbnailUrl = p.getThumbnailUrl();
        r.active = p.isActive();
        r.createdBy = p.getCreatedBy();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();

        // Abstract file information
        r.hasAbstract = p.getAbstractFileName() != null && !p.getAbstractFileName().isEmpty();
        r.abstractFileName = p.getAbstractFileName();
        r.abstractFileType = p.getAbstractFileType();
        r.abstractFileSize = p.getAbstractFileSize();

        return r;
    }

    private String slugify(String input) {
        String nowhitespace = input.trim().replaceAll("[\\s_]+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^a-zA-Z0-9-]", "").toLowerCase(Locale.ROOT);
        return slug;
    }

    /**
     * Upload abstract file for a project
     */
    public ApiResponse<String> uploadAbstractFile(String projectId, MultipartFile file, String updatedBy) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }

            Project project = projectOpt.get();

            // Validate file
            if (file.isEmpty()) {
                return ApiResponse.error("File is empty");
            }

            // Check file size (limit to 500MB)
            if (file.getSize() > 500 * 1024 * 1024) {
                return ApiResponse.error("File size exceeds 500MB limit");
            }

            // Validate file type (allow common document formats)
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("application/pdf") &&
                    !contentType.equals("application/msword") &&
                    !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") &&
                    !contentType.equals("text/plain"))) {
                return ApiResponse.error("Only PDF, DOC, DOCX, and TXT files are allowed");
            }

            // Create storage directory if it doesn't exist
            Path storageDir = Paths.get(abstractStorageDir);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = project.getSlug() + "_abstract_" + System.currentTimeMillis() + fileExtension;
            Path filePath = storageDir.resolve(uniqueFilename);

            // Delete old abstract file if exists
            if (project.getAbstractFilePath() != null) {
                Path oldFilePath = Paths.get(project.getAbstractFilePath());
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            }

            // Save new file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update project with file information
            project.setAbstractFileName(originalFilename);
            project.setAbstractFilePath(filePath.toString());
            project.setAbstractFileType(contentType);
            project.setAbstractFileSize(file.getSize());
            project.setUpdatedBy(updatedBy);
            project.touchUpdated();

            projectRepository.save(project);

            return ApiResponse.success("Abstract file uploaded successfully", uniqueFilename);

        } catch (IOException e) {
            return ApiResponse.error("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Error uploading abstract file: " + e.getMessage());
        }
    }

    /**
     * Get abstract file path for download (authenticated users only)
     */
    public ApiResponse<Path> getAbstractFilePath(String projectId) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }

            Project project = projectOpt.get();
            if (project.getAbstractFilePath() == null || project.getAbstractFilePath().isEmpty()) {
                return ApiResponse.error("No abstract file available for this project");
            }

            Path filePath = Paths.get(project.getAbstractFilePath());
            if (!Files.exists(filePath)) {
                return ApiResponse.error("Abstract file not found on server");
            }

            return ApiResponse.success("Abstract file found", filePath);

        } catch (Exception e) {
            return ApiResponse.error("Error retrieving abstract file: " + e.getMessage());
        }
    }

    /**
     * Delete abstract file for a project
     */
    public ApiResponse<String> deleteAbstractFile(String projectId, String updatedBy) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }

            Project project = projectOpt.get();
            if (project.getAbstractFilePath() == null) {
                return ApiResponse.error("No abstract file to delete");
            }

            // Delete physical file
            Path filePath = Paths.get(project.getAbstractFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Clear abstract file information from project
            project.setAbstractFileName(null);
            project.setAbstractFilePath(null);
            project.setAbstractFileType(null);
            project.setAbstractFileSize(0);
            project.setUpdatedBy(updatedBy);
            project.touchUpdated();

            projectRepository.save(project);

            return ApiResponse.success("Abstract file deleted successfully", "");

        } catch (IOException e) {
            return ApiResponse.error("Failed to delete file: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Error deleting abstract file: " + e.getMessage());
        }
    }
}
