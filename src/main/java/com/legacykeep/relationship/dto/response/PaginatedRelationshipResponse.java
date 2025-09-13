package com.legacykeep.relationship.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Response DTO for paginated relationship results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedRelationshipResponse {

    private List<UserRelationshipResponse> relationships;
    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }

    /**
     * Convert Page of entities to paginated response DTO
     */
    public static PaginatedRelationshipResponse fromPage(Page<com.legacykeep.relationship.entity.UserRelationship> page) {
        List<UserRelationshipResponse> relationships = page.getContent()
                .stream()
                .map(UserRelationshipResponse::fromEntity)
                .collect(Collectors.toList());

        PaginationInfo pagination = PaginationInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();

        return PaginatedRelationshipResponse.builder()
                .relationships(relationships)
                .pagination(pagination)
                .build();
    }
}
