package com.legacykeep.relationship.repository;

import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.enums.RelationshipCategory;
import com.legacykeep.relationship.enums.RelationshipStatus;
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
 * Repository interface for UserRelationship entity.
 * Provides data access methods for user relationships.
 */
@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {

    /**
     * Find relationships where user1 is the specified user
     */
    List<UserRelationship> findByUser1Id(Long user1Id);

    /**
     * Find relationships where user2 is the specified user
     */
    List<UserRelationship> findByUser2Id(Long user2Id);

    /**
     * Find relationships where user1 is the specified user with pagination
     */
    Page<UserRelationship> findByUser1Id(Long user1Id, Pageable pageable);

    /**
     * Find relationships where user2 is the specified user with pagination
     */
    Page<UserRelationship> findByUser2Id(Long user2Id, Pageable pageable);

    /**
     * Find relationships where user1 is the specified user and status
     */
    List<UserRelationship> findByUser1IdAndStatus(Long user1Id, RelationshipStatus status);

    /**
     * Find relationships where user2 is the specified user and status
     */
    List<UserRelationship> findByUser2IdAndStatus(Long user2Id, RelationshipStatus status);

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
     * Find relationships by relationship type
     */
    List<UserRelationship> findByRelationshipType(RelationshipType relationshipType);

    /**
     * Find relationships by relationship type with pagination
     */
    Page<UserRelationship> findByRelationshipType(RelationshipType relationshipType, Pageable pageable);

    /**
     * Find relationships by relationship type and status
     */
    List<UserRelationship> findByRelationshipTypeAndStatus(RelationshipType relationshipType, RelationshipStatus status);

    /**
     * Find relationships by context ID
     */
    List<UserRelationship> findByContextId(Long contextId);

    /**
     * Find relationships by context ID and status
     */
    List<UserRelationship> findByContextIdAndStatus(Long contextId, RelationshipStatus status);

    /**
     * Find relationships by status
     */
    List<UserRelationship> findByStatus(RelationshipStatus status);

    /**
     * Find relationships by status with pagination
     */
    Page<UserRelationship> findByStatus(RelationshipStatus status, Pageable pageable);

    /**
     * Find relationships that started on or after a specific date
     */
    List<UserRelationship> findByStartDateGreaterThanEqual(LocalDate startDate);

    /**
     * Find relationships that ended on or before a specific date
     */
    List<UserRelationship> findByEndDateLessThanEqual(LocalDate endDate);

    /**
     * Find relationships that are currently active (no end date or end date in future)
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.status = 'ACTIVE' AND " +
           "(ur.endDate IS NULL OR ur.endDate > CURRENT_DATE)")
    List<UserRelationship> findCurrentlyActiveRelationships();

    /**
     * Find relationships that ended on a specific date
     */
    List<UserRelationship> findByEndDate(LocalDate endDate);

    /**
     * Find relationships that started on a specific date
     */
    List<UserRelationship> findByStartDate(LocalDate startDate);

    /**
     * Find all relationships for a user (both as user1 and user2)
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    List<UserRelationship> findAllRelationshipsForUser(@Param("userId") Long userId);

    /**
     * Find all relationships for a user with pagination
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    Page<UserRelationship> findAllRelationshipsForUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find active relationships for a user
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE (ur.user1Id = :userId OR ur.user2Id = :userId) AND ur.status = 'ACTIVE'")
    List<UserRelationship> findActiveRelationshipsForUser(@Param("userId") Long userId);

    /**
     * Find relationships by category for a user
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE (ur.user1Id = :userId OR ur.user2Id = :userId) AND ur.relationshipType.category = :category")
    List<UserRelationship> findRelationshipsByCategoryForUser(@Param("userId") Long userId, @Param("category") RelationshipCategory category);

    /**
     * Count relationships for a user
     */
    @Query("SELECT COUNT(ur) FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    long countRelationshipsForUser(@Param("userId") Long userId);

    /**
     * Count active relationships for a user
     */
    @Query("SELECT COUNT(ur) FROM UserRelationship ur WHERE (ur.user1Id = :userId OR ur.user2Id = :userId) AND ur.status = 'ACTIVE'")
    long countActiveRelationshipsForUser(@Param("userId") Long userId);

    /**
     * Check if relationship exists between two users
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRelationship ur WHERE " +
           "(ur.user1Id = :user1Id AND ur.user2Id = :user2Id) OR " +
           "(ur.user1Id = :user2Id AND ur.user2Id = :user1Id)")
    boolean existsRelationshipBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Check if active relationship exists between two users
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRelationship ur WHERE " +
           "((ur.user1Id = :user1Id AND ur.user2Id = :user2Id) OR " +
           "(ur.user1Id = :user2Id AND ur.user2Id = :user1Id)) AND " +
           "ur.status = 'ACTIVE'")
    boolean existsActiveRelationshipBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find relationships with metadata containing specific key
     */
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.metadata LIKE CONCAT('%', :key, '%')")
    List<UserRelationship> findByMetadataContaining(@Param("key") String key);

    /**
     * Find relationships created after a specific date
     */
    List<UserRelationship> findByCreatedAtAfter(java.time.LocalDateTime createdAt);

    /**
     * Find relationships updated after a specific date
     */
    List<UserRelationship> findByUpdatedAtAfter(java.time.LocalDateTime updatedAt);
}
