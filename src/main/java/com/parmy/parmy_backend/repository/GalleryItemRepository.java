package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.GalleryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GalleryItemRepository extends MongoRepository<GalleryItem, String> {
    List<GalleryItem> findAllByOrderByCreatedAtDesc();
}
