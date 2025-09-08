package com.legacykeep.relationship.controller;

import com.legacykeep.relationship.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for the Relationship Service.
 * 
 * Provides basic health check endpoints for monitoring and testing.
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.debug("Health check requested");
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("service", "relationship-service");
        healthData.put("status", "UP");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success(healthData, "Service is healthy"));
    }

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        log.debug("Ping requested");
        return ResponseEntity.ok(ApiResponse.success("pong", "Service is responding"));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        log.debug("Info requested");
        
        Map<String, Object> info = new HashMap<>();
        info.put("service", "LegacyKeep Relationship Service");
        info.put("description", "Ultra-flexible relationship management microservice");
        info.put("version", "1.0.0");
        info.put("buildTime", LocalDateTime.now());
        info.put("features", new String[]{
            "Multi-dimensional relationships",
            "Bidirectional relationship types", 
            "Context-aware relationships",
            "Relationship lifecycle management",
            "JWT authentication",
            "RESTful API"
        });
        
        return ResponseEntity.ok(ApiResponse.success(info, "Service information retrieved"));
    }
}

