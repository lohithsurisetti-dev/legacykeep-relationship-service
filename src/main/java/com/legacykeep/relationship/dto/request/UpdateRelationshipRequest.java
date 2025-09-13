package com.legacykeep.relationship.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.legacykeep.relationship.entity.UserRelationship;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing relationship
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRelationshipRequest {

    @Pattern(regexp = "^(ACTIVE|ENDED|SUSPENDED|PENDING)$", 
             message = "Status must be one of: ACTIVE, ENDED, SUSPENDED, PENDING")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String metadata;
}
