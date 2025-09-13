package com.legacykeep.relationship.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing relationship type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRelationshipTypeRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Pattern(regexp = "^(FAMILY|SOCIAL|PROFESSIONAL|CUSTOM)$", 
             message = "Category must be one of: FAMILY, SOCIAL, PROFESSIONAL, CUSTOM")
    private String category;

    private Boolean bidirectional;

    private Long reverseTypeId;

    private String metadata;
}
