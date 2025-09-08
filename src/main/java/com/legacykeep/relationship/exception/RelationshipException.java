package com.legacykeep.relationship.exception;

/**
 * Base exception for relationship-related errors.
 */
public class RelationshipException extends RuntimeException {

    public RelationshipException(String message) {
        super(message);
    }

    public RelationshipException(String message, Throwable cause) {
        super(message, cause);
    }
}

