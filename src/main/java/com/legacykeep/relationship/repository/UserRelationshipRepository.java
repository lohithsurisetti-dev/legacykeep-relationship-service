package com.legacykeep.relationship.repository;

import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.entity.UserRelationship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserRelationship entity operations
 */
@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {

    /**
     * Find all relationships for a specific user
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    List<UserRelationship> findByUserId(@Param("userId") Long userId);

    /**
     * Find all relationships for a specific user with pagination
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    Page<UserRelationship> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find relationships between two specific users
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :user1Id AND ur.user2Id = :user2Id) OR " +
           "(ur.user1Id = :user2Id AND ur.user2Id = :user1Id)")
    List<UserRelationship> findRelationshipsBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find active relationships between two specific users
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "((ur.user1Id = :user1Id AND ur.user2Id = :user2Id) OR " +
           "(ur.user1Id = :user2Id AND ur.user2Id = :user1Id)) AND " +
           "ur.status = 'ACTIVE'")
    List<UserRelationship> findActiveRelationshipsBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find relationships by status
     */
    List<UserRelationship> findByStatus(UserRelationship.RelationshipStatus status);

    /**
     * Find relationships by status with pagination
     */
    Page<UserRelationship> findByStatus(UserRelationship.RelationshipStatus status, Pageable pageable);

    /**
     * Find relationships by relationship type
     */
    List<UserRelationship> findByRelationshipType(RelationshipType relationshipType);

    /**
     * Find relationships by relationship type with pagination
     */
    Page<UserRelationship> findByRelationshipType(RelationshipType relationshipType, Pageable pageable);

    /**
     * Find relationships by user and relationship type
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :userId OR ur.user2Id = :userId) AND " +
           "ur.relationshipType = :relationshipType")
    List<UserRelationship> findByUserIdAndRelationshipType(@Param("userId") Long userId, @Param("relationshipType") RelationshipType relationshipType);

    /**
     * Find relationships by user and status
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :userId OR ur.user2Id = :userId) AND " +
           "ur.status = :status")
    List<UserRelationship> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") UserRelationship.RelationshipStatus status);

    /**
     * Find relationships by user and status with pagination
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :userId OR ur.user2Id = :userId) AND " +
           "ur.status = :status")
    Page<UserRelationship> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") UserRelationship.RelationshipStatus status, Pageable pageable);

    /**
     * Find relationships by context ID
     */
    List<UserRelationship> findByContextId(Long contextId);

    /**
     * Find relationships by date range
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "ur.startDate BETWEEN :startDate AND :endDate OR " +
           "ur.endDate BETWEEN :startDate AND :endDate OR " +
           "(ur.startDate <= :startDate AND (ur.endDate IS NULL OR ur.endDate >= :endDate))")
    List<UserRelationship> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find relationships that are currently active (no end date or end date in future)
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE " +
           "ur.status = 'ACTIVE' AND " +
           "(ur.endDate IS NULL OR ur.endDate >= CURRENT_DATE)")
    List<UserRelationship> findCurrentlyActiveRelationships();

    /**
     * Count relationships for a user
     */
    @Query("SELECT COUNT(ur) FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Count active relationships for a user
     */
    @Query("SELECT COUNT(ur) FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :userId OR ur.user2Id = :userId) AND " +
           "ur.status = 'ACTIVE'")
    long countActiveByUserId(@Param("userId") Long userId);

    /**
     * Check if a relationship exists between two users
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :user1Id AND ur.user2Id = :user2Id) OR " +
           "(ur.user1Id = :user2Id AND ur.user2Id = :user1Id)")
    boolean existsBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Check if an active relationship exists between two users
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRelationship ur WHERE " +
           "((ur.user1Id = :user1Id AND ur.user2Id = :user2Id) OR " +
           "(ur.user1Id = :user2Id AND ur.user2Id = :user1Id)) AND " +
           "ur.status = 'ACTIVE'")
    boolean existsActiveBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
