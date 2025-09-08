package com.legacykeep.relationship.dto.event;

import com.legacykeep.relationship.enums.RelationshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Base event class for relationship-related events.
 * 
 * All relationship events extend this base class for consistency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipEvent {
    
    /**
     * Event ID for tracking and deduplication
     */
    private String eventId;
    
    /**
     * Event type (e.g., RELATIONSHIP_REQUEST_SENT, RELATIONSHIP_ACCEPTED)
     */
    private String eventType;
    
    /**
     * Timestamp when the event occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * ID of the relationship that triggered this event
     */
    private Long relationshipId;
    
    /**
     * ID of the user who initiated the action
     */
    private Long initiatorUserId;
    
    /**
     * ID of the user who is the target of the action
     */
    private Long targetUserId;
    
    /**
     * Current status of the relationship
     */
    private RelationshipStatus relationshipStatus;
    
    /**
     * Additional metadata for the event
     */
    private String metadata;
    
    /**
     * Service that generated this event
     */
    private String sourceService;
    
    /**
     * Version of the event schema
     */
    private String eventVersion;
}

