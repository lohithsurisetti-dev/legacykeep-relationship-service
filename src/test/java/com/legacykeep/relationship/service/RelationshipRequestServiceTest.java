package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.RespondToRelationshipRequest;
import com.legacykeep.relationship.dto.request.SendRelationshipRequest;
import com.legacykeep.relationship.dto.response.RelationshipResponse;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.entity.UserRelationship;
import com.legacykeep.relationship.enums.RelationshipCategory;
import com.legacykeep.relationship.enums.RelationshipStatus;
import com.legacykeep.relationship.repository.UserRelationshipRepository;
import com.legacykeep.relationship.service.impl.RelationshipRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RelationshipRequestService.
 * 
 * Tests the two-way approval workflow for relationships.
 */
@ExtendWith(MockitoExtension.class)
class RelationshipRequestServiceTest {

    @Mock
    private UserRelationshipRepository userRelationshipRepository;

    @Mock
    private RelationshipTypeService relationshipTypeService;

    @InjectMocks
    private RelationshipRequestServiceImpl relationshipRequestService;

    private RelationshipType brotherRelationshipType;
    private UserRelationship pendingRelationship;
    private UserRelationship activeRelationship;

    @BeforeEach
    void setUp() {
        // Setup relationship type
        brotherRelationshipType = RelationshipType.builder()
                .id(1L)
                .name("Brother")
                .category(RelationshipCategory.FAMILY)
                .bidirectional(true)
                .build();

        // Setup pending relationship
        pendingRelationship = UserRelationship.builder()
                .id(1L)
                .user1Id(1L)  // Requester
                .user2Id(2L)  // Recipient
                .relationshipType(brotherRelationshipType)
                .status(RelationshipStatus.PENDING)
                .startDate(LocalDate.now())
                .build();

        // Setup active relationship
        activeRelationship = UserRelationship.builder()
                .id(1L)
                .user1Id(1L)
                .user2Id(2L)
                .relationshipType(brotherRelationshipType)
                .status(RelationshipStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();
    }

    @Test
    void testSendRelationshipRequest_Success() {
        // Given
        SendRelationshipRequest request = SendRelationshipRequest.builder()
                .requesterUserId(1L)
                .recipientUserId(2L)
                .relationshipTypeId(1L)
                .requestMessage("Hi, I would like to add you as my brother.")
                .build();

        when(relationshipTypeService.getRelationshipTypeEntityById(1L)).thenReturn(brotherRelationshipType);
        when(userRelationshipRepository.existsActiveRelationshipBetweenUsers(1L, 2L)).thenReturn(false);
        when(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(1L, 2L, RelationshipStatus.PENDING))
                .thenReturn(Optional.empty());
        when(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(2L, 1L, RelationshipStatus.PENDING))
                .thenReturn(Optional.empty());
        when(userRelationshipRepository.save(any(UserRelationship.class))).thenReturn(pendingRelationship);

        // When
        RelationshipResponse response = relationshipRequestService.sendRelationshipRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUser1Id());
        assertEquals(2L, response.getUser2Id());
        assertEquals(RelationshipStatus.PENDING, response.getStatus());
        assertEquals("Brother", response.getRelationshipType().getName());

        verify(userRelationshipRepository).save(any(UserRelationship.class));
    }

    @Test
    void testSendRelationshipRequest_SelfRequest_ThrowsException() {
        // Given
        SendRelationshipRequest request = SendRelationshipRequest.builder()
                .requesterUserId(1L)
                .recipientUserId(1L)  // Same user
                .relationshipTypeId(1L)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.sendRelationshipRequest(request);
        });

        assertEquals("Cannot send relationship request to yourself", exception.getMessage());
        verify(userRelationshipRepository, never()).save(any(UserRelationship.class));
    }

    @Test
    void testSendRelationshipRequest_ExistingPendingRequest_ThrowsException() {
        // Given
        SendRelationshipRequest request = SendRelationshipRequest.builder()
                .requesterUserId(1L)
                .recipientUserId(2L)
                .relationshipTypeId(1L)
                .build();

        when(relationshipTypeService.getRelationshipTypeEntityById(1L)).thenReturn(brotherRelationshipType);
        when(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(1L, 2L, RelationshipStatus.PENDING))
                .thenReturn(Optional.of(pendingRelationship));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            relationshipRequestService.sendRelationshipRequest(request);
        });

        assertEquals("There is already a pending relationship request between these users", exception.getMessage());
        verify(userRelationshipRepository, never()).save(any(UserRelationship.class));
    }

    @Test
    void testRespondToRelationshipRequest_Accept_Success() {
        // Given
        RespondToRelationshipRequest request = RespondToRelationshipRequest.builder()
                .relationshipId(1L)
                .responderUserId(2L)  // Recipient
                .action(RespondToRelationshipRequest.ResponseAction.ACCEPT)
                .responseMessage("Sure, I accept you as my brother!")
                .build();

        when(userRelationshipRepository.findById(1L)).thenReturn(Optional.of(pendingRelationship));
        when(userRelationshipRepository.save(any(UserRelationship.class))).thenReturn(activeRelationship);

        // When
        RelationshipResponse response = relationshipRequestService.respondToRelationshipRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(RelationshipStatus.ACTIVE, response.getStatus());
        verify(userRelationshipRepository).save(any(UserRelationship.class));
    }

    @Test
    void testRespondToRelationshipRequest_Reject_Success() {
        // Given
        RespondToRelationshipRequest request = RespondToRelationshipRequest.builder()
                .relationshipId(1L)
                .responderUserId(2L)
                .action(RespondToRelationshipRequest.ResponseAction.REJECT)
                .responseMessage("Sorry, I don't want to establish this relationship.")
                .build();

        UserRelationship rejectedRelationship = UserRelationship.builder()
                .id(1L)
                .user1Id(1L)
                .user2Id(2L)
                .relationshipType(brotherRelationshipType)
                .status(RelationshipStatus.ENDED)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        when(userRelationshipRepository.findById(1L)).thenReturn(Optional.of(pendingRelationship));
        when(userRelationshipRepository.save(any(UserRelationship.class))).thenReturn(rejectedRelationship);

        // When
        RelationshipResponse response = relationshipRequestService.respondToRelationshipRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(RelationshipStatus.ENDED, response.getStatus());
        verify(userRelationshipRepository).save(any(UserRelationship.class));
    }

    @Test
    void testRespondToRelationshipRequest_WrongUser_ThrowsException() {
        // Given
        RespondToRelationshipRequest request = RespondToRelationshipRequest.builder()
                .relationshipId(1L)
                .responderUserId(3L)  // Wrong user (not the recipient)
                .action(RespondToRelationshipRequest.ResponseAction.ACCEPT)
                .build();

        when(userRelationshipRepository.findById(1L)).thenReturn(Optional.of(pendingRelationship));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.respondToRelationshipRequest(request);
        });

        assertEquals("Only the recipient can respond to the relationship request", exception.getMessage());
        verify(userRelationshipRepository, never()).save(any(UserRelationship.class));
    }

    @Test
    void testCancelRelationshipRequest_Success() {
        // Given
        Long relationshipId = 1L;
        Long userId = 1L;  // Sender

        UserRelationship canceledRelationship = UserRelationship.builder()
                .id(1L)
                .user1Id(1L)
                .user2Id(2L)
                .relationshipType(brotherRelationshipType)
                .status(RelationshipStatus.ENDED)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        when(userRelationshipRepository.findById(relationshipId)).thenReturn(Optional.of(pendingRelationship));
        when(userRelationshipRepository.save(any(UserRelationship.class))).thenReturn(canceledRelationship);

        // When
        RelationshipResponse response = relationshipRequestService.cancelRelationshipRequest(relationshipId, userId);

        // Then
        assertNotNull(response);
        assertEquals(RelationshipStatus.ENDED, response.getStatus());
        verify(userRelationshipRepository).save(any(UserRelationship.class));
    }

    @Test
    void testCancelRelationshipRequest_WrongUser_ThrowsException() {
        // Given
        Long relationshipId = 1L;
        Long userId = 3L;  // Wrong user (not the sender)

        when(userRelationshipRepository.findById(relationshipId)).thenReturn(Optional.of(pendingRelationship));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.cancelRelationshipRequest(relationshipId, userId);
        });

        assertEquals("Only the sender can cancel the relationship request", exception.getMessage());
        verify(userRelationshipRepository, never()).save(any(UserRelationship.class));
    }

    @Test
    void testHasPendingRequestBetweenUsers_True() {
        // Given
        when(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(1L, 2L, RelationshipStatus.PENDING))
                .thenReturn(Optional.of(pendingRelationship));

        // When
        boolean hasPending = relationshipRequestService.hasPendingRequestBetweenUsers(1L, 2L);

        // Then
        assertTrue(hasPending);
    }

    @Test
    void testHasPendingRequestBetweenUsers_False() {
        // Given
        when(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(1L, 2L, RelationshipStatus.PENDING))
                .thenReturn(Optional.empty());
        when(userRelationshipRepository.findByUser1IdAndUser2IdAndStatus(2L, 1L, RelationshipStatus.PENDING))
                .thenReturn(Optional.empty());

        // When
        boolean hasPending = relationshipRequestService.hasPendingRequestBetweenUsers(1L, 2L);

        // Then
        assertFalse(hasPending);
    }

    @Test
    void testGetPendingRequestsForUser() {
        // Given
        Long userId = 2L;
        List<UserRelationship> pendingRequests = Arrays.asList(pendingRelationship);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRelationshipRepository.findByUser2IdAndStatus(userId, RelationshipStatus.PENDING))
                .thenReturn(pendingRequests);

        // When
        Page<RelationshipResponse> response = relationshipRequestService.getPendingRequestsForUser(userId, pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(RelationshipStatus.PENDING, response.getContent().get(0).getStatus());
    }

    @Test
    void testCountPendingRequestsForUser() {
        // Given
        Long userId = 2L;
        List<UserRelationship> pendingRequests = Arrays.asList(pendingRelationship, pendingRelationship);

        when(userRelationshipRepository.findByUser2IdAndStatus(userId, RelationshipStatus.PENDING))
                .thenReturn(pendingRequests);

        // When
        long count = relationshipRequestService.countPendingRequestsForUser(userId);

        // Then
        assertEquals(2, count);
    }
}

