package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.RespondToRelationshipRequest;
import com.legacykeep.relationship.dto.request.SendRelationshipRequest;
import com.legacykeep.relationship.dto.response.RelationshipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing relationship requests and approvals.
 * 
 * Handles the two-way approval workflow for relationships.
 */
public interface RelationshipRequestService {

    /**
     * Send a relationship request to another user.
     * Creates a PENDING relationship that requires approval.
     */
    RelationshipResponse sendRelationshipRequest(SendRelationshipRequest request);

    /**
     * Respond to a relationship request (accept or reject).
     */
    RelationshipResponse respondToRelationshipRequest(RespondToRelationshipRequest request);

    /**
     * Get all pending relationship requests for a user.
     */
    Page<RelationshipResponse> getPendingRequestsForUser(Long userId, Pageable pageable);

    /**
     * Get all relationship requests sent by a user.
     */
    Page<RelationshipResponse> getSentRequestsByUser(Long userId, Pageable pageable);

    /**
     * Get all relationship requests received by a user.
     */
    Page<RelationshipResponse> getReceivedRequestsByUser(Long userId, Pageable pageable);

    /**
     * Cancel a pending relationship request (only the sender can cancel).
     */
    RelationshipResponse cancelRelationshipRequest(Long relationshipId, Long userId);

    /**
     * Get relationship request by ID.
     */
    RelationshipResponse getRelationshipRequestById(Long relationshipId);

    /**
     * Check if there's a pending request between two users.
     */
    boolean hasPendingRequestBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Get pending request between two users.
     */
    RelationshipResponse getPendingRequestBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Count pending requests for a user.
     */
    long countPendingRequestsForUser(Long userId);

    /**
     * Get all pending requests (for admin purposes).
     */
    Page<RelationshipResponse> getAllPendingRequests(Pageable pageable);
}
