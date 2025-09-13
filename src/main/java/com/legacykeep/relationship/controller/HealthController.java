package com.legacykeep.relationship.controller;

import com.legacykeep.relationship.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for the Relationship Service
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final DataSource dataSource;

    @Value("${spring.application.name:relationship-service}")
    private String serviceName;

    @Value("${spring.application.version:1.0.0}")
    private String serviceVersion;

    /**
     * Basic health check endpoint
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.debug("Health check requested");
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", serviceName);
        healthData.put("version", serviceVersion);
        healthData.put("timestamp", LocalDateTime.now());
        
        // Check database connectivity
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                healthData.put("database", "Connected");
            } else {
                healthData.put("database", "Disconnected");
                healthData.put("status", "DOWN");
            }
        } catch (Exception e) {
            log.error("Database health check failed", e);
            healthData.put("database", "Error: " + e.getMessage());
            healthData.put("status", "DOWN");
        }
        
        boolean isHealthy = "UP".equals(healthData.get("status"));
        String message = isHealthy ? "Service is healthy" : "Service is unhealthy";
        
        return ResponseEntity.status(isHealthy ? 200 : 503)
                .body(ApiResponse.success(healthData, message));
    }

    /**
     * Detailed health check endpoint
     */
    @GetMapping("/detailed")
    public ResponseEntity<ApiResponse<Map<String, Object>>> detailedHealth() {
        log.debug("Detailed health check requested");
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", serviceName);
        healthData.put("version", serviceVersion);
        healthData.put("timestamp", LocalDateTime.now());
        
        // Database health
        Map<String, Object> databaseHealth = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                databaseHealth.put("status", "UP");
                databaseHealth.put("connection", "Valid");
                databaseHealth.put("url", connection.getMetaData().getURL());
            } else {
                databaseHealth.put("status", "DOWN");
                databaseHealth.put("connection", "Invalid");
                healthData.put("status", "DOWN");
            }
        } catch (Exception e) {
            log.error("Database detailed health check failed", e);
            databaseHealth.put("status", "DOWN");
            databaseHealth.put("error", e.getMessage());
            healthData.put("status", "DOWN");
        }
        healthData.put("database", databaseHealth);
        
        // Memory health
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memoryHealth = new HashMap<>();
        memoryHealth.put("totalMemory", runtime.totalMemory());
        memoryHealth.put("freeMemory", runtime.freeMemory());
        memoryHealth.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        memoryHealth.put("maxMemory", runtime.maxMemory());
        healthData.put("memory", memoryHealth);
        
        boolean isHealthy = "UP".equals(healthData.get("status"));
        String message = isHealthy ? "Service is healthy with detailed information" : "Service is unhealthy";
        
        return ResponseEntity.status(isHealthy ? 200 : 503)
                .body(ApiResponse.success(healthData, message));
    }
}
