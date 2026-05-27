package com.parmy.parmy_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "projects")
public class Project {
	@Id
	private String id;

	@Indexed(unique = true)
	private String slug; // URL-safe unique identifier

	private String title;
	private String shortDescription;
	private String description;

	private double price;
	private String currency = "INR"; // default currency

	private String category;
	private List<String> tags;
	private String thumbnailUrl;

	// Abstract file fields
	private String abstractFileName; // Original filename
	private String abstractFilePath; // Server file path
	private String abstractFileType; // MIME type
	private long abstractFileSize; // File size in bytes

	private boolean isActive = true;

	private String createdBy; // email or user id
	private String updatedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Project() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getAbstractFileName() {
		return abstractFileName;
	}

	public void setAbstractFileName(String abstractFileName) {
		this.abstractFileName = abstractFileName;
	}

	public String getAbstractFilePath() {
		return abstractFilePath;
	}

	public void setAbstractFilePath(String abstractFilePath) {
		this.abstractFilePath = abstractFilePath;
	}

	public String getAbstractFileType() {
		return abstractFileType;
	}

	public void setAbstractFileType(String abstractFileType) {
		this.abstractFileType = abstractFileType;
	}

	public long getAbstractFileSize() {
		return abstractFileSize;
	}

	public void setAbstractFileSize(long abstractFileSize) {
		this.abstractFileSize = abstractFileSize;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void touchUpdated() {
		this.updatedAt = LocalDateTime.now();
	}
}
