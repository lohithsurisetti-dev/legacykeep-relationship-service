package com.legacykeep.relationship.service.impl;

import com.legacykeep.relationship.dto.request.CreateRelationshipRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipRequest;
import com.legacykeep.relationship.dto.response.RelationshipResponse;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.enums.RelationshipStatus;
import com.legacykeep.relationship.exception.RelationshipNotFoundException;
import com.legacykeep.relationship.exception.RelationshipTypeNotFoundException;
import com.legacykeep.relationship.repository.UserRelationshipRepository;
import com.legacykeep.relationship.service.RelationshipService;
import com.legacykeep.relationship.service.RelationshipTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RelationshipService.
 * 
 * Provides business logic for managing user relationships.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RelationshipServiceImpl implements RelationshipService {

    private final UserRelationshipRepository userRelationshipRepository;
    private final RelationshipTypeService relationshipTypeService;

    @Override
    public RelationshipResponse createRelationship(CreateRelationshipRequest request) {
        log.info("Creating relationship between users {} and {}", request.getUser1Id(), request.getUser2Id());

        // Validate that users are different
        if (request.getUser1Id().equals(request.getUser2Id())) {
            throw new IllegalArgumentException("User1 and User2 cannot be the same");
        }

        // Validate relationship type exists
        RelationshipType relationshipType = relationshipTypeService.getRelationshipTypeEntityById(request.getRelationshipTypeId());
        if (relationshipType == null) {
            throw new RelationshipTypeNotFoundException(request.getRelationshipTypeId());
        }

        // Check if relationship already exists
        if (userRelationshipRepository.existsRelationshipBetweenUsers(request.getUser1Id(), request.getUser2Id())) {
            throw new IllegalStateException("Relationship already exists between these users");
        }

        // Create relationship entity
        UserRelationship relationship = UserRelationship.builder()
                .user1Id(request.getUser1Id())
                .user2Id(request.getUser2Id())
                .relationshipType(relationshipType)
                .contextId(request.getContextId())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .metadata(request.getMetadata())
                .build();

        // Save relationship
        UserRelationship savedRelationship = userRelationshipRepository.save(relationship);
        log.info("Created relationship with ID: {}", savedRelationship.getId());

        return convertToResponse(savedRelationship);
    }

    @Override
    public RelationshipResponse updateRelationship(Long relationshipId, UpdateRelationshipRequest request) {
        log.info("Updating relationship with ID: {}", relationshipId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        // Update fields if provided
        if (request.getRelationshipTypeId() != null) {
            RelationshipType relationshipType = relationshipTypeService.getRelationshipTypeEntityById(request.getRelationshipTypeId());
            if (relationshipType == null) {
                throw new RelationshipTypeNotFoundException(request.getRelationshipTypeId());
            }
            relationship.setRelationshipType(relationshipType);
        }

        if (request.getContextId() != null) {
            relationship.setContextId(request.getContextId());
        }

        if (request.getStartDate() != null) {
            relationship.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            relationship.setEndDate(request.getEndDate());
        }

        if (request.getStatus() != null) {
            relationship.setStatus(request.getStatus());
        }

        if (request.getMetadata() != null) {
            relationship.setMetadata(request.getMetadata());
        }

        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);
        log.info("Updated relationship with ID: {}", updatedRelationship.getId());

        return convertToResponse(updatedRelationship);
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipResponse getRelationshipById(Long relationshipId) {
        log.debug("Getting relationship with ID: {}", relationshipId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        return convertToResponse(relationship);
    }

    @Override
    public void deleteRelationship(Long relationshipId) {
        log.info("Deleting relationship with ID: {}", relationshipId);

        if (!userRelationshipRepository.existsById(relationshipId)) {
            throw new RelationshipNotFoundException(relationshipId);
        }

        userRelationshipRepository.deleteById(relationshipId);
        log.info("Deleted relationship with ID: {}", relationshipId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipResponse> getUserRelationships(Long userId, Pageable pageable) {
        log.debug("Getting relationships for user: {}", userId);

        return userRelationshipRepository.findAllRelationshipsForUser(userId, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipResponse> getActiveUserRelationships(Long userId) {
        log.debug("Getting active relationships for user: {}", userId);

        return userRelationshipRepository.findActiveRelationshipsForUser(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipResponse> getRelationshipsBetweenUsers(Long user1Id, Long user2Id) {
        log.debug("Getting relationships between users {} and {}", user1Id, user2Id);

        return userRelationshipRepository.findRelationshipsBetweenUsers(user1Id, user2Id)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipResponse> getActiveRelationshipsBetweenUsers(Long user1Id, Long user2Id) {
        log.debug("Getting active relationships between users {} and {}", user1Id, user2Id);

        return userRelationshipRepository.findActiveRelationshipsBetweenUsers(user1Id, user2Id)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipResponse> getRelationshipsByStatus(RelationshipStatus status, Pageable pageable) {
        log.debug("Getting relationships by status: {}", status);

        return userRelationshipRepository.findByStatus(status, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipResponse> getRelationshipsByContext(Long contextId) {
        log.debug("Getting relationships by context: {}", contextId);

        return userRelationshipRepository.findByContextId(contextId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RelationshipResponse updateRelationshipStatus(Long relationshipId, RelationshipStatus status) {
        log.info("Updating relationship status for ID: {} to {}", relationshipId, status);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        relationship.setStatus(status);
        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);

        log.info("Updated relationship status for ID: {}", relationshipId);
        return convertToResponse(updatedRelationship);
    }

    @Override
    public RelationshipResponse endRelationship(Long relationshipId) {
        log.info("Ending relationship with ID: {}", relationshipId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        relationship.setStatus(RelationshipStatus.ENDED);
        relationship.setEndDate(LocalDate.now());
        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);

        log.info("Ended relationship with ID: {}", relationshipId);
        return convertToResponse(updatedRelationship);
    }

    @Override
    public RelationshipResponse suspendRelationship(Long relationshipId) {
        log.info("Suspending relationship with ID: {}", relationshipId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        relationship.setStatus(RelationshipStatus.SUSPENDED);
        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);

        log.info("Suspended relationship with ID: {}", relationshipId);
        return convertToResponse(updatedRelationship);
    }

    @Override
    public RelationshipResponse activateRelationship(Long relationshipId) {
        log.info("Activating relationship with ID: {}", relationshipId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        relationship.setStatus(RelationshipStatus.ACTIVE);
        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);

        log.info("Activated relationship with ID: {}", relationshipId);
        return convertToResponse(updatedRelationship);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipExists(Long user1Id, Long user2Id) {
        return userRelationshipRepository.existsRelationshipBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean activeRelationshipExists(Long user1Id, Long user2Id) {
        return userRelationshipRepository.existsActiveRelationshipBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserRelationships(Long userId) {
        return userRelationshipRepository.countRelationshipsForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUserRelationships(Long userId) {
        return userRelationshipRepository.countActiveRelationshipsForUser(userId);
    }

    /**
     * Convert UserRelationship entity to RelationshipResponse DTO.
     */
    private RelationshipResponse convertToResponse(UserRelationship relationship) {
        return RelationshipResponse.builder()
                .id(relationship.getId())
                .user1Id(relationship.getUser1Id())
                .user2Id(relationship.getUser2Id())
                .relationshipType(convertRelationshipTypeToResponse(relationship.getRelationshipType()))
                .contextId(relationship.getContextId())
                .startDate(relationship.getStartDate())
                .endDate(relationship.getEndDate())
                .status(relationship.getStatus())
                .metadata(relationship.getMetadata())
                .createdAt(relationship.getCreatedAt())
                .updatedAt(relationship.getUpdatedAt())
                .build();
    }

    /**
     * Convert RelationshipType entity to RelationshipTypeResponse DTO.
     */
    private com.legacykeep.relationship.dto.response.RelationshipTypeResponse convertRelationshipTypeToResponse(RelationshipType relationshipType) {
        if (relationshipType == null) {
            return null;
        }

        return com.legacykeep.relationship.dto.response.RelationshipTypeResponse.builder()
                .id(relationshipType.getId())
                .name(relationshipType.getName())
                .category(relationshipType.getCategory())
                .bidirectional(relationshipType.getBidirectional())
                .reverseType(relationshipType.getReverseType() != null ? 
                        convertRelationshipTypeToResponse(relationshipType.getReverseType()) : null)
                .metadata(relationshipType.getMetadata())
                .createdAt(relationshipType.getCreatedAt())
                .updatedAt(relationshipType.getUpdatedAt())
                .build();
    }
}
