package com.legacykeep.relationship.service;

import com.legacykeep.relationship.service.impl.UserServiceClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test for UserServiceClient.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceClientImpl userServiceClient;

    private final String USER_SERVICE_BASE_URL = "http://localhost:8082";
    private final Long EXISTING_USER_ID = 1L;
    private final Long NON_EXISTING_USER_ID = 999L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userServiceClient, "userServiceBaseUrl", USER_SERVICE_BASE_URL);
        ReflectionTestUtils.setField(userServiceClient, "timeoutMs", 5000);
    }

    @Test
    void testValidateUserExists_WithExistingUser_ShouldReturnTrue() {
        // Given
        Map<String, Object> responseBody = Map.of("data", Map.of("userId", EXISTING_USER_ID));
        ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(response);

        // When
        boolean result = userServiceClient.validateUserExists(EXISTING_USER_ID);

        // Then
        assertTrue(result);
        verify(restTemplate).getForEntity(
                USER_SERVICE_BASE_URL + "/api/v1/users/" + EXISTING_USER_ID + "/profile", 
                Map.class
        );
    }

    @Test
    void testValidateUserExists_WithNonExistingUser_ShouldReturnFalse() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));

        // When
        boolean result = userServiceClient.validateUserExists(NON_EXISTING_USER_ID);

        // Then
        assertFalse(result);
        verify(restTemplate).getForEntity(
                USER_SERVICE_BASE_URL + "/api/v1/users/" + NON_EXISTING_USER_ID + "/profile", 
                Map.class
        );
    }

    @Test
    void testValidateUserExists_WithNullUserId_ShouldReturnFalse() {
        // When
        boolean result = userServiceClient.validateUserExists(null);

        // Then
        assertFalse(result);
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void testValidateUsersExist_WithAllExistingUsers_ShouldReturnTrue() {
        // Given
        List<Long> userIds = List.of(1L, 2L, 3L);
        Map<String, Object> responseBody = Map.of("data", Map.of("userId", 1L));
        ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(response);

        // When
        boolean result = userServiceClient.validateUsersExist(userIds);

        // Then
        assertTrue(result);
        verify(restTemplate, times(3)).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void testValidateUsersExist_WithNonExistingUser_ShouldReturnFalse() {
        // Given
        List<Long> userIds = List.of(1L, NON_EXISTING_USER_ID);
        Map<String, Object> responseBody = Map.of("data", Map.of("userId", 1L));
        ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(response) // First call succeeds
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found")); // Second call fails

        // When
        boolean result = userServiceClient.validateUsersExist(userIds);

        // Then
        assertFalse(result);
        verify(restTemplate, times(2)).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void testValidateUsersExist_WithNullList_ShouldReturnFalse() {
        // When
        boolean result = userServiceClient.validateUsersExist(null);

        // Then
        assertFalse(result);
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void testValidateUsersExist_WithEmptyList_ShouldReturnFalse() {
        // When
        boolean result = userServiceClient.validateUsersExist(List.of());

        // Then
        assertFalse(result);
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void testGetUserProfile_WithExistingUser_ShouldReturnProfileData() {
        // Given
        Map<String, Object> userData = Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "displayName", "John Doe",
                "isPublic", true
        );
        Map<String, Object> responseBody = Map.of("data", userData);
        ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(response);

        // When
        UserServiceClient.UserProfileData result = userServiceClient.getUserProfile(EXISTING_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(EXISTING_USER_ID, result.getUserId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("John Doe", result.getDisplayName());
        assertTrue(result.isActive());
        assertTrue(result.isPublic());
        verify(restTemplate).getForEntity(
                USER_SERVICE_BASE_URL + "/api/v1/users/" + EXISTING_USER_ID + "/profile", 
                Map.class
        );
    }

    @Test
    void testGetUserProfile_WithNonExistingUser_ShouldReturnNull() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));

        // When
        UserServiceClient.UserProfileData result = userServiceClient.getUserProfile(NON_EXISTING_USER_ID);

        // Then
        assertNull(result);
        verify(restTemplate).getForEntity(
                USER_SERVICE_BASE_URL + "/api/v1/users/" + NON_EXISTING_USER_ID + "/profile", 
                Map.class
        );
    }

    @Test
    void testIsUserActive_WithActiveUser_ShouldReturnTrue() {
        // Given
        Map<String, Object> userData = Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "displayName", "John Doe",
                "isPublic", true
        );
        Map<String, Object> responseBody = Map.of("data", userData);
        ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(response);

        // When
        boolean result = userServiceClient.isUserActive(EXISTING_USER_ID);

        // Then
        assertTrue(result);
        verify(restTemplate).getForEntity(
                USER_SERVICE_BASE_URL + "/api/v1/users/" + EXISTING_USER_ID + "/profile", 
                Map.class
        );
    }

    @Test
    void testIsUserActive_WithNonExistingUser_ShouldReturnFalse() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));

        // When
        boolean result = userServiceClient.isUserActive(NON_EXISTING_USER_ID);

        // Then
        assertFalse(result);
        verify(restTemplate).getForEntity(
                USER_SERVICE_BASE_URL + "/api/v1/users/" + NON_EXISTING_USER_ID + "/profile", 
                Map.class
        );
    }
}
