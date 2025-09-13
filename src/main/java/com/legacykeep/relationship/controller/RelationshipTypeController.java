package com.legacykeep.relationship.controller;

import com.legacykeep.relationship.dto.ApiResponse;
import com.legacykeep.relationship.dto.request.CreateRelationshipTypeRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipTypeRequest;
import com.legacykeep.relationship.dto.response.RelationshipTypeResponse;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.exception.ResourceNotFoundException;
import com.legacykeep.relationship.service.RelationshipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for RelationshipType operations
 */
@RestController
@RequestMapping("/v1/relationship-types")
@RequiredArgsConstructor
@Slf4j
public class RelationshipTypeController {

    private final RelationshipTypeService relationshipTypeService;

    /**
     * Get all relationship types
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RelationshipTypeResponse>>> getAllRelationshipTypes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean bidirectional) {
        
        log.debug("Getting all relationship types with filters - category: {}, bidirectional: {}", category, bidirectional);
        
        List<RelationshipType> relationshipTypes;
        
        if (category != null && bidirectional != null) {
            relationshipTypes = relationshipTypeService.getRelationshipTypesByCategory(
                RelationshipType.RelationshipCategory.valueOf(category.toUpperCase()));
            relationshipTypes = relationshipTypes.stream()
                .filter(rt -> rt.getBidirectional().equals(bidirectional))
                .collect(Collectors.toList());
        } else if (category != null) {
            relationshipTypes = relationshipTypeService.getRelationshipTypesByCategory(
                RelationshipType.RelationshipCategory.valueOf(category.toUpperCase()));
        } else if (bidirectional != null) {
            relationshipTypes = relationshipTypeService.getRelationshipTypesByBidirectional(bidirectional);
        } else {
            relationshipTypes = relationshipTypeService.getAllRelationshipTypes();
        }
        
        List<RelationshipTypeResponse> responses = relationshipTypes.stream()
                .map(RelationshipTypeResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses, "Relationship types retrieved successfully"));
    }

    /**
     * Get relationship type by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> getRelationshipTypeById(@PathVariable Long id) {
        log.debug("Getting relationship type by ID: {}", id);
        
        RelationshipType relationshipType = relationshipTypeService.getRelationshipTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship type not found with ID: " + id));
        
        RelationshipTypeResponse response = RelationshipTypeResponse.fromEntity(relationshipType);
        return ResponseEntity.ok(ApiResponse.success(response, "Relationship type retrieved successfully"));
    }

    /**
     * Get relationship type by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> getRelationshipTypeByName(@PathVariable String name) {
        log.debug("Getting relationship type by name: {}", name);
        
        RelationshipType relationshipType = relationshipTypeService.getRelationshipTypeByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship type not found with name: " + name));
        
        RelationshipTypeResponse response = RelationshipTypeResponse.fromEntity(relationshipType);
        return ResponseEntity.ok(ApiResponse.success(response, "Relationship type retrieved successfully"));
    }

    /**
     * Create a new relationship type
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> createRelationshipType(
            @Valid @RequestBody CreateRelationshipTypeRequest request) {
        
        log.debug("Creating relationship type: {}", request.getName());
        
        RelationshipType relationshipType = relationshipTypeService.createRelationshipType(request);
        RelationshipTypeResponse response = RelationshipTypeResponse.fromEntity(relationshipType);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Relationship type created successfully"));
    }

    /**
     * Update an existing relationship type
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> updateRelationshipType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRelationshipTypeRequest request) {
        
        log.debug("Updating relationship type with ID: {}", id);
        
        RelationshipType relationshipType = relationshipTypeService.updateRelationshipType(id, request);
        RelationshipTypeResponse response = RelationshipTypeResponse.fromEntity(relationshipType);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Relationship type updated successfully"));
    }

    /**
     * Delete a relationship type
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRelationshipType(@PathVariable Long id) {
        log.debug("Deleting relationship type with ID: {}", id);
        
        relationshipTypeService.deleteRelationshipType(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Relationship type deleted successfully"));
    }

    /**
     * Search relationship types by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RelationshipTypeResponse>>> searchRelationshipTypes(
            @RequestParam String name) {
        
        log.debug("Searching relationship types by name: {}", name);
        
        List<RelationshipType> relationshipTypes = relationshipTypeService.searchRelationshipTypes(name);
        List<RelationshipTypeResponse> responses = relationshipTypes.stream()
                .map(RelationshipTypeResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses, "Relationship types search completed"));
    }
}
