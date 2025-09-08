package com.legacykeep.relationship.service.impl;

import com.legacykeep.relationship.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

/**
 * Implementation of UserServiceClient using HTTP REST calls.
 * 
 * Communicates with User Service to validate user existence and retrieve user data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClientImpl implements UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.base-url:http://localhost:8082}")
    private String userServiceBaseUrl;

    @Value("${user.service.timeout:5000}")
    private int timeoutMs;

    @Override
    public boolean validateUserExists(Long userId) {
        if (userId == null) {
            log.warn("User ID is null, cannot validate user existence");
            return false;
        }

        try {
            String url = userServiceBaseUrl + "/api/v1/users/" + userId + "/profile";
            log.debug("Validating user existence for user ID: {} at URL: {}", userId, url);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("User {} exists in User Service", userId);
                return true;
            } else {
                log.warn("User {} validation failed with status: {}", userId, response.getStatusCode());
                return false;
            }

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User {} not found in User Service", userId);
            return false;
        } catch (HttpClientErrorException e) {
            log.error("Error validating user {} existence: HTTP {} - {}", 
                     userId, e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        } catch (ResourceAccessException e) {
            log.error("Timeout or connection error validating user {} existence: {}", userId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error validating user {} existence: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean validateUsersExist(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("User IDs list is null or empty, cannot validate users existence");
            return false;
        }

        log.debug("Validating existence of {} users", userIds.size());
        
        for (Long userId : userIds) {
            if (!validateUserExists(userId)) {
                log.warn("User {} does not exist, validation failed for user list", userId);
                return false;
            }
        }

        log.debug("All {} users exist in User Service", userIds.size());
        return true;
    }

    @Override
    public UserProfileData getUserProfile(Long userId) {
        if (userId == null) {
            log.warn("User ID is null, cannot retrieve user profile");
            return null;
        }

        try {
            String url = userServiceBaseUrl + "/api/v1/users/" + userId + "/profile";
            log.debug("Retrieving user profile for user ID: {} at URL: {}", userId, url);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                
                if (data != null) {
                    UserProfileData profileData = new UserProfileData();
                    profileData.setUserId(userId);
                    profileData.setFirstName((String) data.get("firstName"));
                    profileData.setLastName((String) data.get("lastName"));
                    profileData.setDisplayName((String) data.get("displayName"));
                    profileData.setPublic((Boolean) data.getOrDefault("isPublic", true));
                    profileData.setActive(true); // Assume active if profile exists
                    
                    log.debug("Retrieved user profile for user {}: {}", userId, profileData);
                    return profileData;
                }
            }

            log.warn("User {} profile data not found or invalid response", userId);
            return null;

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User {} profile not found in User Service", userId);
            return null;
        } catch (HttpClientErrorException e) {
            log.error("Error retrieving user {} profile: HTTP {} - {}", 
                     userId, e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (ResourceAccessException e) {
            log.error("Timeout or connection error retrieving user {} profile: {}", userId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error retrieving user {} profile: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean isUserActive(Long userId) {
        UserProfileData profile = getUserProfile(userId);
        return profile != null && profile.isActive();
    }
}

