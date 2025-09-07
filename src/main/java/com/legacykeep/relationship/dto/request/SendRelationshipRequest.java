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
 * Request DTO for sending a relationship request to another user.
 * 
 * This represents the initial request that needs approval from the recipient.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendRelationshipRequest {

    /**
     * ID of the user who is sending the request (the requester)
     */
    @NotNull(message = "Requester user ID is required")
    @Positive(message = "Requester user ID must be positive")
    private Long requesterUserId;

    /**
     * ID of the user who will receive the request (the recipient)
     */
    @NotNull(message = "Recipient user ID is required")
    @Positive(message = "Recipient user ID must be positive")
    private Long recipientUserId;

    /**
     * ID of the relationship type being requested
     */
    @NotNull(message = "Relationship type ID is required")
    @Positive(message = "Relationship type ID must be positive")
    private Long relationshipTypeId;

    /**
     * Optional context ID (e.g., family circle ID)
     */
    private Long contextId;

    /**
     * Start date of the relationship (optional)
     */
    private LocalDate startDate;

    /**
     * Additional metadata as JSON string
     */
    private String metadata;

    /**
     * Message from the requester to the recipient
     */
    private String requestMessage;
}
