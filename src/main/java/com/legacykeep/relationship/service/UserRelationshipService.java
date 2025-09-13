package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.CreateRelationshipRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipRequest;
import com.legacykeep.relationship.entity.UserRelationship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for UserRelationship operations
 */
public interface UserRelationshipService {

    /**
     * Get all relationships for a specific user
     */
    List<UserRelationship> getUserRelationships(Long userId);

    /**
     * Get all relationships for a specific user with pagination
     */
    Page<UserRelationship> getUserRelationships(Long userId, Pageable pageable);

    /**
     * Get relationships by status
     */
    List<UserRelationship> getRelationshipsByStatus(UserRelationship.RelationshipStatus status);

    /**
     * Get relationships by user and status
     */
    List<UserRelationship> getUserRelationshipsByStatus(Long userId, UserRelationship.RelationshipStatus status);

    /**
     * Get relationships by user and status with pagination
     */
    Page<UserRelationship> getUserRelationshipsByStatus(Long userId, UserRelationship.RelationshipStatus status, Pageable pageable);

    /**
     * Get relationships between two users
     */
    List<UserRelationship> getRelationshipsBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Get active relationships between two users
     */
    List<UserRelationship> getActiveRelationshipsBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Get relationship by ID
     */
    Optional<UserRelationship> getRelationshipById(Long id);

    /**
     * Create a new relationship
     */
    UserRelationship createRelationship(CreateRelationshipRequest request);

    /**
     * Update an existing relationship
     */
    UserRelationship updateRelationship(Long id, UpdateRelationshipRequest request);

    /**
     * Delete a relationship
     */
    void deleteRelationship(Long id);

    /**
     * Check if relationship exists between users
     */
    boolean relationshipExistsBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Check if active relationship exists between users
     */
    boolean activeRelationshipExistsBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Count relationships for a user
     */
    long countUserRelationships(Long userId);

    /**
     * Count active relationships for a user
     */
    long countActiveUserRelationships(Long userId);
}
