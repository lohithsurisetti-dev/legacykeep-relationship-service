package com.legacykeep.relationship.dto.response;

import com.legacykeep.relationship.enums.RelationshipCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for relationship type data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipTypeResponse {

    /**
     * Relationship type ID
     */
    private Long id;

    /**
     * Name of the relationship type
     */
    private String name;

    /**
     * Category of the relationship type
     */
    private RelationshipCategory category;

    /**
     * Whether this relationship type is bidirectional
     */
    private Boolean bidirectional;

    /**
     * Reverse relationship type information
     */
    private RelationshipTypeResponse reverseType;

    /**
     * Additional metadata
     */
    private String metadata;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Whether this relationship type has a reverse type
     */
    public boolean hasReverseType() {
        return reverseType != null;
    }

    /**
     * Whether this is a bidirectional relationship type
     */
    public boolean isBidirectional() {
        return Boolean.TRUE.equals(bidirectional);
    }
}
