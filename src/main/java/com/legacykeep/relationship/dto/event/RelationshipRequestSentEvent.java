package com.legacykeep.relationship.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event published when a relationship request is sent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RelationshipRequestSentEvent extends RelationshipEvent {
    
    /**
     * ID of the relationship type being requested
     */
    private Long relationshipTypeId;
    
    /**
     * Name of the relationship type
     */
    private String relationshipTypeName;
    
    /**
     * Message from the requester
     */
    private String requestMessage;
    
    /**
     * Context ID (e.g., family circle ID)
     */
    private Long contextId;
    
    /**
     * Static factory method for creating the event
     */
    public static RelationshipRequestSentEvent create(
            String eventId,
            Long relationshipId,
            Long requesterUserId,
            Long recipientUserId,
            Long relationshipTypeId,
            String relationshipTypeName,
            String requestMessage,
            Long contextId) {
        
        RelationshipRequestSentEvent event = new RelationshipRequestSentEvent();
        event.setEventId(eventId);
        event.setEventType("RELATIONSHIP_REQUEST_SENT");
        event.setTimestamp(java.time.LocalDateTime.now());
        event.setRelationshipId(relationshipId);
        event.setInitiatorUserId(requesterUserId);
        event.setTargetUserId(recipientUserId);
        event.setRelationshipStatus(com.legacykeep.relationship.enums.RelationshipStatus.PENDING);
        event.setRelationshipTypeId(relationshipTypeId);
        event.setRelationshipTypeName(relationshipTypeName);
        event.setRequestMessage(requestMessage);
        event.setContextId(contextId);
        event.setSourceService("relationship-service");
        event.setEventVersion("1.0");
        return event;
    }
}
