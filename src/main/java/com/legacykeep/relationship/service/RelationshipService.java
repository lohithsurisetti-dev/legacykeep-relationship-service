package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.CreateRelationshipRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipRequest;
import com.legacykeep.relationship.dto.response.RelationshipResponse;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.enums.RelationshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing user relationships.
 * 
 * Provides business logic for creating, updating, and querying relationships between users.
 */
public interface RelationshipService {

    /**
     * Create a new relationship between two users.
     */
    RelationshipResponse createRelationship(CreateRelationshipRequest request);

    /**
     * Update an existing relationship.
     */
    RelationshipResponse updateRelationship(Long relationshipId, UpdateRelationshipRequest request);

    /**
     * Get a relationship by ID.
     */
    RelationshipResponse getRelationshipById(Long relationshipId);

    /**
     * Delete a relationship by ID.
     */
    void deleteRelationship(Long relationshipId);

    /**
     * Get all relationships for a user.
     */
    Page<RelationshipResponse> getUserRelationships(Long userId, Pageable pageable);

    /**
     * Get active relationships for a user.
     */
    List<RelationshipResponse> getActiveUserRelationships(Long userId);

    /**
     * Get relationships between two specific users.
     */
    List<RelationshipResponse> getRelationshipsBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Get active relationships between two specific users.
     */
    List<RelationshipResponse> getActiveRelationshipsBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Get relationships by status.
     */
    Page<RelationshipResponse> getRelationshipsByStatus(RelationshipStatus status, Pageable pageable);

    /**
     * Get relationships by context ID.
     */
    List<RelationshipResponse> getRelationshipsByContext(Long contextId);

    /**
     * Update relationship status.
     */
    RelationshipResponse updateRelationshipStatus(Long relationshipId, RelationshipStatus status);

    /**
     * End a relationship (set status to ENDED and end date).
     */
    RelationshipResponse endRelationship(Long relationshipId);

    /**
     * Suspend a relationship (set status to SUSPENDED).
     */
    RelationshipResponse suspendRelationship(Long relationshipId);

    /**
     * Activate a relationship (set status to ACTIVE).
     */
    RelationshipResponse activateRelationship(Long relationshipId);

    /**
     * Check if a relationship exists between two users.
     */
    boolean relationshipExists(Long user1Id, Long user2Id);

    /**
     * Check if an active relationship exists between two users.
     */
    boolean activeRelationshipExists(Long user1Id, Long user2Id);

    /**
     * Count relationships for a user.
     */
    long countUserRelationships(Long userId);

    /**
     * Count active relationships for a user.
     */
    long countActiveUserRelationships(Long userId);
}
