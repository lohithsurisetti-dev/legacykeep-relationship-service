package com.legacykeep.relationship.service.impl;

import com.legacykeep.relationship.dto.request.CreateRelationshipRequest;
import com.legacykeep.relationship.dto.request.UpdateRelationshipRequest;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.exception.ResourceNotFoundException;
import com.legacykeep.relationship.exception.DuplicateResourceException;
import com.legacykeep.relationship.repository.RelationshipTypeRepository;
import com.legacykeep.relationship.repository.UserRelationshipRepository;
import com.legacykeep.relationship.service.UserRelationshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserRelationshipService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRelationshipServiceImpl implements UserRelationshipService {

    private final UserRelationshipRepository userRelationshipRepository;
    private final RelationshipTypeRepository relationshipTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationship> getUserRelationships(Long userId) {
        log.debug("Getting relationships for user: {}", userId);
        return userRelationshipRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserRelationship> getUserRelationships(Long userId, Pageable pageable) {
        log.debug("Getting relationships for user: {} with pagination", userId);
        return userRelationshipRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationship> getRelationshipsByStatus(UserRelationship.RelationshipStatus status) {
        log.debug("Getting relationships by status: {}", status);
        return userRelationshipRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationship> getUserRelationshipsByStatus(Long userId, UserRelationship.RelationshipStatus status) {
        log.debug("Getting relationships for user: {} with status: {}", userId, status);
        return userRelationshipRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserRelationship> getUserRelationshipsByStatus(Long userId, UserRelationship.RelationshipStatus status, Pageable pageable) {
        log.debug("Getting relationships for user: {} with status: {} and pagination", userId, status);
        return userRelationshipRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationship> getRelationshipsBetweenUsers(Long user1Id, Long user2Id) {
        log.debug("Getting relationships between users: {} and {}", user1Id, user2Id);
        return userRelationshipRepository.findRelationshipsBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationship> getActiveRelationshipsBetweenUsers(Long user1Id, Long user2Id) {
        log.debug("Getting active relationships between users: {} and {}", user1Id, user2Id);
        return userRelationshipRepository.findActiveRelationshipsBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRelationship> getRelationshipById(Long id) {
        log.debug("Getting relationship by ID: {}", id);
        return userRelationshipRepository.findById(id);
    }

    @Override
    @Transactional
    public UserRelationship createRelationship(CreateRelationshipRequest request) {
        log.debug("Creating relationship between users: {} and {}", request.getUser1Id(), request.getUser2Id());

        // Validate users are different
        if (request.getUser1Id().equals(request.getUser2Id())) {
            throw new IllegalArgumentException("Cannot create relationship between same user");
        }

        // Get relationship type
        RelationshipType relationshipType = relationshipTypeRepository.findById(request.getRelationshipTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Relationship type not found with ID: " + request.getRelationshipTypeId()));

        // Check if relationship already exists
        if (userRelationshipRepository.existsBetweenUsers(request.getUser1Id(), request.getUser2Id())) {
            throw new DuplicateResourceException("Relationship already exists between users");
        }

        UserRelationship userRelationship = UserRelationship.builder()
                .user1Id(request.getUser1Id())
                .user2Id(request.getUser2Id())
                .relationshipType(relationshipType)
                .contextId(request.getContextId())
                .startDate(request.getStartDate())
                .status(UserRelationship.RelationshipStatus.ACTIVE)
                .metadata(request.getMetadata())
                .build();

        UserRelationship saved = userRelationshipRepository.save(userRelationship);
        log.info("Created relationship with ID: {} between users: {} and {}", saved.getId(), saved.getUser1Id(), saved.getUser2Id());
        return saved;
    }

    @Override
    @Transactional
    public UserRelationship updateRelationship(Long id, UpdateRelationshipRequest request) {
        log.debug("Updating relationship with ID: {}", id);

        UserRelationship userRelationship = userRelationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship not found with ID: " + id));

        if (request.getStatus() != null) {
            userRelationship.setStatus(UserRelationship.RelationshipStatus.valueOf(request.getStatus()));
        }

        if (request.getEndDate() != null) {
            userRelationship.setEndDate(request.getEndDate());
        }

        if (request.getMetadata() != null) {
            userRelationship.setMetadata(request.getMetadata());
        }

        UserRelationship updated = userRelationshipRepository.save(userRelationship);
        log.info("Updated relationship with ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void deleteRelationship(Long id) {
        log.debug("Deleting relationship with ID: {}", id);

        UserRelationship userRelationship = userRelationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship not found with ID: " + id));

        userRelationshipRepository.delete(userRelationship);
        log.info("Deleted relationship with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipExistsBetweenUsers(Long user1Id, Long user2Id) {
        return userRelationshipRepository.existsBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean activeRelationshipExistsBetweenUsers(Long user1Id, Long user2Id) {
        return userRelationshipRepository.existsActiveBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserRelationships(Long userId) {
        return userRelationshipRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUserRelationships(Long userId) {
        return userRelationshipRepository.countActiveByUserId(userId);
    }
}
