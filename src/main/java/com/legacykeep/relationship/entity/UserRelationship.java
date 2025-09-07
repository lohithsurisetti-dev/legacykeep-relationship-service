package com.legacykeep.relationship.entity;

import com.legacykeep.relationship.enums.RelationshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a relationship between two users.
 * Stores actual relationships with their properties and status.
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
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


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

    /**
     * Check if the relationship is suspended
     */
    public boolean isSuspended() {
        return status == RelationshipStatus.SUSPENDED;
    }

    /**
     * Check if the relationship has a defined end date
     */
    public boolean hasEndDate() {
        return endDate != null;
    }

    /**
     * Check if the relationship has a defined start date
     */
    public boolean hasStartDate() {
        return startDate != null;
    }

    /**
     * Check if the relationship is currently valid (active and not ended)
     */
    public boolean isValid() {
        return isActive() && (!hasEndDate() || endDate.isAfter(LocalDate.now()));
    }

    /**
     * Get the relationship type name
     */
    public String getRelationshipTypeName() {
        return relationshipType != null ? relationshipType.getName() : null;
    }

    /**
     * Get the relationship category
     */
    public com.legacykeep.relationship.enums.RelationshipCategory getRelationshipCategory() {
        return relationshipType != null ? relationshipType.getCategory() : null;
    }
}
