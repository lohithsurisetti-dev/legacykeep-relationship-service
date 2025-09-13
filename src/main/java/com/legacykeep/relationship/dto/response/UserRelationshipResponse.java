package com.legacykeep.relationship.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.legacykeep.relationship.entity.UserRelationship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for UserRelationship
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationshipResponse {

    private Long id;
    private Long user1Id;
    private Long user2Id;
    private RelationshipTypeResponse relationshipType;
    private Long contextId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String status;
    private String metadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    /**
     * Convert entity to response DTO
     */
    public static UserRelationshipResponse fromEntity(UserRelationship entity) {
        if (entity == null) {
            return null;
        }

        return UserRelationshipResponse.builder()
                .id(entity.getId())
                .user1Id(entity.getUser1Id())
                .user2Id(entity.getUser2Id())
                .relationshipType(RelationshipTypeResponse.fromEntity(entity.getRelationshipType()))
                .contextId(entity.getContextId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
