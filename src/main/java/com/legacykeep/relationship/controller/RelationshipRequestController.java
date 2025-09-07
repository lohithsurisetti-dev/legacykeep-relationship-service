package com.legacykeep.relationship.controller;

import com.legacykeep.relationship.dto.ApiResponse;
import com.legacykeep.relationship.dto.request.RespondToRelationshipRequest;
import com.legacykeep.relationship.dto.request.SendRelationshipRequest;
import com.legacykeep.relationship.dto.response.RelationshipResponse;
import com.legacykeep.relationship.service.RelationshipRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing relationship requests and approvals.
 * 
 * Handles the two-way approval workflow for relationships.
 */
@RestController
@RequestMapping("/api/v1/relationship-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relationship Requests", description = "API for managing relationship requests and approvals")
public class RelationshipRequestController {

    private final RelationshipRequestService relationshipRequestService;

    @PostMapping("/send")
    @Operation(summary = "Send relationship request", description = "Send a relationship request to another user")
    public ResponseEntity<ApiResponse<RelationshipResponse>> sendRelationshipRequest(
            @RequestBody SendRelationshipRequest request) {
        
        log.info("Sending relationship request from user {} to user {}", 
                request.getRequesterUserId(), request.getRecipientUserId());
        
        RelationshipResponse relationship = relationshipRequestService.sendRelationshipRequest(request);
        
        return ResponseEntity.ok(ApiResponse.created(relationship, "Relationship request sent successfully"));
    }

    @PostMapping("/respond")
    @Operation(summary = "Respond to relationship request", description = "Accept or reject a relationship request")
    public ResponseEntity<ApiResponse<RelationshipResponse>> respondToRelationshipRequest(
            @RequestBody RespondToRelationshipRequest request) {
        
        log.info("Responding to relationship request {} with action: {}", 
                request.getRelationshipId(), request.getAction());
        
        RelationshipResponse relationship = relationshipRequestService.respondToRelationshipRequest(request);
        
        String message = request.getAction() == RespondToRelationshipRequest.ResponseAction.ACCEPT 
                ? "Relationship request accepted successfully" 
                : "Relationship request rejected successfully";
        
        return ResponseEntity.ok(ApiResponse.success(relationship, message));
    }

    @GetMapping("/pending/{userId}")
    @Operation(summary = "Get pending requests for user", description = "Get all pending relationship requests for a user")
    public ResponseEntity<ApiResponse<Page<RelationshipResponse>>> getPendingRequestsForUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        log.info("Getting pending requests for user: {}", userId);
        Page<RelationshipResponse> requests = relationshipRequestService.getPendingRequestsForUser(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(requests, "Pending requests retrieved successfully"));
    }

    @GetMapping("/sent/{userId}")
    @Operation(summary = "Get sent requests by user", description = "Get all relationship requests sent by a user")
    public ResponseEntity<ApiResponse<Page<RelationshipResponse>>> getSentRequestsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        log.info("Getting sent requests by user: {}", userId);
        Page<RelationshipResponse> requests = relationshipRequestService.getSentRequestsByUser(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(requests, "Sent requests retrieved successfully"));
    }

    @GetMapping("/received/{userId}")
    @Operation(summary = "Get received requests by user", description = "Get all relationship requests received by a user")
    public ResponseEntity<ApiResponse<Page<RelationshipResponse>>> getReceivedRequestsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        log.info("Getting received requests by user: {}", userId);
        Page<RelationshipResponse> requests = relationshipRequestService.getReceivedRequestsByUser(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(requests, "Received requests retrieved successfully"));
    }

    @DeleteMapping("/{relationshipId}/cancel/{userId}")
    @Operation(summary = "Cancel relationship request", description = "Cancel a pending relationship request")
    public ResponseEntity<ApiResponse<RelationshipResponse>> cancelRelationshipRequest(
            @Parameter(description = "Relationship ID") @PathVariable Long relationshipId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        log.info("Canceling relationship request {} by user {}", relationshipId, userId);
        RelationshipResponse relationship = relationshipRequestService.cancelRelationshipRequest(relationshipId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(relationship, "Relationship request canceled successfully"));
    }

    @GetMapping("/{relationshipId}")
    @Operation(summary = "Get relationship request by ID", description = "Get a specific relationship request by ID")
    public ResponseEntity<ApiResponse<RelationshipResponse>> getRelationshipRequestById(
            @Parameter(description = "Relationship ID") @PathVariable Long relationshipId) {
        
        log.info("Getting relationship request by ID: {}", relationshipId);
        RelationshipResponse relationship = relationshipRequestService.getRelationshipRequestById(relationshipId);
        
        return ResponseEntity.ok(ApiResponse.success(relationship, "Relationship request retrieved successfully"));
    }

    @GetMapping("/check-pending/{user1Id}/{user2Id}")
    @Operation(summary = "Check pending request between users", description = "Check if there's a pending request between two users")
    public ResponseEntity<ApiResponse<Boolean>> hasPendingRequestBetweenUsers(
            @Parameter(description = "First user ID") @PathVariable Long user1Id,
            @Parameter(description = "Second user ID") @PathVariable Long user2Id) {
        
        log.info("Checking pending request between users {} and {}", user1Id, user2Id);
        boolean hasPending = relationshipRequestService.hasPendingRequestBetweenUsers(user1Id, user2Id);
        
        return ResponseEntity.ok(ApiResponse.success(hasPending, "Pending request check completed"));
    }

    @GetMapping("/pending-between/{user1Id}/{user2Id}")
    @Operation(summary = "Get pending request between users", description = "Get the pending request between two users")
    public ResponseEntity<ApiResponse<RelationshipResponse>> getPendingRequestBetweenUsers(
            @Parameter(description = "First user ID") @PathVariable Long user1Id,
            @Parameter(description = "Second user ID") @PathVariable Long user2Id) {
        
        log.info("Getting pending request between users {} and {}", user1Id, user2Id);
        RelationshipResponse relationship = relationshipRequestService.getPendingRequestBetweenUsers(user1Id, user2Id);
        
        return ResponseEntity.ok(ApiResponse.success(relationship, "Pending request retrieved successfully"));
    }

    @GetMapping("/count-pending/{userId}")
    @Operation(summary = "Count pending requests for user", description = "Count pending relationship requests for a user")
    public ResponseEntity<ApiResponse<Long>> countPendingRequestsForUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        log.info("Counting pending requests for user: {}", userId);
        long count = relationshipRequestService.countPendingRequestsForUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(count, "Pending requests count retrieved"));
    }

    @GetMapping("/all-pending")
    @Operation(summary = "Get all pending requests", description = "Get all pending relationship requests (admin)")
    public ResponseEntity<ApiResponse<Page<RelationshipResponse>>> getAllPendingRequests(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        log.info("Getting all pending requests");
        Page<RelationshipResponse> requests = relationshipRequestService.getAllPendingRequests(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(requests, "All pending requests retrieved successfully"));
    }
}
