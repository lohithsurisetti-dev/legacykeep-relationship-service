package com.legacykeep.relationship.service.impl;

import com.legacykeep.relationship.dto.request.CreateRelationshipTypeRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipTypeRequest;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.exception.ResourceNotFoundException;
import com.legacykeep.relationship.exception.DuplicateResourceException;
import com.legacykeep.relationship.repository.RelationshipTypeRepository;
import com.legacykeep.relationship.service.RelationshipTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of RelationshipTypeService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipTypeServiceImpl implements RelationshipTypeService {

    private final RelationshipTypeRepository relationshipTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipType> getAllRelationshipTypes() {
        log.debug("Getting all relationship types");
        return relationshipTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipType> getRelationshipTypesByCategory(RelationshipType.RelationshipCategory category) {
        log.debug("Getting relationship types by category: {}", category);
        return relationshipTypeRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipType> getRelationshipTypesByBidirectional(Boolean bidirectional) {
        log.debug("Getting relationship types by bidirectional: {}", bidirectional);
        return bidirectional ? 
               relationshipTypeRepository.findByBidirectionalTrue() : 
               relationshipTypeRepository.findByBidirectionalFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelationshipType> getRelationshipTypeById(Long id) {
        log.debug("Getting relationship type by ID: {}", id);
        return relationshipTypeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelationshipType> getRelationshipTypeByName(String name) {
        log.debug("Getting relationship type by name: {}", name);
        return relationshipTypeRepository.findByName(name);
    }

    @Override
    @Transactional
    public RelationshipType createRelationshipType(CreateRelationshipTypeRequest request) {
        log.debug("Creating relationship type: {}", request.getName());

        // Check if name already exists
        if (relationshipTypeRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Relationship type with name '" + request.getName() + "' already exists");
        }

        // Validate reverse type if provided
        RelationshipType reverseType = null;
        if (request.getReverseTypeId() != null) {
            reverseType = relationshipTypeRepository.findById(request.getReverseTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reverse type not found with ID: " + request.getReverseTypeId()));
        }

        RelationshipType relationshipType = RelationshipType.builder()
                .name(request.getName())
                .category(RelationshipType.RelationshipCategory.valueOf(request.getCategory()))
                .bidirectional(request.getBidirectional())
                .reverseType(reverseType)
                .metadata(request.getMetadata())
                .build();

        RelationshipType saved = relationshipTypeRepository.save(relationshipType);
        log.info("Created relationship type: {} with ID: {}", saved.getName(), saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public RelationshipType updateRelationshipType(Long id, UpdateRelationshipTypeRequest request) {
        log.debug("Updating relationship type with ID: {}", id);

        RelationshipType relationshipType = relationshipTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship type not found with ID: " + id));

        // Check if name already exists (excluding current record)
        if (request.getName() != null && !request.getName().equals(relationshipType.getName())) {
            if (relationshipTypeRepository.existsByNameAndIdNot(request.getName(), id)) {
                throw new DuplicateResourceException("Relationship type with name '" + request.getName() + "' already exists");
            }
            relationshipType.setName(request.getName());
        }

        if (request.getCategory() != null) {
            relationshipType.setCategory(RelationshipType.RelationshipCategory.valueOf(request.getCategory()));
        }

        if (request.getBidirectional() != null) {
            relationshipType.setBidirectional(request.getBidirectional());
        }

        if (request.getReverseTypeId() != null) {
            RelationshipType reverseType = relationshipTypeRepository.findById(request.getReverseTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reverse type not found with ID: " + request.getReverseTypeId()));
            relationshipType.setReverseType(reverseType);
        }

        if (request.getMetadata() != null) {
            relationshipType.setMetadata(request.getMetadata());
        }

        RelationshipType updated = relationshipTypeRepository.save(relationshipType);
        log.info("Updated relationship type: {} with ID: {}", updated.getName(), updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void deleteRelationshipType(Long id) {
        log.debug("Deleting relationship type with ID: {}", id);

        RelationshipType relationshipType = relationshipTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship type not found with ID: " + id));

        relationshipTypeRepository.delete(relationshipType);
        log.info("Deleted relationship type: {} with ID: {}", relationshipType.getName(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return relationshipTypeRepository.findByName(name).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return relationshipTypeRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipType> searchRelationshipTypes(String name) {
        log.debug("Searching relationship types by name: {}", name);
        return relationshipTypeRepository.findByNameContainingIgnoreCase(name);
    }
}
