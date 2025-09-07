package com.legacykeep.relationship.service.impl;

import com.legacykeep.relationship.dto.request.CreateRelationshipTypeRequest;
import com.legacykeep.relationship.dto.response.RelationshipTypeResponse;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.enums.RelationshipCategory;
import com.legacykeep.relationship.exception.RelationshipTypeNotFoundException;
import com.legacykeep.relationship.repository.RelationshipTypeRepository;
import com.legacykeep.relationship.service.RelationshipTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RelationshipTypeService.
 * 
 * Provides business logic for managing relationship types.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RelationshipTypeServiceImpl implements RelationshipTypeService {

    private final RelationshipTypeRepository relationshipTypeRepository;

    @Override
    public RelationshipTypeResponse createRelationshipType(CreateRelationshipTypeRequest request) {
        log.info("Creating relationship type: {}", request.getName());

        // Check if relationship type already exists
        if (relationshipTypeRepository.existsByName(request.getName())) {
            throw new IllegalStateException("Relationship type with name '" + request.getName() + "' already exists");
        }

        // Validate reverse type if provided
        RelationshipType reverseType = null;
        if (request.getReverseTypeId() != null) {
            reverseType = relationshipTypeRepository.findById(request.getReverseTypeId())
                    .orElseThrow(() -> new RelationshipTypeNotFoundException(request.getReverseTypeId()));
        }

        // Create relationship type entity
        RelationshipType relationshipType = RelationshipType.builder()
                .name(request.getName())
                .category(request.getCategory())
                .bidirectional(request.getBidirectional())
                .reverseType(reverseType)
                .metadata(request.getMetadata())
                .build();

        // Save relationship type
        RelationshipType savedRelationshipType = relationshipTypeRepository.save(relationshipType);
        log.info("Created relationship type with ID: {}", savedRelationshipType.getId());

        return convertToResponse(savedRelationshipType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipTypeResponse> getAllRelationshipTypes(Pageable pageable) {
        log.debug("Getting all relationship types with pagination");

        return relationshipTypeRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getAllRelationshipTypes() {
        log.debug("Getting all relationship types");

        return relationshipTypeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipTypeResponse getRelationshipTypeById(Long id) {
        log.debug("Getting relationship type by ID: {}", id);

        RelationshipType relationshipType = relationshipTypeRepository.findById(id)
                .orElseThrow(() -> new RelationshipTypeNotFoundException(id));

        return convertToResponse(relationshipType);
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipTypeResponse getRelationshipTypeByName(String name) {
        log.debug("Getting relationship type by name: {}", name);

        RelationshipType relationshipType = relationshipTypeRepository.findByName(name)
                .orElseThrow(() -> new RelationshipTypeNotFoundException(name, "Relationship type not found"));

        return convertToResponse(relationshipType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getRelationshipTypesByCategory(RelationshipCategory category) {
        log.debug("Getting relationship types by category: {}", category);

        return relationshipTypeRepository.findByCategory(category)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipTypeResponse> getRelationshipTypesByCategory(RelationshipCategory category, Pageable pageable) {
        log.debug("Getting relationship types by category: {} with pagination", category);

        return relationshipTypeRepository.findByCategory(category, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getBidirectionalRelationshipTypes() {
        log.debug("Getting bidirectional relationship types");

        return relationshipTypeRepository.findByBidirectionalTrue()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getNonBidirectionalRelationshipTypes() {
        log.debug("Getting non-bidirectional relationship types");

        return relationshipTypeRepository.findByBidirectionalFalse()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getRelationshipTypesWithReverseTypes() {
        log.debug("Getting relationship types with reverse types");

        return relationshipTypeRepository.findWithReverseType()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getRelationshipTypesWithoutReverseTypes() {
        log.debug("Getting relationship types without reverse types");

        return relationshipTypeRepository.findWithoutReverseType()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> searchRelationshipTypesByName(String name) {
        log.debug("Searching relationship types by name: {}", name);

        return relationshipTypeRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getRelationshipTypesByCategoryAndBidirectional(
            RelationshipCategory category, Boolean bidirectional) {
        log.debug("Getting relationship types by category: {} and bidirectional: {}", category, bidirectional);

        return relationshipTypeRepository.findByCategoryAndBidirectional(category, bidirectional)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipTypeExists(String name) {
        return relationshipTypeRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public long countRelationshipTypesByCategory(RelationshipCategory category) {
        return relationshipTypeRepository.countByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBidirectionalRelationshipTypes() {
        return relationshipTypeRepository.countByBidirectionalTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countNonBidirectionalRelationshipTypes() {
        return relationshipTypeRepository.countByBidirectionalFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getAllRelationshipTypesOrdered() {
        log.debug("Getting all relationship types ordered by category and name");

        return relationshipTypeRepository.findAllOrderedByCategoryAndName()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipTypeResponse> getRelationshipTypesByMetadata(String key) {
        log.debug("Getting relationship types by metadata key: {}", key);

        return relationshipTypeRepository.findByMetadataContaining(key)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipType getRelationshipTypeEntityById(Long id) {
        return relationshipTypeRepository.findById(id)
                .orElseThrow(() -> new RelationshipTypeNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipType getRelationshipTypeEntityByName(String name) {
        return relationshipTypeRepository.findByName(name)
                .orElseThrow(() -> new RelationshipTypeNotFoundException(name, "Relationship type not found"));
    }

    /**
     * Convert RelationshipType entity to RelationshipTypeResponse DTO.
     */
    private RelationshipTypeResponse convertToResponse(RelationshipType relationshipType) {
        return convertToResponse(relationshipType, new java.util.HashSet<>());
    }

    /**
     * Convert RelationshipType entity to RelationshipTypeResponse DTO with cycle detection.
     */
    private RelationshipTypeResponse convertToResponse(RelationshipType relationshipType, java.util.Set<Long> visited) {
        if (relationshipType == null) {
            return null;
        }

        // Check for circular reference
        if (visited.contains(relationshipType.getId())) {
            return RelationshipTypeResponse.builder()
                    .id(relationshipType.getId())
                    .name(relationshipType.getName())
                    .category(relationshipType.getCategory())
                    .bidirectional(relationshipType.getBidirectional())
                    .reverseType(null) // Break the cycle
                    .metadata(relationshipType.getMetadata())
                    .createdAt(relationshipType.getCreatedAt())
                    .updatedAt(relationshipType.getUpdatedAt())
                    .build();
        }

        visited.add(relationshipType.getId());

        RelationshipTypeResponse response = RelationshipTypeResponse.builder()
                .id(relationshipType.getId())
                .name(relationshipType.getName())
                .category(relationshipType.getCategory())
                .bidirectional(relationshipType.getBidirectional())
                .reverseType(relationshipType.getReverseType() != null ? 
                        convertToResponse(relationshipType.getReverseType(), visited) : null)
                .metadata(relationshipType.getMetadata())
                .createdAt(relationshipType.getCreatedAt())
                .updatedAt(relationshipType.getUpdatedAt())
                .build();

        visited.remove(relationshipType.getId());
        return response;
    }
}
