package com.legacykeep.relationship.repository;

import com.legacykeep.relationship.entity.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RelationshipType entity operations
 */
@Repository
public interface RelationshipTypeRepository extends JpaRepository<RelationshipType, Long> {

    /**
     * Find relationship type by name
     */
    Optional<RelationshipType> findByName(String name);

    /**
     * Find all relationship types by category
     */
    List<RelationshipType> findByCategory(RelationshipType.RelationshipCategory category);

    /**
     * Find all bidirectional relationship types
     */
    List<RelationshipType> findByBidirectionalTrue();

    /**
     * Find all non-bidirectional relationship types
     */
    List<RelationshipType> findByBidirectionalFalse();

    /**
     * Find relationship types by category and bidirectional flag
     */
    List<RelationshipType> findByCategoryAndBidirectional(
            RelationshipType.RelationshipCategory category, 
            Boolean bidirectional
    );

    /**
     * Find relationship types that have a reverse type
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE rt.reverseType IS NOT NULL")
    List<RelationshipType> findTypesWithReverse();

    /**
     * Find relationship types that are reverse types of the given type
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE rt.reverseType.id = :typeId")
    List<RelationshipType> findReverseTypes(@Param("typeId") Long typeId);

    /**
     * Check if a relationship type name exists (excluding the given ID)
     */
    @Query("SELECT COUNT(rt) > 0 FROM RelationshipType rt WHERE rt.name = :name AND rt.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);

    /**
     * Find relationship types by name containing (case insensitive)
     */
    @Query("SELECT rt FROM RelationshipType rt WHERE LOWER(rt.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<RelationshipType> findByNameContainingIgnoreCase(@Param("name") String name);
}
