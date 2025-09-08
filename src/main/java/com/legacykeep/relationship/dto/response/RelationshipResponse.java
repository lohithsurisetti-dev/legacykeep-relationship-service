package com.legacykeep.relationship.dto.response;

import com.legacykeep.relationship.enums.RelationshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for relationship data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipResponse {

    /**
     * Relationship ID
     */
    private Long id;

    /**
     * ID of the first user
     */
    private Long user1Id;

    /**
     * ID of the second user
     */
    private Long user2Id;

    /**
     * Relationship type information
     */
    private RelationshipTypeResponse relationshipType;

    /**
     * Context ID
     */
    private Long contextId;

    /**
     * Start date of the relationship
     */
    private LocalDate startDate;

    /**
     * End date of the relationship
     */
    private LocalDate endDate;

    /**
     * Status of the relationship
     */
    private RelationshipStatus status;

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
     * Whether the relationship is currently active
     */
    public boolean isActive() {
        return status == RelationshipStatus.ACTIVE;
    }

    /**
     * Whether the relationship has ended
     */
    public boolean isEnded() {
        return status == RelationshipStatus.ENDED;
    }

    /**
     * Whether the relationship is pending
     */
    public boolean isPending() {
        return status == RelationshipStatus.PENDING;
    }

    /**
     * Whether the relationship is suspended
     */
    public boolean isSuspended() {
        return status == RelationshipStatus.SUSPENDED;
    }
}

