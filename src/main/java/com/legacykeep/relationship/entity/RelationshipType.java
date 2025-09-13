package com.legacykeep.relationship.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entity representing a relationship type (e.g., Father, Mother, Friend, Colleague)
 * This defines the nature of relationships between users.
 */
@Entity
@Table(name = "relationship_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private RelationshipCategory category;

    @Column(name = "bidirectional", nullable = false)
    @Builder.Default
    private Boolean bidirectional = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reverse_type_id")
    private RelationshipType reverseType;

    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for relationship categories
     */
    public enum RelationshipCategory {
        FAMILY,
        SOCIAL,
        PROFESSIONAL,
        CUSTOM
    }
}
