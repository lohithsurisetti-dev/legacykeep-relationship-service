# Relationship Service Security Guide

## Overview
This document outlines the security measures, best practices, and compliance requirements for the Relationship Service.

## Security Architecture

### Authentication
- **JWT Tokens**: JSON Web Tokens for stateless authentication
- **Shared Secret**: Common JWT secret across all LegacyKeep services
- **Token Validation**: Automatic validation on all protected endpoints
- **Token Expiration**: Configurable token expiration times

### Authorization
- **Role-Based Access Control (RBAC)**: Admin, User roles
- **Resource-Based Access**: Users can only access their own relationships
- **Context-Based Access**: Access based on family circle membership
- **API-Level Security**: Endpoint-level authorization

### Data Protection
- **Input Validation**: Comprehensive validation on all inputs
- **SQL Injection Prevention**: JPA/Hibernate protection
- **XSS Protection**: Input sanitization and output encoding
- **CSRF Protection**: Cross-Site Request Forgery protection

## Security Configuration

### JWT Configuration
```properties
# JWT Settings
relationship.jwt.secret=legacykeep-jwt-secret-key-change-in-production-512-bits-minimum-required-for-hs512-algorithm
relationship.jwt.issuer=LegacyKeep
relationship.jwt.audience=LegacyKeep-Relationships
relationship.jwt.algorithm=HS256
relationship.jwt.access-token-expiration=3600000
relationship.jwt.refresh-token-expiration=86400000
```

### Security Headers
```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public FilterRegistrationBean<HeaderFilter> securityHeadersFilter() {
        FilterRegistrationBean<HeaderFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HeaderFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
    
    public static class HeaderFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Security headers
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            httpResponse.setHeader("Content-Security-Policy", "default-src 'self'");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            chain.doFilter(request, response);
        }
    }
}
```

## Input Validation

### Request Validation
```java
@RestController
@Validated
public class RelationshipController {
    
    @PostMapping("/relationships")
    public ResponseEntity<ApiResponse<RelationshipResponse>> createRelationship(
            @Valid @RequestBody CreateRelationshipRequest request) {
        // Validation handled by @Valid annotation
        return relationshipService.createRelationship(request);
    }
}
```

### DTO Validation
```java
public class CreateRelationshipRequest {
    
    @NotNull(message = "User1 ID is required")
    @Positive(message = "User1 ID must be positive")
    private Long user1Id;
    
    @NotNull(message = "User2 ID is required")
    @Positive(message = "User2 ID must be positive")
    private Long user2Id;
    
    @NotNull(message = "Relationship type ID is required")
    @Positive(message = "Relationship type ID must be positive")
    private Long relationshipTypeId;
    
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.,!?()]*$", message = "Invalid characters in metadata")
    @Size(max = 1000, message = "Metadata too long")
    private String metadata;
    
    // Getters and setters
}
```

### Custom Validation
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RelationshipValidator.class)
public @interface ValidRelationship {
    String message() default "Invalid relationship";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class RelationshipValidator implements ConstraintValidator<ValidRelationship, CreateRelationshipRequest> {
    
    @Override
    public boolean isValid(CreateRelationshipRequest request, ConstraintValidatorContext context) {
        // Custom validation logic
        if (request.getUser1Id().equals(request.getUser2Id())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Users cannot have relationship with themselves")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}
```

## Database Security

### Connection Security
```properties
# Encrypted database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/relationship_db?ssl=true&sslmode=require
spring.datasource.username=legacykeep
spring.datasource.password=${DB_PASSWORD}

# Connection pool security
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.validation-timeout=3000
spring.datasource.hikari.leak-detection-threshold=60000
```

### SQL Injection Prevention
```java
@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {
    
    // Use parameterized queries
    @Query("SELECT ur FROM UserRelationship ur WHERE ur.user1Id = :userId OR ur.user2Id = :userId")
    List<UserRelationship> findByUserId(@Param("userId") Long userId);
    
    // Native queries with parameters
    @Query(value = "SELECT * FROM user_relationships WHERE user1_id = :userId AND status = :status", 
           nativeQuery = true)
    List<UserRelationship> findByUserIdAndStatus(@Param("userId") Long userId, 
                                                @Param("status") String status);
}
```

### Data Encryption
```java
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    
    @Value("${relationship.encryption.secret-key}")
    private String secretKey;
    
    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (plainText == null) return null;
        return encrypt(plainText);
    }
    
    @Override
    public String convertToEntityAttribute(String encryptedText) {
        if (encryptedText == null) return null;
        return decrypt(encryptedText);
    }
    
    private String encrypt(String plainText) {
        // AES encryption implementation
    }
    
    private String decrypt(String encryptedText) {
        // AES decryption implementation
    }
}
```

## API Security

### Rate Limiting
```java
@Configuration
public class RateLimitingConfig {
    
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
    
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(1000.0); // 1000 requests per second
    }
}

@Component
public class RateLimitingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientId = getClientId(httpRequest);
        
        if (!rateLimiter.tryAcquire()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        
        chain.doFilter(request, response);
    }
}
```

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("https://*.legacykeep.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## Logging and Monitoring

### Security Event Logging
```java
@Component
public class SecurityEventLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityEventLogger.class);
    
    public void logAuthenticationSuccess(String userId, String ipAddress) {
        logger.info("Authentication successful for user: {} from IP: {}", userId, ipAddress);
    }
    
    public void logAuthenticationFailure(String email, String ipAddress, String reason) {
        logger.warn("Authentication failed for email: {} from IP: {} - Reason: {}", email, ipAddress, reason);
    }
    
    public void logAuthorizationFailure(String userId, String resource, String action) {
        logger.warn("Authorization failed for user: {} accessing resource: {} with action: {}", 
                   userId, resource, action);
    }
    
    public void logSuspiciousActivity(String userId, String activity, String details) {
        logger.error("Suspicious activity detected for user: {} - Activity: {} - Details: {}", 
                    userId, activity, details);
    }
}
```

### Audit Trail
```java
@Entity
@Table(name = "security_audit_logs")
public class SecurityAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(nullable = false)
    private String resource;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column(nullable = false)
    private String userAgent;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column
    private String details;
    
    // Getters and setters
}
```

## Compliance

### GDPR Compliance
```java
@Service
public class GdprComplianceService {
    
    public void handleDataDeletionRequest(Long userId) {
        // Anonymize user relationships
        anonymizeUserRelationships(userId);
        
        // Log data deletion
        logDataDeletion(userId);
        
        // Notify other services
        publishDataDeletionEvent(userId);
    }
    
    public void handleDataExportRequest(Long userId) {
        // Export user data
        UserDataExport export = createUserDataExport(userId);
        
        // Secure delivery
        deliverDataExport(export);
    }
    
    private void anonymizeUserRelationships(Long userId) {
        // Replace user ID with anonymized identifier
        // Keep relationship structure but remove personal data
    }
}
```

### Data Retention
```java
@Component
public class DataRetentionService {
    
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldData() {
        // Remove old relationship history
        LocalDate cutoffDate = LocalDate.now().minusYears(7);
        relationshipRepository.deleteOldRelationships(cutoffDate);
        
        // Archive old audit logs
        LocalDate archiveDate = LocalDate.now().minusYears(2);
        auditLogRepository.archiveOldLogs(archiveDate);
    }
}
```

## Vulnerability Management

### Dependency Scanning
```xml
<!-- OWASP Dependency Check Plugin -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.4.0</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Security Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class SecurityIntegrationTest {
    
    @Test
    void testUnauthorizedAccess() {
        // Test unauthorized access to protected endpoints
        mockMvc.perform(get("/relationships"))
               .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testSqlInjection() {
        // Test SQL injection prevention
        String maliciousInput = "'; DROP TABLE user_relationships; --";
        
        mockMvc.perform(post("/relationships")
               .contentType(MediaType.APPLICATION_JSON)
               .content(createRelationshipRequest(maliciousInput)))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void testXssPrevention() {
        // Test XSS prevention
        String xssPayload = "<script>alert('XSS')</script>";
        
        mockMvc.perform(post("/relationships")
               .contentType(MediaType.APPLICATION_JSON)
               .content(createRelationshipRequest(xssPayload)))
               .andExpect(status().isBadRequest());
    }
}
```

## Incident Response

### Security Incident Response Plan
1. **Detection**: Automated monitoring and alerting
2. **Assessment**: Evaluate severity and impact
3. **Containment**: Isolate affected systems
4. **Investigation**: Analyze root cause
5. **Recovery**: Restore normal operations
6. **Lessons Learned**: Document and improve

### Incident Response Team
- **Security Lead**: Overall incident coordination
- **Development Team**: Technical investigation
- **Operations Team**: System recovery
- **Legal Team**: Compliance and notification

### Communication Plan
- **Internal**: Immediate notification to incident response team
- **External**: Customer notification if data breach
- **Regulatory**: GDPR notification within 72 hours
- **Public**: Press release if necessary

## Security Best Practices

### Development
- **Secure Coding**: Follow OWASP guidelines
- **Code Reviews**: Security-focused code reviews
- **Static Analysis**: Automated security scanning
- **Dependency Management**: Regular dependency updates

### Operations
- **Access Control**: Principle of least privilege
- **Network Security**: Firewall and network segmentation
- **Monitoring**: Continuous security monitoring
- **Backup Security**: Encrypted backups

### Training
- **Security Awareness**: Regular security training
- **Incident Response**: Tabletop exercises
- **Secure Development**: Developer security training
- **Compliance**: GDPR and security compliance training

## Security Checklist

### Pre-Deployment
- [ ] Security code review completed
- [ ] Vulnerability scan passed
- [ ] Dependency check completed
- [ ] Security tests passed
- [ ] Configuration review completed
- [ ] Access controls verified

### Post-Deployment
- [ ] Security monitoring enabled
- [ ] Log aggregation configured
- [ ] Alerting rules set up
- [ ] Backup procedures tested
- [ ] Incident response plan updated
- [ ] Security documentation updated

### Regular Maintenance
- [ ] Security patches applied
- [ ] Dependency updates completed
- [ ] Access reviews conducted
- [ ] Security logs reviewed
- [ ] Penetration testing scheduled
- [ ] Security training completed

## Contact Information

### Security Team
- **Email**: security@legacykeep.com
- **Phone**: +1-555-SECURITY
- **Emergency**: 24/7 security hotline

### Reporting Security Issues
- **Vulnerability Disclosure**: security@legacykeep.com
- **Bug Bounty**: bugbounty@legacykeep.com
- **General Security**: security@legacykeep.com

### External Resources
- **OWASP**: https://owasp.org/
- **NIST Cybersecurity Framework**: https://www.nist.gov/cyberframework
- **GDPR Guidelines**: https://gdpr.eu/
- **Security Advisories**: https://www.cisa.gov/known-exploited-vulnerabilities-catalog
