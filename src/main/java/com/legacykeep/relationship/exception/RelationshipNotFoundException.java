package com.legacykeep.relationship.exception;

/**
 * Exception thrown when a relationship is not found.
 */
public class RelationshipNotFoundException extends RelationshipException {

    public RelationshipNotFoundException(String message) {
        super(message);
    }

    public RelationshipNotFoundException(Long relationshipId) {
        super("Relationship not found with ID: " + relationshipId);
    }
}
