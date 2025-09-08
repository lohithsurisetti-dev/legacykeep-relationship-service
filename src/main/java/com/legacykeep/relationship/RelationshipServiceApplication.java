package com.legacykeep.relationship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the Relationship Service.
 * 
 * This microservice manages relationships between users in the LegacyKeep system,
 * providing ultra-flexible relationship management capabilities.
 * 
 * Features:
 * - Multi-dimensional relationship support
 * - Bidirectional relationship types
 * - Context-aware relationships
 * - Relationship lifecycle management
 * - Event-driven architecture
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class RelationshipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelationshipServiceApplication.class, args);
    }
}

