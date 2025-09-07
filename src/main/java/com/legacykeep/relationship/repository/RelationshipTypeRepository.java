package com.legacykeep.relationship.repository;

import com.legacykeep.relationship.entity.RelationshipType;
import com.legacykeep.relationship.enums.RelationshipCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RelationshipType entity.
 * Provides data access methods for relationship types.
 */
@Repository
public interface RelationshipTypeRepository extends JpaRepository<RelationshipType, Long> {

    /**
     * Find relationship type by name
     */
    Optional<RelationshipType> findByName(String name);

    /**
     * Find relationship types by category
     */
    List<RelationshipType> findByCategory(RelationshipCategory category);

    /**
     * Find relationship types by category with pagination
     */
    Page<RelationshipType> findByCategory(RelationshipCategory category, Pageable pageable);

    /**
     * Find bidirectional relationship types
     */
    List<RelationshipType> findByBidirectionalTrue();

    /**
     * Find non-bidirectional relationship types
     */
    List<RelationshipType> findByBidirectionalFalse();

    /**
     * Find relationship types that have a reverse type
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE rt.reverseType IS NOT NULL")
    List<RelationshipType> findWithReverseType();

    /**
     * Find relationship types that don't have a reverse type
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE rt.reverseType IS NULL")
    List<RelationshipType> findWithoutReverseType();

    /**
     * Find relationship types by name containing (case-insensitive)
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE LOWER(rt.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<RelationshipType> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find relationship types by category and bidirectional flag
     */
    List<RelationshipType> findByCategoryAndBidirectional(
            RelationshipCategory category, 
            Boolean bidirectional
    );

    /**
     * Check if relationship type exists by name
     */
    boolean existsByName(String name);

    /**
     * Count relationship types by category
     */
    long countByCategory(RelationshipCategory category);

    /**
     * Count bidirectional relationship types
     */
    long countByBidirectionalTrue();

    /**
     * Count non-bidirectional relationship types
     */
    long countByBidirectionalFalse();

    /**
     * Find all relationship types ordered by category and name
     */
    @Query("SELECT rt FROM RelationshipType rt ORDER BY rt.category, rt.name")
    List<RelationshipType> findAllOrderedByCategoryAndName();

    /**
     * Find relationship types with metadata containing specific key
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE rt.metadata LIKE CONCAT('%', :key, '%')")
    List<RelationshipType> findByMetadataContaining(@Param("key") String key);
}
