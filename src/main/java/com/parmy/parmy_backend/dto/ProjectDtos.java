package com.parmy.parmy_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDtos {
    public static class ProjectRequest {
        public String title;
        public String slug; // optional; generate if missing
        public String shortDescription;
        public String description;
        public Double price;
        public String currency;
        public String category;
        public List<String> tags;
        public String thumbnailUrl;
        public Boolean active;
        // Abstract file is handled separately via multipart upload
    }

    public static class ProjectResponse {
        public String id;
        public String slug;
        public String title;
        public String shortDescription;
        public String description;
        public double price;
        public String currency;
        public String category;
        public List<String> tags;
        public String thumbnailUrl;
        public boolean active;
        public String createdBy;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;

        // Abstract file fields (only for authenticated users)
        public String abstractFileName;
        public String abstractFileType;
        public long abstractFileSize;
        public boolean hasAbstract; // Public indicator without exposing file details
    }
}
