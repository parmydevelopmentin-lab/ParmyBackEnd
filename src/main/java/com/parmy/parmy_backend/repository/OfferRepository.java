package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.Offer;
import com.parmy.parmy_backend.model.OfferStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {
    
    // Find by candidate email
    Optional<Offer> findByCandidateEmail(String candidateEmail);
    
    // Find all offers by status
    List<Offer> findByStatus(OfferStatus status);
    
    // Find offers by role
    List<Offer> findByRoleContainingIgnoreCase(String role);
    
    // Find offers by created by (admin)
    List<Offer> findByCreatedBy(String createdBy);
    
    // Find offers by joining date range
    List<Offer> findByJoiningDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find offers created between dates
    List<Offer> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    // Find offers by candidate name (case insensitive search)
    List<Offer> findByCandidateNameContainingIgnoreCase(String candidateName);
    
    // Find offers by location
    List<Offer> findByLocationContainingIgnoreCase(String location);
    
    // Find sent offers (email sent)
    List<Offer> findByEmailSentTrue();
    
    // Find unsent offers (email not sent)
    List<Offer> findByEmailSentFalse();
    
    // Find offers sent in a date range
    List<Offer> findByEmailSentAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    // Count offers by status
    long countByStatus(OfferStatus status);
    
    // Count offers created by admin
    long countByCreatedBy(String createdBy);
    
    // Custom query to find offers by multiple criteria
    @Query("{ $and: [ " +
           "{ $or: [ { 'candidateName': { $regex: ?0, $options: 'i' } }, " +
           "        { 'candidateEmail': { $regex: ?0, $options: 'i' } }, " +
           "        { 'role': { $regex: ?0, $options: 'i' } } ] }, " +
           "{ $or: [ { 'status': ?1 }, { ?1: null } ] } ] }")
    List<Offer> searchOffers(String searchTerm, OfferStatus status);
    
    // Find recent offers (created in last N days)
    @Query("{ 'createdAt': { $gte: ?0 } }")
    List<Offer> findRecentOffers(LocalDateTime dateTime);
    
    // Find expired offers (joining date passed and status not accepted)
    @Query("{ $and: [ { 'joiningDate': { $lt: ?0 } }, " +
           "{ 'status': { $nin: ['ACCEPTED', 'EXPIRED'] } } ] }")
    List<Offer> findExpiredOffers(LocalDate currentDate);
    
    // Check if candidate already has an active offer
    @Query("{ $and: [ { 'candidateEmail': ?0 }, " +
           "{ 'status': { $in: ['DRAFT', 'SENT'] } } ] }")
    Optional<Offer> findActiveByCandidateEmail(String candidateEmail);
    
    // Find offers by status and created by
    List<Offer> findByStatusAndCreatedBy(OfferStatus status, String createdBy);
    
    // Find offers sorted by creation date (most recent first)
    List<Offer> findAllByOrderByCreatedAtDesc();
    
    // Find offers by role and status
    List<Offer> findByRoleContainingIgnoreCaseAndStatus(String role, OfferStatus status);
    
    // Find offers by location and status
    List<Offer> findByLocationContainingIgnoreCaseAndStatus(String location, OfferStatus status);
}