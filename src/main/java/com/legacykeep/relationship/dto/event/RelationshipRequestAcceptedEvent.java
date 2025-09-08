package com.legacykeep.relationship.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event published when a relationship request is accepted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RelationshipRequestAcceptedEvent extends RelationshipEvent {
    
    /**
     * ID of the relationship type
     */
    private Long relationshipTypeId;
    
    /**
     * Name of the relationship type
     */
    private String relationshipTypeName;
    
    /**
     * Response message from the acceptor
     */
    private String responseMessage;
    
    /**
     * Context ID (e.g., family circle ID)
     */
    private Long contextId;
    
    /**
     * Static factory method for creating the event
     */
    public static RelationshipRequestAcceptedEvent create(
            String eventId,
            Long relationshipId,
            Long acceptorUserId,
            Long requesterUserId,
            Long relationshipTypeId,
            String relationshipTypeName,
            String responseMessage,
            Long contextId) {
        
        RelationshipRequestAcceptedEvent event = new RelationshipRequestAcceptedEvent();
        event.setEventId(eventId);
        event.setEventType("RELATIONSHIP_REQUEST_ACCEPTED");
        event.setTimestamp(java.time.LocalDateTime.now());
        event.setRelationshipId(relationshipId);
        event.setInitiatorUserId(acceptorUserId);
        event.setTargetUserId(requesterUserId);
        event.setRelationshipStatus(com.legacykeep.relationship.enums.RelationshipStatus.ACTIVE);
        event.setRelationshipTypeId(relationshipTypeId);
        event.setRelationshipTypeName(relationshipTypeName);
        event.setResponseMessage(responseMessage);
        event.setContextId(contextId);
        event.setSourceService("relationship-service");
        event.setEventVersion("1.0");
        return event;
    }
}
