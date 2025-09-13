package com.legacykeep.relationship.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating a new relationship
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRelationshipRequest {

    @NotNull(message = "User1 ID is required")
    @Positive(message = "User1 ID must be positive")
    private Long user1Id;

    @NotNull(message = "User2 ID is required")
    @Positive(message = "User2 ID must be positive")
    private Long user2Id;

    @NotNull(message = "Relationship type ID is required")
    @Positive(message = "Relationship type ID must be positive")
    private Long relationshipTypeId;

    private Long contextId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private String metadata;
}
