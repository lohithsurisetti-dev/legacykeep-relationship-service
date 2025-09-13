package com.legacykeep.relationship.dto.request;

import com.legacykeep.relationship.entity.RelationshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new relationship type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRelationshipTypeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Category is required")
    @Pattern(regexp = "^(FAMILY|SOCIAL|PROFESSIONAL|CUSTOM)$", 
             message = "Category must be one of: FAMILY, SOCIAL, PROFESSIONAL, CUSTOM")
    private String category;

    @Builder.Default
    private Boolean bidirectional = false;

    private Long reverseTypeId;

    private String metadata;
}
