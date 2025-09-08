package com.legacykeep.relationship.service.impl;

import com.legacykeep.relationship.dto.request.RespondToRelationshipRequest;
import com.legacykeep.relationship.dto.request.SendRelationshipRequest;
import com.legacykeep.relationship.dto.response.RelationshipResponse;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.enums.RelationshipStatus;
import com.legacykeep.relationship.exception.RelationshipNotFoundException;
import com.legacykeep.relationship.exception.RelationshipTypeNotFoundException;
import com.legacykeep.relationship.repository.UserRelationshipRepository;
import com.legacykeep.relationship.service.RelationshipRequestService;
import com.legacykeep.relationship.service.RelationshipTypeService;
import com.legacykeep.relationship.service.EventPublisherService;
import com.legacykeep.relationship.service.CacheService;
import com.legacykeep.relationship.service.UserServiceClient;
import com.legacykeep.relationship.dto.event.RelationshipRequestSentEvent;
import com.legacykeep.relationship.dto.event.RelationshipRequestAcceptedEvent;
import com.legacykeep.relationship.dto.event.RelationshipRequestRejectedEvent;
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
 * Implementation of RelationshipRequestService.
 * 
 * Handles the two-way approval workflow for relationships.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RelationshipRequestServiceImpl implements RelationshipRequestService {

    private final UserRelationshipRepository userRelationshipRepository;
    private final RelationshipTypeService relationshipTypeService;
    private final EventPublisherService eventPublisherService;
    private final CacheService cacheService;
    private final UserServiceClient userServiceClient;

    @Override
    public RelationshipResponse sendRelationshipRequest(SendRelationshipRequest request) {
        log.info("Sending relationship request from user {} to user {}", 
                request.getRequesterUserId(), request.getRecipientUserId());

        // Validate that users are different
        if (request.getRequesterUserId().equals(request.getRecipientUserId())) {
            throw new IllegalArgumentException("Cannot send relationship request to yourself");
        }

        // Validate that both users exist in User Service
        List<Long> userIds = List.of(request.getRequesterUserId(), request.getRecipientUserId());
        if (!userServiceClient.validateUsersExist(userIds)) {
            throw new IllegalArgumentException("One or more users do not exist in the system");
        }

        // Validate that both users are active
        if (!userServiceClient.isUserActive(request.getRequesterUserId())) {
            throw new IllegalArgumentException("Requester user is not active");
        }
        if (!userServiceClient.isUserActive(request.getRecipientUserId())) {
            throw new IllegalArgumentException("Recipient user is not active");
        }

        // Validate relationship type exists
        RelationshipType relationshipType = relationshipTypeService.getRelationshipTypeEntityById(request.getRelationshipTypeId());
        if (relationshipType == null) {
            throw new RelationshipTypeNotFoundException(request.getRelationshipTypeId());
        }

        // Check if there's already a pending request between these users
        if (hasPendingRequestBetweenUsers(request.getRequesterUserId(), request.getRecipientUserId())) {
            throw new IllegalStateException("There is already a pending relationship request between these users");
        }

        // Check if there's already an active relationship
        if (userRelationshipRepository.existsActiveRelationshipBetweenUsers(request.getRequesterUserId(), request.getRecipientUserId())) {
            throw new IllegalStateException("There is already an active relationship between these users");
        }

        // Create relationship request entity
        UserRelationship relationshipRequest = UserRelationship.builder()
                .user1Id(request.getRequesterUserId())
                .user2Id(request.getRecipientUserId())
                .relationshipType(relationshipType)
                .contextId(request.getContextId())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .status(RelationshipStatus.PENDING)
                .metadata(request.getMetadata())
                .build();

        // Save relationship request
        UserRelationship savedRequest = userRelationshipRepository.save(relationshipRequest);
        log.info("Created relationship request with ID: {}", savedRequest.getId());

        // Publish relationship request sent event
        try {
            String eventId = eventPublisherService.generateEventId();
            RelationshipRequestSentEvent event = RelationshipRequestSentEvent.create(
                    eventId,
                    savedRequest.getId(),
                    request.getRequesterUserId(),
                    request.getRecipientUserId(),
                    request.getRelationshipTypeId(),
                    relationshipType.getName(),
                    request.getRequestMessage(),
                    request.getContextId()
            );
            eventPublisherService.publishRelationshipEvent(event);
            log.info("Published relationship request sent event: {}", eventId);
        } catch (Exception e) {
            log.error("Failed to publish relationship request sent event", e);
            // Don't fail the main operation if event publishing fails
        }

        // Cache the relationship type for future use
        cacheService.cacheRelationshipTypeData(relationshipType.getId(), relationshipType);

        return convertToResponse(savedRequest);
    }

    @Override
    public RelationshipResponse respondToRelationshipRequest(RespondToRelationshipRequest request) {
        log.info("Responding to relationship request {} with action: {}", 
                request.getRelationshipId(), request.getAction());

        // Get the relationship request
        UserRelationship relationship = userRelationshipRepository.findById(request.getRelationshipId())
                .orElseThrow(() -> new RelationshipNotFoundException(request.getRelationshipId()));

        // Validate that the relationship is pending
        if (relationship.getStatus() != RelationshipStatus.PENDING) {
            throw new IllegalStateException("Relationship request is not pending");
        }

        // Validate that the responder user exists and is active
        if (!userServiceClient.validateUserExists(request.getResponderUserId())) {
            throw new IllegalArgumentException("Responder user does not exist in the system");
        }
        if (!userServiceClient.isUserActive(request.getResponderUserId())) {
            throw new IllegalArgumentException("Responder user is not active");
        }

        // Validate that the responder is the recipient
        if (!relationship.getUser2Id().equals(request.getResponderUserId())) {
            throw new IllegalArgumentException("Only the recipient can respond to the relationship request");
        }

        // Update relationship based on response
        if (request.getAction() == RespondToRelationshipRequest.ResponseAction.ACCEPT) {
            relationship.setStatus(RelationshipStatus.ACTIVE);
            log.info("Relationship request {} accepted", request.getRelationshipId());
        } else {
            relationship.setStatus(RelationshipStatus.ENDED);
            relationship.setEndDate(LocalDate.now());
            log.info("Relationship request {} rejected", request.getRelationshipId());
        }

        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);

        // Publish appropriate event based on response
        try {
            String eventId = eventPublisherService.generateEventId();
            if (request.getAction() == RespondToRelationshipRequest.ResponseAction.ACCEPT) {
                RelationshipRequestAcceptedEvent event = RelationshipRequestAcceptedEvent.create(
                        eventId,
                        updatedRelationship.getId(),
                        request.getResponderUserId(),
                        relationship.getUser1Id(),
                        relationship.getRelationshipType().getId(),
                        relationship.getRelationshipType().getName(),
                        request.getResponseMessage(),
                        relationship.getContextId()
                );
                eventPublisherService.publishRelationshipEvent(event);
                log.info("Published relationship request accepted event: {}", eventId);
            } else {
                RelationshipRequestRejectedEvent event = RelationshipRequestRejectedEvent.create(
                        eventId,
                        updatedRelationship.getId(),
                        request.getResponderUserId(),
                        relationship.getUser1Id(),
                        relationship.getRelationshipType().getId(),
                        relationship.getRelationshipType().getName(),
                        request.getResponseMessage(),
                        relationship.getContextId()
                );
                eventPublisherService.publishRelationshipEvent(event);
                log.info("Published relationship request rejected event: {}", eventId);
            }
        } catch (Exception e) {
            log.error("Failed to publish relationship response event", e);
            // Don't fail the main operation if event publishing fails
        }

        // Evict user relationships cache for both users
        cacheService.evictUserRelationshipsData(relationship.getUser1Id());
        cacheService.evictUserRelationshipsData(relationship.getUser2Id());

        return convertToResponse(updatedRelationship);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipResponse> getPendingRequestsForUser(Long userId, Pageable pageable) {
        log.debug("Getting pending requests for user: {}", userId);

        List<RelationshipResponse> requests = userRelationshipRepository.findByUser2IdAndStatus(userId, RelationshipStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Simple pagination implementation
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), requests.size());
        List<RelationshipResponse> pageContent = requests.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, requests.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipResponse> getSentRequestsByUser(Long userId, Pageable pageable) {
        log.debug("Getting sent requests by user: {}", userId);

        List<RelationshipResponse> requests = userRelationshipRepository.findByUser1IdAndStatus(userId, RelationshipStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Simple pagination implementation
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), requests.size());
        List<RelationshipResponse> pageContent = requests.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, requests.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipResponse> getReceivedRequestsByUser(Long userId, Pageable pageable) {
        log.debug("Getting received requests by user: {}", userId);

        List<RelationshipResponse> requests = userRelationshipRepository.findByUser2IdAndStatus(userId, RelationshipStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Simple pagination implementation
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), requests.size());
        List<RelationshipResponse> pageContent = requests.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, requests.size());
    }

    @Override
    public RelationshipResponse cancelRelationshipRequest(Long relationshipId, Long userId) {
        log.info("Canceling relationship request {} by user {}", relationshipId, userId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        // Validate that the relationship is pending
        if (relationship.getStatus() != RelationshipStatus.PENDING) {
            throw new IllegalStateException("Only pending relationship requests can be canceled");
        }

        // Validate that the user is the sender
        if (!relationship.getUser1Id().equals(userId)) {
            throw new IllegalArgumentException("Only the sender can cancel the relationship request");
        }

        // Cancel the request
        relationship.setStatus(RelationshipStatus.ENDED);
        relationship.setEndDate(LocalDate.now());
        UserRelationship updatedRelationship = userRelationshipRepository.save(relationship);

        log.info("Canceled relationship request {}", relationshipId);
        return convertToResponse(updatedRelationship);
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipResponse getRelationshipRequestById(Long relationshipId) {
        log.debug("Getting relationship request by ID: {}", relationshipId);

        UserRelationship relationship = userRelationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));

        return convertToResponse(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingRequestBetweenUsers(Long user1Id, Long user2Id) {
        return userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(user1Id, user2Id, RelationshipStatus.PENDING).isPresent() ||
               userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(user2Id, user1Id, RelationshipStatus.PENDING).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipResponse getPendingRequestBetweenUsers(Long user1Id, Long user2Id) {
        log.debug("Getting pending request between users {} and {}", user1Id, user2Id);

        UserRelationship relationship = userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(user1Id, user2Id, RelationshipStatus.PENDING)
                .orElse(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(user2Id, user1Id, RelationshipStatus.PENDING)
                        .orElse(null));

        if (relationship == null) {
            throw new RelationshipNotFoundException("No pending relationship request found between these users");
        }

        return convertToResponse(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingRequestsForUser(Long userId) {
        return userRelationshipRepository.findByUser2IdAndStatus(userId, RelationshipStatus.PENDING).size();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RelationshipResponse> getAllPendingRequests(Pageable pageable) {
        log.debug("Getting all pending requests");

        return userRelationshipRepository.findByStatus(RelationshipStatus.PENDING, pageable)
                .map(this::convertToResponse);
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
                .reverseType(null) // Don't include reverse type to avoid circular references
                .metadata(relationshipType.getMetadata())
                .createdAt(relationshipType.getCreatedAt())
                .updatedAt(relationshipType.getUpdatedAt())
                .build();
    }
}
