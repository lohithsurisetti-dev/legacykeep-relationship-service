package com.legacykeep.relationship.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.legacykeep.relationship.entity.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for RelationshipType
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipTypeResponse {

    private Long id;
    private String name;
    private String category;
    private Boolean bidirectional;
    private Long reverseTypeId;
    private String metadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    /**
     * Convert entity to response DTO
     */
    public static RelationshipTypeResponse fromEntity(RelationshipType entity) {
        if (entity == null) {
            return null;
        }

        return RelationshipTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory() != null ? entity.getCategory().name() : null)
                .bidirectional(entity.getBidirectional())
                .reverseTypeId(entity.getReverseType() != null ? entity.getReverseType().getId() : null)
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
