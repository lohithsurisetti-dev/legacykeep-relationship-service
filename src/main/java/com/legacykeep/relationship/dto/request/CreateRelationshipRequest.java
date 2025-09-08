package com.legacykeep.relationship.dto.request;

import com.legacykeep.relationship.enums.RelationshipStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating a new relationship between users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRelationshipRequest {

    /**
     * ID of the first user in the relationship
     */
    @NotNull(message = "User1 ID is required")
    @Positive(message = "User1 ID must be positive")
    private Long user1Id;

    /**
     * ID of the second user in the relationship
     */
    @NotNull(message = "User2 ID is required")
    @Positive(message = "User2 ID must be positive")
    private Long user2Id;

    /**
     * ID of the relationship type
     */
    @NotNull(message = "Relationship type ID is required")
    @Positive(message = "Relationship type ID must be positive")
    private Long relationshipTypeId;

    /**
     * Optional context ID (e.g., family circle ID)
     */
    private Long contextId;

    /**
     * Start date of the relationship
     */
    private LocalDate startDate;

    /**
     * End date of the relationship (optional)
     */
    private LocalDate endDate;

    /**
     * Status of the relationship
     */
    @Builder.Default
    private RelationshipStatus status = RelationshipStatus.ACTIVE;

    /**
     * Additional metadata as JSON string
     */
    private String metadata;
}

