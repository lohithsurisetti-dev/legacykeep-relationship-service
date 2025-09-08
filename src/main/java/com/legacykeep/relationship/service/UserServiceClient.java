package com.legacykeep.relationship.service;

import java.util.List;

/**
 * Client interface for communicating with User Service.
 * 
 * Provides methods to validate user existence and retrieve user data
 * for relationship operations.
 */
public interface UserServiceClient {

    /**
     * Validate that a user exists in the User Service.
     * 
     * @param userId The user ID to validate
     * @return true if user exists, false otherwise
     */
    boolean validateUserExists(Long userId);

    /**
     * Validate that multiple users exist in the User Service.
     * 
     * @param userIds List of user IDs to validate
     * @return true if all users exist, false if any user doesn't exist
     */
    boolean validateUsersExist(List<Long> userIds);

    /**
     * Get user profile information for relationship context.
     * 
     * @param userId The user ID
     * @return User profile data or null if user doesn't exist
     */
    UserProfileData getUserProfile(Long userId);

    /**
     * Check if user is active and can participate in relationships.
     * 
     * @param userId The user ID
     * @return true if user is active, false otherwise
     */
    boolean isUserActive(Long userId);

    /**
     * Data class for user profile information needed by Relationship Service.
     */
    class UserProfileData {
        private Long userId;
        private String firstName;
        private String lastName;
        private String displayName;
        private boolean isActive;
        private boolean isPublic;

        // Constructors
        public UserProfileData() {}

        public UserProfileData(Long userId, String firstName, String lastName, 
                             String displayName, boolean isActive, boolean isPublic) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
            this.isActive = isActive;
            this.isPublic = isPublic;
        }

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }

        public boolean isPublic() { return isPublic; }
        public void setPublic(boolean aPublic) { isPublic = aPublic; }

        @Override
        public String toString() {
            return "UserProfileData{" +
                    "userId=" + userId +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", isActive=" + isActive +
                    ", isPublic=" + isPublic +
                    '}';
        }
    }
}

