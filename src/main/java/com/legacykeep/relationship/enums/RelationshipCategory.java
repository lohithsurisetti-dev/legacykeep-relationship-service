package com.legacykeep.relationship.enums;

/**
 * Enum representing the categories of relationships in the system.
 * Each category groups related relationship types together.
 */
public enum RelationshipCategory {
    
    /**
     * Family relationships - blood relations, marriage, adoption, etc.
     */
    FAMILY("Family", "Blood relations, marriage, adoption, and family connections"),
    
    /**
     * Social relationships - friendships, acquaintances, neighbors, etc.
     */
    SOCIAL("Social", "Friendships, acquaintances, neighbors, and social connections"),
    
    /**
     * Professional relationships - work colleagues, business partners, etc.
     */
    PROFESSIONAL("Professional", "Work colleagues, business partners, and professional connections"),
    
    /**
     * Custom relationships - user-defined relationship types
     */
    CUSTOM("Custom", "User-defined relationship types");

    private final String displayName;
    private final String description;

    RelationshipCategory(String displayName, String description) {
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
     * Get category by name (case-insensitive)
     */
    public static RelationshipCategory fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (RelationshipCategory category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        
        throw new IllegalArgumentException("Unknown relationship category: " + name);
    }

    /**
     * Check if the category is a predefined category
     */
    public boolean isPredefined() {
        return this != CUSTOM;
    }

    /**
     * Check if the category allows custom relationship types
     */
    public boolean allowsCustomTypes() {
        return this == CUSTOM;
    }
}
