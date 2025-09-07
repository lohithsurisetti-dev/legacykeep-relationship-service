package com.legacykeep.relationship.dto.request;

import com.legacykeep.relationship.enums.RelationshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for responding to a relationship request.
 * 
 * Used when a user accepts or rejects a relationship request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespondToRelationshipRequest {

    /**
     * ID of the relationship request
     */
    @NotNull(message = "Relationship ID is required")
    private Long relationshipId;

    /**
     * ID of the user responding to the request
     */
    @NotNull(message = "Responder user ID is required")
    private Long responderUserId;

    /**
     * Response action: ACCEPT or REJECT
     */
    @NotNull(message = "Response action is required")
    private ResponseAction action;

    /**
     * Optional message from the responder
     */
    private String responseMessage;

    /**
     * Enum for response actions
     */
    public enum ResponseAction {
        ACCEPT,
        REJECT
    }
}
