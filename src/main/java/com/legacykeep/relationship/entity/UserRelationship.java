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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a relationship between two users
 * This is the core entity that stores actual user relationships.
 */
@Entity
@Table(name = "user_relationships")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_type_id", nullable = false)
    private RelationshipType relationshipType;

    @Column(name = "context_id")
    private Long contextId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RelationshipStatus status = RelationshipStatus.ACTIVE;

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
     * Enum for relationship status
     */
    public enum RelationshipStatus {
        ACTIVE,
        ENDED,
        SUSPENDED,
        PENDING
    }

    /**
     * Check if the relationship is currently active
     */
    public boolean isActive() {
        return status == RelationshipStatus.ACTIVE;
    }

    /**
     * Check if the relationship has ended
     */
    public boolean isEnded() {
        return status == RelationshipStatus.ENDED;
    }

    /**
     * Check if the relationship is pending approval
     */
    public boolean isPending() {
        return status == RelationshipStatus.PENDING;
    }
}
