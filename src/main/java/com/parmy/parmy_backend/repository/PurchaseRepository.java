package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.Purchase;
import com.parmy.parmy_backend.model.PurchaseStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    List<Purchase> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Purchase> findByStatusOrderByCreatedAtDesc(PurchaseStatus status);
}
