package com.legacykeep.relationship.dto.request;

import com.legacykeep.relationship.enums.RelationshipCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new relationship type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRelationshipTypeRequest {

    /**
     * Name of the relationship type
     */
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    /**
     * Category of the relationship type
     */
    @NotNull(message = "Category is required")
    private RelationshipCategory category;

    /**
     * Whether this relationship type is bidirectional
     */
    @Builder.Default
    private Boolean bidirectional = false;

    /**
     * ID of the reverse relationship type (optional)
     */
    @Positive(message = "Reverse type ID must be positive")
    private Long reverseTypeId;

    /**
     * Additional metadata as JSON string
     */
    private String metadata;
}

