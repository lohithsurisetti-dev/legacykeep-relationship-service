package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.CreateRelationshipTypeRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipTypeRequest;
import com.legacykeep.relationship.entity.RelationshipType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for RelationshipType operations
 */
public interface RelationshipTypeService {

    /**
     * Get all relationship types
     */
    List<RelationshipType> getAllRelationshipTypes();

    /**
     * Get relationship types by category
     */
    List<RelationshipType> getRelationshipTypesByCategory(RelationshipType.RelationshipCategory category);

    /**
     * Get relationship types by bidirectional flag
     */
    List<RelationshipType> getRelationshipTypesByBidirectional(Boolean bidirectional);

    /**
     * Get relationship type by ID
     */
    Optional<RelationshipType> getRelationshipTypeById(Long id);

    /**
     * Get relationship type by name
     */
    Optional<RelationshipType> getRelationshipTypeByName(String name);

    /**
     * Create a new relationship type
     */
    RelationshipType createRelationshipType(CreateRelationshipTypeRequest request);

    /**
     * Update an existing relationship type
     */
    RelationshipType updateRelationshipType(Long id, UpdateRelationshipTypeRequest request);

    /**
     * Delete a relationship type
     */
    void deleteRelationshipType(Long id);

    /**
     * Check if relationship type exists by name
     */
    boolean existsByName(String name);

    /**
     * Check if relationship type exists by name (excluding given ID)
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Search relationship types by name
     */
    List<RelationshipType> searchRelationshipTypes(String name);
}
