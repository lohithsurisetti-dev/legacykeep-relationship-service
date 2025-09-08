package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.CreateRelationshipTypeRequest;
import com.legacykeep.relationship.dto.response.RelationshipTypeResponse;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.enums.RelationshipCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing relationship types.
 * 
 * Provides business logic for creating, updating, and querying relationship types.
 */
public interface RelationshipTypeService {

    /**
     * Create a new relationship type.
     */
    RelationshipTypeResponse createRelationshipType(CreateRelationshipTypeRequest request);

    /**
     * Get all relationship types.
     */
    Page<RelationshipTypeResponse> getAllRelationshipTypes(Pageable pageable);

    /**
     * Get all relationship types as a list.
     */
    List<RelationshipTypeResponse> getAllRelationshipTypes();

    /**
     * Get relationship type by ID.
     */
    RelationshipTypeResponse getRelationshipTypeById(Long id);

    /**
     * Get relationship type by name.
     */
    RelationshipTypeResponse getRelationshipTypeByName(String name);

    /**
     * Get relationship types by category.
     */
    List<RelationshipTypeResponse> getRelationshipTypesByCategory(RelationshipCategory category);

    /**
     * Get relationship types by category with pagination.
     */
    Page<RelationshipTypeResponse> getRelationshipTypesByCategory(RelationshipCategory category, Pageable pageable);

    /**
     * Get bidirectional relationship types.
     */
    List<RelationshipTypeResponse> getBidirectionalRelationshipTypes();

    /**
     * Get non-bidirectional relationship types.
     */
    List<RelationshipTypeResponse> getNonBidirectionalRelationshipTypes();

    /**
     * Get relationship types with reverse types.
     */
    List<RelationshipTypeResponse> getRelationshipTypesWithReverseTypes();

    /**
     * Get relationship types without reverse types.
     */
    List<RelationshipTypeResponse> getRelationshipTypesWithoutReverseTypes();

    /**
     * Search relationship types by name.
     */
    List<RelationshipTypeResponse> searchRelationshipTypesByName(String name);

    /**
     * Get relationship types by category and bidirectional flag.
     */
    List<RelationshipTypeResponse> getRelationshipTypesByCategoryAndBidirectional(
            RelationshipCategory category, Boolean bidirectional);

    /**
     * Check if relationship type exists by name.
     */
    boolean relationshipTypeExists(String name);

    /**
     * Count relationship types by category.
     */
    long countRelationshipTypesByCategory(RelationshipCategory category);

    /**
     * Count bidirectional relationship types.
     */
    long countBidirectionalRelationshipTypes();

    /**
     * Count non-bidirectional relationship types.
     */
    long countNonBidirectionalRelationshipTypes();

    /**
     * Get all relationship types ordered by category and name.
     */
    List<RelationshipTypeResponse> getAllRelationshipTypesOrdered();

    /**
     * Get relationship types with metadata containing specific key.
     */
    List<RelationshipTypeResponse> getRelationshipTypesByMetadata(String key);

    /**
     * Get the internal RelationshipType entity by ID (for internal use).
     */
    RelationshipType getRelationshipTypeEntityById(Long id);

    /**
     * Get the internal RelationshipType entity by name (for internal use).
     */
    RelationshipType getRelationshipTypeEntityByName(String name);
}

