package com.legacykeep.relationship.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties for the Relationship Service.
 * 
 * Manages JWT-related configuration including secret key and token expiration.
 * Follows the same JWT configuration pattern as other LegacyKeep services.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {

    /**
     * JWT secret key for token signing and validation.
     * Should be the same across all LegacyKeep services for inter-service communication.
     */
    private String secret = "legacykeep-secret-key-2024-ultra-secure-jwt-token-for-microservices";

    /**
     * JWT token expiration time in milliseconds.
     * Default: 15 minutes (900000 ms) - matches Auth Service standard
     */
    private long expiration = 900000;

    /**
     * JWT refresh token expiration time in milliseconds.
     * Default: 7 days (604800000 ms)
     */
    private long refreshExpiration = 604800000;

    /**
     * JWT token prefix for Authorization header.
     */
    private String tokenPrefix = "Bearer ";

    /**
     * JWT token header name.
     */
    private String tokenHeader = "Authorization";

    /**
     * JWT issuer claim.
     */
    private String issuer = "legacykeep-relationship-service";

    /**
     * JWT audience claim.
     */
    private String audience = "legacykeep-users";
}

