package com.legacykeep.relationship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the Relationship Service
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class RelationshipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelationshipServiceApplication.class, args);
    }
}
