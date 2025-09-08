package com.legacykeep.relationship.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event published when a relationship request is rejected.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RelationshipRequestRejectedEvent extends RelationshipEvent {
    
    /**
     * ID of the relationship type
     */
    private Long relationshipTypeId;
    
    /**
     * Name of the relationship type
     */
    private String relationshipTypeName;
    
    /**
     * Response message from the rejector
     */
    private String responseMessage;
    
    /**
     * Context ID (e.g., family circle ID)
     */
    private Long contextId;
    
    /**
     * Static factory method for creating the event
     */
    public static RelationshipRequestRejectedEvent create(
            String eventId,
            Long relationshipId,
            Long rejectorUserId,
            Long requesterUserId,
            Long relationshipTypeId,
            String relationshipTypeName,
            String responseMessage,
            Long contextId) {
        
        RelationshipRequestRejectedEvent event = new RelationshipRequestRejectedEvent();
        event.setEventId(eventId);
        event.setEventType("RELATIONSHIP_REQUEST_REJECTED");
        event.setTimestamp(java.time.LocalDateTime.now());
        event.setRelationshipId(relationshipId);
        event.setInitiatorUserId(rejectorUserId);
        event.setTargetUserId(requesterUserId);
        event.setRelationshipStatus(com.legacykeep.relationship.enums.RelationshipStatus.ENDED);
        event.setRelationshipTypeId(relationshipTypeId);
        event.setRelationshipTypeName(relationshipTypeName);
        event.setResponseMessage(responseMessage);
        event.setContextId(contextId);
        event.setSourceService("relationship-service");
        event.setEventVersion("1.0");
        return event;
    }
}
