package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.request.SendRelationshipRequest;
import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.enums.RelationshipCategory;
import com.legacykeep.relationship.enums.RelationshipStatus;
import com.legacykeep.relationship.repository.RelationshipTypeRepository;
import com.legacykeep.relationship.repository.UserRelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Integration test for User Service validation in Relationship Service.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private RelationshipRequestService relationshipRequestService;

    @Autowired
    private RelationshipTypeRepository relationshipTypeRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    private RelationshipType testRelationshipType;
    private final Long EXISTING_USER_ID = 1L;
    private final Long NON_EXISTING_USER_ID = 999L;
    private final Long INACTIVE_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        // Create a test relationship type
        testRelationshipType = new RelationshipType();
        testRelationshipType.setName("Test Relationship");
        testRelationshipType.setCategory(RelationshipCategory.FAMILY);
        testRelationshipType.setBidirectional(true);
        testRelationshipType.setMetadata(null); // Set to null to avoid JSONB issues
        testRelationshipType = relationshipTypeRepository.save(testRelationshipType);

        // Mock User Service responses
        when(userServiceClient.validateUserExists(EXISTING_USER_ID)).thenReturn(true);
        when(userServiceClient.validateUserExists(NON_EXISTING_USER_ID)).thenReturn(false);
        when(userServiceClient.validateUserExists(INACTIVE_USER_ID)).thenReturn(true);
        
        when(userServiceClient.isUserActive(EXISTING_USER_ID)).thenReturn(true);
        when(userServiceClient.isUserActive(INACTIVE_USER_ID)).thenReturn(false);
        
        when(userServiceClient.validateUsersExist(anyList())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            java.util.List<Long> userIds = invocation.getArgument(0);
            return userIds.stream().allMatch(id -> id.equals(EXISTING_USER_ID));
        });
    }

    @Test
    void testSendRelationshipRequest_WithValidUsers_ShouldSucceed() {
        // Given
        SendRelationshipRequest request = new SendRelationshipRequest();
        request.setRequesterUserId(EXISTING_USER_ID);
        request.setRecipientUserId(EXISTING_USER_ID + 1);
        request.setRelationshipTypeId(testRelationshipType.getId());
        request.setRequestMessage("Test relationship request");
        request.setStartDate(LocalDate.now());

        // Mock both users as existing and active
        when(userServiceClient.validateUserExists(EXISTING_USER_ID + 1)).thenReturn(true);
        when(userServiceClient.isUserActive(EXISTING_USER_ID + 1)).thenReturn(true);
        when(userServiceClient.validateUsersExist(anyList())).thenReturn(true);

        // When
        var response = relationshipRequestService.sendRelationshipRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(RelationshipStatus.PENDING, response.getStatus());
        verify(userServiceClient).validateUsersExist(anyList());
        verify(userServiceClient).isUserActive(EXISTING_USER_ID);
        verify(userServiceClient).isUserActive(EXISTING_USER_ID + 1);
    }

    @Test
    void testSendRelationshipRequest_WithNonExistingUser_ShouldFail() {
        // Given
        SendRelationshipRequest request = new SendRelationshipRequest();
        request.setRequesterUserId(EXISTING_USER_ID);
        request.setRecipientUserId(NON_EXISTING_USER_ID);
        request.setRelationshipTypeId(testRelationshipType.getId());
        request.setRequestMessage("Test relationship request");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.sendRelationshipRequest(request);
        });

        assertEquals("One or more users do not exist in the system", exception.getMessage());
        verify(userServiceClient).validateUsersExist(anyList());
    }

    @Test
    void testSendRelationshipRequest_WithInactiveUser_ShouldFail() {
        // Given
        SendRelationshipRequest request = new SendRelationshipRequest();
        request.setRequesterUserId(EXISTING_USER_ID);
        request.setRecipientUserId(INACTIVE_USER_ID);
        request.setRelationshipTypeId(testRelationshipType.getId());
        request.setRequestMessage("Test relationship request");

        // Mock user exists but is inactive
        when(userServiceClient.validateUsersExist(anyList())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.sendRelationshipRequest(request);
        });

        assertEquals("Recipient user is not active", exception.getMessage());
        verify(userServiceClient).validateUsersExist(anyList());
        verify(userServiceClient).isUserActive(EXISTING_USER_ID);
        verify(userServiceClient).isUserActive(INACTIVE_USER_ID);
    }

    @Test
    void testSendRelationshipRequest_WithInactiveRequester_ShouldFail() {
        // Given
        SendRelationshipRequest request = new SendRelationshipRequest();
        request.setRequesterUserId(INACTIVE_USER_ID);
        request.setRecipientUserId(EXISTING_USER_ID);
        request.setRelationshipTypeId(testRelationshipType.getId());
        request.setRequestMessage("Test relationship request");

        // Mock user exists but is inactive
        when(userServiceClient.validateUsersExist(anyList())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.sendRelationshipRequest(request);
        });

        assertEquals("Requester user is not active", exception.getMessage());
        verify(userServiceClient).validateUsersExist(anyList());
        verify(userServiceClient).isUserActive(INACTIVE_USER_ID);
    }

    @Test
    void testSendRelationshipRequest_WithSameUser_ShouldFail() {
        // Given
        SendRelationshipRequest request = new SendRelationshipRequest();
        request.setRequesterUserId(EXISTING_USER_ID);
        request.setRecipientUserId(EXISTING_USER_ID); // Same user
        request.setRelationshipTypeId(testRelationshipType.getId());
        request.setRequestMessage("Test relationship request");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            relationshipRequestService.sendRelationshipRequest(request);
        });

        assertEquals("Cannot send relationship request to yourself", exception.getMessage());
        // Should not call User Service for same user validation
        verify(userServiceClient, never()).validateUsersExist(anyList());
    }
}
