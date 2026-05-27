package com.parmy.parmy_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.parmy.parmy_backend.model.Project;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
	Optional<Project> findBySlug(String slug);
	boolean existsBySlug(String slug);
	boolean existsByTitleIgnoreCase(String title);
	List<Project> findAllByIsActiveTrueOrderByCreatedAtDesc();
	List<Project> findAllByOrderByCreatedAtDesc();
}
