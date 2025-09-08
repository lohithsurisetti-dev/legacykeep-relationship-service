package com.legacykeep.relationship.service;

import com.legacykeep.relationship.dto.event.RelationshipEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing relationship events to Kafka.
 * 
 * Handles asynchronous event publishing with proper error handling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // Kafka topics for relationship events
    private static final String RELATIONSHIP_EVENTS_TOPIC = "relationship-events";
    private static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";

    /**
     * Publish a relationship event to Kafka.
     * 
     * @param event The relationship event to publish
     */
    public void publishRelationshipEvent(RelationshipEvent event) {
        try {
            log.info("Publishing relationship event: {} for relationship ID: {}", 
                    event.getEventType(), event.getRelationshipId());
            
            // Publish to relationship events topic
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(RELATIONSHIP_EVENTS_TOPIC, event.getEventId(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published relationship event: {} with offset: {}", 
                            event.getEventId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish relationship event: {}", event.getEventId(), ex);
                }
            });
            
            // Also publish to notification events topic for notification service
            publishNotificationEvent(event);
            
        } catch (Exception e) {
            log.error("Error publishing relationship event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to publish relationship event", e);
        }
    }

    /**
     * Publish event to notification service topic.
     * 
     * @param event The relationship event to publish
     */
    private void publishNotificationEvent(RelationshipEvent event) {
        try {
            log.debug("Publishing notification event for relationship event: {}", event.getEventId());
            
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(NOTIFICATION_EVENTS_TOPIC, event.getEventId(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Successfully published notification event: {} with offset: {}", 
                            event.getEventId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish notification event: {}", event.getEventId(), ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error publishing notification event: {}", event.getEventId(), e);
            // Don't throw exception for notification events to avoid breaking the main flow
        }
    }

    /**
     * Generate a unique event ID.
     * 
     * @return A unique event ID
     */
    public String generateEventId() {
        return "rel-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}

