package com.legacykeep.relationship.controller;

import com.legacykeep.relationship.dto.ApiResponse;
import com.legacykeep.relationship.dto.response.RelationshipTypeResponse;
import com.legacykeep.relationship.enums.RelationshipCategory;
import com.legacykeep.relationship.service.RelationshipTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing relationship types.
 * 
 * Provides REST endpoints for relationship type operations.
 */
@RestController
@RequestMapping("/api/v1/relationship-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relationship Types", description = "API for managing relationship types")
public class RelationshipTypeController {

    private final RelationshipTypeService relationshipTypeService;

    @GetMapping
    @Operation(summary = "Get all relationship types", description = "Retrieve all relationship types with optional pagination")
    public ResponseEntity<ApiResponse<Page<RelationshipTypeResponse>>> getAllRelationshipTypes(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        log.info("Getting all relationship types with pagination: {}", pageable);
        Page<RelationshipTypeResponse> relationshipTypes = relationshipTypeService.getAllRelationshipTypes(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(relationshipTypes, "Relationship types retrieved successfully"));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all relationship types as list", description = "Retrieve all relationship types as a list")
    public ResponseEntity<ApiResponse<List<RelationshipTypeResponse>>> getAllRelationshipTypesList() {
        
        log.info("Getting all relationship types as list");
        List<RelationshipTypeResponse> relationshipTypes = relationshipTypeService.getAllRelationshipTypes();
        
        return ResponseEntity.ok(ApiResponse.success(relationshipTypes, "Relationship types retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get relationship type by ID", description = "Retrieve a specific relationship type by its ID")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> getRelationshipTypeById(
            @Parameter(description = "Relationship type ID") @PathVariable Long id) {
        
        log.info("Getting relationship type by ID: {}", id);
        RelationshipTypeResponse relationshipType = relationshipTypeService.getRelationshipTypeById(id);
        
        return ResponseEntity.ok(ApiResponse.success(relationshipType, "Relationship type retrieved successfully"));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get relationship type by name", description = "Retrieve a specific relationship type by its name")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> getRelationshipTypeByName(
            @Parameter(description = "Relationship type name") @PathVariable String name) {
        
        log.info("Getting relationship type by name: {}", name);
        RelationshipTypeResponse relationshipType = relationshipTypeService.getRelationshipTypeByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(relationshipType, "Relationship type retrieved successfully"));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get relationship types by category", description = "Retrieve relationship types by category")
    public ResponseEntity<ApiResponse<List<RelationshipTypeResponse>>> getRelationshipTypesByCategory(
            @Parameter(description = "Relationship category") @PathVariable RelationshipCategory category) {
        
        log.info("Getting relationship types by category: {}", category);
        List<RelationshipTypeResponse> relationshipTypes = relationshipTypeService.getRelationshipTypesByCategory(category);
        
        return ResponseEntity.ok(ApiResponse.success(relationshipTypes, "Relationship types retrieved successfully"));
    }

    @GetMapping("/bidirectional")
    @Operation(summary = "Get bidirectional relationship types", description = "Retrieve all bidirectional relationship types")
    public ResponseEntity<ApiResponse<List<RelationshipTypeResponse>>> getBidirectionalRelationshipTypes() {
        
        log.info("Getting bidirectional relationship types");
        List<RelationshipTypeResponse> relationshipTypes = relationshipTypeService.getBidirectionalRelationshipTypes();
        
        return ResponseEntity.ok(ApiResponse.success(relationshipTypes, "Bidirectional relationship types retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search relationship types by name", description = "Search relationship types by name containing the search term")
    public ResponseEntity<ApiResponse<List<RelationshipTypeResponse>>> searchRelationshipTypesByName(
            @Parameter(description = "Search term") @RequestParam String name) {
        
        log.info("Searching relationship types by name: {}", name);
        List<RelationshipTypeResponse> relationshipTypes = relationshipTypeService.searchRelationshipTypesByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(relationshipTypes, "Relationship types found successfully"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get relationship type statistics", description = "Get statistics about relationship types")
    public ResponseEntity<ApiResponse<Object>> getRelationshipTypeStats() {
        
        log.info("Getting relationship type statistics");
        
        // Create stats object
        Object stats = new Object() {
            public final long totalTypes = relationshipTypeService.getAllRelationshipTypes().size();
            public final long familyTypes = relationshipTypeService.countRelationshipTypesByCategory(RelationshipCategory.FAMILY);
            public final long socialTypes = relationshipTypeService.countRelationshipTypesByCategory(RelationshipCategory.SOCIAL);
            public final long professionalTypes = relationshipTypeService.countRelationshipTypesByCategory(RelationshipCategory.PROFESSIONAL);
            public final long customTypes = relationshipTypeService.countRelationshipTypesByCategory(RelationshipCategory.CUSTOM);
            public final long bidirectionalTypes = relationshipTypeService.countBidirectionalRelationshipTypes();
            public final long nonBidirectionalTypes = relationshipTypeService.countNonBidirectionalRelationshipTypes();
        };
        
        return ResponseEntity.ok(ApiResponse.success(stats, "Relationship type statistics retrieved successfully"));
    }
}
