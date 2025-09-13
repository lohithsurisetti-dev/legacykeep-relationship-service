package com.legacykeep.relationship.controller;

import com.legacykeep.relationship.dto.ApiResponse;
import com.legacykeep.relationship.dto.request.CreateRelationshipRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipRequest;
import com.legacykeep.relationship.dto.response.PaginatedRelationshipResponse;
import com.legacykeep.relationship.dto.response.UserRelationshipResponse;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.exception.ResourceNotFoundException;
import com.legacykeep.relationship.service.UserRelationshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for UserRelationship operations
 */
@RestController
@RequestMapping("/v1/relationships")
@RequiredArgsConstructor
@Slf4j
public class UserRelationshipController {

    private final UserRelationshipService userRelationshipService;

    /**
     * Get all relationships for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PaginatedRelationshipResponse>> getUserRelationships(
            @PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long contextId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Getting relationships for user: {} with filters - status: {}, category: {}, contextId: {}", 
                 userId, status, category, contextId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserRelationship> relationships;
        
        if (status != null) {
            relationships = userRelationshipService.getUserRelationshipsByStatus(
                userId, UserRelationship.RelationshipStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            relationships = userRelationshipService.getUserRelationships(userId, pageable);
        }
        
        PaginatedRelationshipResponse response = PaginatedRelationshipResponse.fromPage(relationships);
        return ResponseEntity.ok(ApiResponse.success(response, "User relationships retrieved successfully"));
    }

    /**
     * Get relationships between two users
     */
    @GetMapping("/between/{user1Id}/{user2Id}")
    public ResponseEntity<ApiResponse<List<UserRelationshipResponse>>> getRelationshipsBetweenUsers(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        
        log.debug("Getting relationships between users: {} and {}, activeOnly: {}", user1Id, user2Id, activeOnly);
        
        List<UserRelationship> relationships;
        if (activeOnly) {
            relationships = userRelationshipService.getActiveRelationshipsBetweenUsers(user1Id, user2Id);
        } else {
            relationships = userRelationshipService.getRelationshipsBetweenUsers(user1Id, user2Id);
        }
        
        List<UserRelationshipResponse> responses = relationships.stream()
                .map(UserRelationshipResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses, "Relationships between users retrieved successfully"));
    }

    /**
     * Get relationship by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRelationshipResponse>> getRelationshipById(@PathVariable Long id) {
        log.debug("Getting relationship by ID: {}", id);
        
        UserRelationship relationship = userRelationshipService.getRelationshipById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship not found with ID: " + id));
        
        UserRelationshipResponse response = UserRelationshipResponse.fromEntity(relationship);
        return ResponseEntity.ok(ApiResponse.success(response, "Relationship retrieved successfully"));
    }

    /**
     * Create a new relationship
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserRelationshipResponse>> createRelationship(
            @Valid @RequestBody CreateRelationshipRequest request) {
        
        log.debug("Creating relationship between users: {} and {}", request.getUser1Id(), request.getUser2Id());
        
        UserRelationship relationship = userRelationshipService.createRelationship(request);
        UserRelationshipResponse response = UserRelationshipResponse.fromEntity(relationship);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Relationship created successfully"));
    }

    /**
     * Update an existing relationship
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRelationshipResponse>> updateRelationship(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRelationshipRequest request) {
        
        log.debug("Updating relationship with ID: {}", id);
        
        UserRelationship relationship = userRelationshipService.updateRelationship(id, request);
        UserRelationshipResponse response = UserRelationshipResponse.fromEntity(relationship);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Relationship updated successfully"));
    }

    /**
     * Delete a relationship
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRelationship(@PathVariable Long id) {
        log.debug("Deleting relationship with ID: {}", id);
        
        userRelationshipService.deleteRelationship(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Relationship deleted successfully"));
    }

    /**
     * Get relationship statistics for a user
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ApiResponse<Object>> getUserRelationshipStats(@PathVariable Long userId) {
        log.debug("Getting relationship statistics for user: {}", userId);
        
        long totalRelationships = userRelationshipService.countUserRelationships(userId);
        long activeRelationships = userRelationshipService.countActiveUserRelationships(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRelationships", totalRelationships);
        stats.put("activeRelationships", activeRelationships);
        stats.put("endedRelationships", totalRelationships - activeRelationships);
        
        return ResponseEntity.ok(ApiResponse.success(stats, "User relationship statistics retrieved successfully"));
    }

    /**
     * Check if relationship exists between users
     */
    @GetMapping("/exists/{user1Id}/{user2Id}")
    public ResponseEntity<ApiResponse<Object>> checkRelationshipExists(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        
        log.debug("Checking if relationship exists between users: {} and {}, activeOnly: {}", user1Id, user2Id, activeOnly);
        
        boolean exists;
        if (activeOnly) {
            exists = userRelationshipService.activeRelationshipExistsBetweenUsers(user1Id, user2Id);
        } else {
            exists = userRelationshipService.relationshipExistsBetweenUsers(user1Id, user2Id);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        
        return ResponseEntity.ok(ApiResponse.success(result, "Relationship existence check completed"));
    }
}
