package com.legacykeep.relationship.dto.request;

import com.legacykeep.relationship.enums.RelationshipStatus;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing relationship.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRelationshipRequest {

    /**
     * ID of the relationship type (optional update)
     */
    @Positive(message = "Relationship type ID must be positive")
    private Long relationshipTypeId;

    /**
     * Context ID (optional update)
     */
    private Long contextId;

    /**
     * Start date of the relationship (optional update)
     */
    private LocalDate startDate;

    /**
     * End date of the relationship (optional update)
     */
    private LocalDate endDate;

    /**
     * Status of the relationship (optional update)
     */
    private RelationshipStatus status;

    /**
     * Additional metadata as JSON string (optional update)
     */
    private String metadata;
}

