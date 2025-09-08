package com.legacykeep.relationship.enums;

/**
 * Enum representing the status of a relationship between users.
 * Tracks the current state and lifecycle of relationships.
 */
public enum RelationshipStatus {
    
    /**
     * Active relationship - currently valid and ongoing
     */
    ACTIVE("Active", "Currently valid and ongoing relationship"),
    
    /**
     * Pending relationship - awaiting approval or confirmation
     */
    PENDING("Pending", "Relationship awaiting approval or confirmation"),
    
    /**
     * Suspended relationship - temporarily inactive
     */
    SUSPENDED("Suspended", "Temporarily inactive relationship"),
    
    /**
     * Ended relationship - permanently terminated
     */
    ENDED("Ended", "Permanently terminated relationship");

    private final String displayName;
    private final String description;

    RelationshipStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get status by name (case-insensitive)
     */
    public static RelationshipStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (RelationshipStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown relationship status: " + name);
    }

    /**
     * Check if the relationship is currently active
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Check if the relationship is pending approval
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * Check if the relationship is suspended
     */
    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    /**
     * Check if the relationship has ended
     */
    public boolean isEnded() {
        return this == ENDED;
    }

    /**
     * Check if the relationship is in a valid state (active or pending)
     */
    public boolean isValid() {
        return this == ACTIVE || this == PENDING;
    }

    /**
     * Check if the relationship can be modified
     */
    public boolean canBeModified() {
        return this == ACTIVE || this == PENDING || this == SUSPENDED;
    }

    /**
     * Check if the relationship can be ended
     */
    public boolean canBeEnded() {
        return this == ACTIVE || this == PENDING || this == SUSPENDED;
    }

    /**
     * Check if the relationship can be suspended
     */
    public boolean canBeSuspended() {
        return this == ACTIVE;
    }

    /**
     * Check if the relationship can be activated
     */
    public boolean canBeActivated() {
        return this == PENDING || this == SUSPENDED;
    }
}

