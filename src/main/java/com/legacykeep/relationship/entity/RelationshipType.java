package com.legacykeep.relationship.entity;

import com.legacykeep.relationship.enums.RelationshipCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a relationship type in the system.
 * Defines all available relationship types including predefined and custom types.
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

    @Column(name = "name", nullable = false, unique = true, length = 100)
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
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "reverseType", fetch = FetchType.LAZY)
    private List<RelationshipType> reverseTypes;

    @OneToMany(mappedBy = "relationshipType", fetch = FetchType.LAZY)
    private List<UserRelationship> userRelationships;


    /**
     * Check if this relationship type has a reverse type
     */
    public boolean hasReverseType() {
        return reverseType != null;
    }

    /**
     * Check if this is a bidirectional relationship
     */
    public boolean isBidirectional() {
        return Boolean.TRUE.equals(bidirectional);
    }
}
