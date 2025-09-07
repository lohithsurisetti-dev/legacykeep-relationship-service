package com.legacykeep.relationship.exception;

/**
 * Exception thrown when a relationship type is not found.
 */
public class RelationshipTypeNotFoundException extends RelationshipException {

    public RelationshipTypeNotFoundException(String message) {
        super(message);
    }

    public RelationshipTypeNotFoundException(Long relationshipTypeId) {
        super("Relationship type not found with ID: " + relationshipTypeId);
    }

    public RelationshipTypeNotFoundException(String name, String message) {
        super("Relationship type not found with name: " + name + ". " + message);
    }
}
