# Relationship Service Testing Guide

## Overview
This document outlines the testing strategy, test cases, and testing procedures for the Relationship Service.

## Testing Strategy

### Testing Pyramid
- **Unit Tests**: 70% - Fast, isolated tests for individual components
- **Integration Tests**: 20% - Tests for component interactions
- **End-to-End Tests**: 10% - Full system tests

### Testing Levels
1. **Unit Testing**: Individual classes and methods
2. **Integration Testing**: Service interactions and database
3. **Contract Testing**: API contract validation
4. **Performance Testing**: Load and stress testing
5. **Security Testing**: Security vulnerability testing

## Unit Testing

### Entity Testing
```java
@ExtendWith(MockitoExtension.class)
class RelationshipTypeTest {
    
    @Test
    void testRelationshipTypeCreation() {
        // Given
        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setName("Father");
        relationshipType.setCategory("FAMILY");
        relationshipType.setBidirectional(true);
        
        // When & Then
        assertThat(relationshipType.getName()).isEqualTo("Father");
        assertThat(relationshipType.getCategory()).isEqualTo("FAMILY");
        assertThat(relationshipType.isBidirectional()).isTrue();
    }
    
    @Test
    void testRelationshipTypeValidation() {
        // Given
        RelationshipType relationshipType = new RelationshipType();
        
        // When
        Set<ConstraintViolation<RelationshipType>> violations = validator.validate(relationshipType);
        
        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .contains("Name is required", "Category is required");
    }
}
```

### Service Testing
```java
@ExtendWith(MockitoExtension.class)
class RelationshipServiceTest {
    
    @Mock
    private UserRelationshipRepository relationshipRepository;
    
    @Mock
    private RelationshipTypeRepository typeRepository;
    
    @Mock
    private RelationshipValidationService validationService;
    
    @InjectMocks
    private RelationshipService relationshipService;
    
    @Test
    void testCreateRelationship_Success() {
        // Given
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(1L)
            .build();
        
        RelationshipType type = new RelationshipType();
        type.setName("Father");
        type.setBidirectional(true);
        
        when(typeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(validationService.validateRelationship(any())).thenReturn(ValidationResult.success());
        when(relationshipRepository.save(any())).thenReturn(new UserRelationship());
        
        // When
        RelationshipResponse response = relationshipService.createRelationship(request);
        
        // Then
        assertThat(response).isNotNull();
        verify(relationshipRepository).save(any(UserRelationship.class));
    }
    
    @Test
    void testCreateRelationship_ValidationFailure() {
        // Given
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(1L) // Same user
            .relationshipTypeId(1L)
            .build();
        
        when(validationService.validateRelationship(any()))
            .thenReturn(ValidationResult.failure("Users cannot have relationship with themselves"));
        
        // When & Then
        assertThatThrownBy(() -> relationshipService.createRelationship(request))
            .isInstanceOf(RelationshipValidationException.class)
            .hasMessage("Users cannot have relationship with themselves");
    }
}
```

### Repository Testing
```java
@DataJpaTest
class UserRelationshipRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRelationshipRepository relationshipRepository;
    
    @Test
    void testFindByUserId() {
        // Given
        UserRelationship relationship = new UserRelationship();
        relationship.setUser1Id(1L);
        relationship.setUser2Id(2L);
        relationship.setStatus(RelationshipStatus.ACTIVE);
        entityManager.persistAndFlush(relationship);
        
        // When
        List<UserRelationship> relationships = relationshipRepository.findByUserId(1L);
        
        // Then
        assertThat(relationships).hasSize(1);
        assertThat(relationships.get(0).getUser1Id()).isEqualTo(1L);
    }
    
    @Test
    void testFindActiveRelationships() {
        // Given
        UserRelationship activeRelationship = new UserRelationship();
        activeRelationship.setUser1Id(1L);
        activeRelationship.setStatus(RelationshipStatus.ACTIVE);
        entityManager.persistAndFlush(activeRelationship);
        
        UserRelationship endedRelationship = new UserRelationship();
        endedRelationship.setUser1Id(1L);
        endedRelationship.setStatus(RelationshipStatus.ENDED);
        entityManager.persistAndFlush(endedRelationship);
        
        // When
        List<UserRelationship> activeRelationships = relationshipRepository.findActiveRelationships(1L);
        
        // Then
        assertThat(activeRelationships).hasSize(1);
        assertThat(activeRelationships.get(0).getStatus()).isEqualTo(RelationshipStatus.ACTIVE);
    }
}
```

## Integration Testing

### Controller Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
class RelationshipControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void testCreateRelationship_Integration() throws Exception {
        // Given
        RelationshipType type = new RelationshipType();
        type.setName("Father");
        type.setCategory("FAMILY");
        entityManager.persistAndFlush(type);
        
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(type.getId())
            .build();
        
        // When
        MvcResult result = mockMvc.perform(post("/relationships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // Then
        String responseContent = result.getResponse().getContentAsString();
        RelationshipResponse response = objectMapper.readValue(responseContent, RelationshipResponse.class);
        assertThat(response.getUser1Id()).isEqualTo(1L);
        assertThat(response.getUser2Id()).isEqualTo(2L);
    }
    
    @Test
    void testGetUserRelationships_Integration() throws Exception {
        // Given
        RelationshipType type = new RelationshipType();
        type.setName("Father");
        entityManager.persistAndFlush(type);
        
        UserRelationship relationship = new UserRelationship();
        relationship.setUser1Id(1L);
        relationship.setUser2Id(2L);
        relationship.setRelationshipTypeId(type.getId());
        relationship.setStatus(RelationshipStatus.ACTIVE);
        entityManager.persistAndFlush(relationship);
        
        // When
        mockMvc.perform(get("/relationships/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.relationships").isArray())
                .andExpect(jsonPath("$.data.relationships[0].user1Id").value(1))
                .andExpect(jsonPath("$.data.relationships[0].status").value("ACTIVE"));
    }
}
```

### Database Integration Testing
```java
@SpringBootTest
@Transactional
@Sql(scripts = "/test-data/relationship-types.sql")
class RelationshipServiceDatabaseIntegrationTest {
    
    @Autowired
    private RelationshipService relationshipService;
    
    @Autowired
    private UserRelationshipRepository relationshipRepository;
    
    @Test
    void testCreateRelationship_DatabaseIntegration() {
        // Given
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(1L) // Father type from test data
            .build();
        
        // When
        RelationshipResponse response = relationshipService.createRelationship(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUser1Id()).isEqualTo(1L);
        assertThat(response.getUser2Id()).isEqualTo(2L);
        
        // Verify database state
        List<UserRelationship> relationships = relationshipRepository.findAll();
        assertThat(relationships).hasSize(1);
        assertThat(relationships.get(0).getUser1Id()).isEqualTo(1L);
    }
    
    @Test
    void testBidirectionalRelationshipCreation() {
        // Given
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(1L) // Father type (bidirectional)
            .build();
        
        // When
        relationshipService.createRelationship(request);
        
        // Then
        List<UserRelationship> relationships = relationshipRepository.findAll();
        assertThat(relationships).hasSize(2); // Original + reverse relationship
        
        // Verify reverse relationship
        UserRelationship reverseRelationship = relationships.stream()
            .filter(r -> r.getUser1Id().equals(2L) && r.getUser2Id().equals(1L))
            .findFirst()
            .orElseThrow();
        assertThat(reverseRelationship.getRelationshipTypeId()).isEqualTo(2L); // Son type
    }
}
```

## Contract Testing

### API Contract Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class RelationshipServiceContractTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testRelationshipCreationContract() throws Exception {
        // Given
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(1L)
            .build();
        
        // When
        mockMvc.perform(post("/relationships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.user1Id").value(1))
                .andExpect(jsonPath("$.data.user2Id").value(2))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void testErrorResponseContract() throws Exception {
        // Given
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(1L) // Invalid: same user
            .relationshipTypeId(1L)
            .build();
        
        // When
        mockMvc.perform(post("/relationships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error.code").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
```

## Performance Testing

### Load Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class RelationshipServicePerformanceTest {
    
    @Autowired
    private RelationshipService relationshipService;
    
    @Test
    void testCreateRelationship_Performance() {
        // Given
        int numberOfRelationships = 1000;
        List<CreateRelationshipRequest> requests = createTestRequests(numberOfRelationships);
        
        // When
        long startTime = System.currentTimeMillis();
        
        for (CreateRelationshipRequest request : requests) {
            relationshipService.createRelationship(request);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        assertThat(duration).isLessThan(5000); // Should complete within 5 seconds
        double averageTime = (double) duration / numberOfRelationships;
        assertThat(averageTime).isLessThan(5); // Average time per request < 5ms
    }
    
    @Test
    void testGetUserRelationships_Performance() {
        // Given
        Long userId = 1L;
        createTestRelationships(userId, 1000);
        
        // When
        long startTime = System.currentTimeMillis();
        
        List<RelationshipResponse> relationships = relationshipService.getUserRelationships(userId);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        assertThat(duration).isLessThan(100); // Should complete within 100ms
        assertThat(relationships).hasSize(1000);
    }
}
```

### Stress Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class RelationshipServiceStressTest {
    
    @Autowired
    private RelationshipService relationshipService;
    
    @Test
    void testConcurrentRelationshipCreation() throws InterruptedException {
        // Given
        int numberOfThreads = 10;
        int relationshipsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < relationshipsPerThread; j++) {
                        try {
                            CreateRelationshipRequest request = CreateRelationshipRequest.builder()
                                .user1Id((long) (threadId * 1000 + j))
                                .user2Id((long) (threadId * 1000 + j + 1))
                                .relationshipTypeId(1L)
                                .build();
                            
                            relationshipService.createRelationship(request);
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Then
        assertThat(successCount.get()).isEqualTo(numberOfThreads * relationshipsPerThread);
        assertThat(failureCount.get()).isEqualTo(0);
    }
}
```

## Security Testing

### Authentication Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class RelationshipServiceSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testUnauthorizedAccess() throws Exception {
        // When
        mockMvc.perform(get("/relationships"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testAuthorizedAccess() throws Exception {
        // Given
        String validJwtToken = createValidJwtToken();
        
        // When
        mockMvc.perform(get("/relationships")
                .header("Authorization", "Bearer " + validJwtToken))
                .andExpect(status().isOk());
    }
    
    @Test
    void testInvalidJwtToken() throws Exception {
        // Given
        String invalidJwtToken = "invalid.token.here";
        
        // When
        mockMvc.perform(get("/relationships")
                .header("Authorization", "Bearer " + invalidJwtToken))
                .andExpect(status().isUnauthorized());
    }
}
```

### Input Validation Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class RelationshipServiceInputValidationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testSqlInjectionPrevention() throws Exception {
        // Given
        String maliciousInput = "'; DROP TABLE user_relationships; --";
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(1L)
            .metadata(maliciousInput)
            .build();
        
        // When
        mockMvc.perform(post("/relationships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testXssPrevention() throws Exception {
        // Given
        String xssPayload = "<script>alert('XSS')</script>";
        CreateRelationshipRequest request = CreateRelationshipRequest.builder()
            .user1Id(1L)
            .user2Id(2L)
            .relationshipTypeId(1L)
            .metadata(xssPayload)
            .build();
        
        // When
        mockMvc.perform(post("/relationships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
```

## Test Data Management

### Test Data Setup
```java
@Component
public class TestDataSetup {
    
    @Autowired
    private RelationshipTypeRepository typeRepository;
    
    @Autowired
    private UserRelationshipRepository relationshipRepository;
    
    public void setupRelationshipTypes() {
        if (typeRepository.count() == 0) {
            // Create family relationship types
            RelationshipType father = new RelationshipType();
            father.setName("Father");
            father.setCategory("FAMILY");
            father.setBidirectional(true);
            typeRepository.save(father);
            
            RelationshipType son = new RelationshipType();
            son.setName("Son");
            son.setCategory("FAMILY");
            son.setBidirectional(true);
            son.setReverseTypeId(father.getId());
            typeRepository.save(son);
            
            father.setReverseTypeId(son.getId());
            typeRepository.save(father);
        }
    }
    
    public void setupTestRelationships() {
        if (relationshipRepository.count() == 0) {
            // Create test relationships
            for (int i = 1; i <= 100; i++) {
                UserRelationship relationship = new UserRelationship();
                relationship.setUser1Id((long) i);
                relationship.setUser2Id((long) (i + 1));
                relationship.setRelationshipTypeId(1L);
                relationship.setStatus(RelationshipStatus.ACTIVE);
                relationshipRepository.save(relationship);
            }
        }
    }
}
```

### Test Data Cleanup
```java
@SpringBootTest
@Transactional
@Rollback
class RelationshipServiceTestWithCleanup {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRelationshipRepository relationshipRepository;
    
    @AfterEach
    void cleanup() {
        // Clean up test data after each test
        relationshipRepository.deleteAll();
        entityManager.flush();
    }
    
    @Test
    void testWithCleanup() {
        // Test implementation
    }
}
```

## Test Configuration

### Test Properties
```properties
# test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Disable security for testing
spring.security.user.name=test
spring.security.user.password=test

# Test logging
logging.level.com.legacykeep=DEBUG
logging.level.org.springframework=WARN
```

### Test Profiles
```java
@ActiveProfiles("test")
@SpringBootTest
class RelationshipServiceTest {
    
    @Test
    void testWithTestProfile() {
        // Test implementation
    }
}
```

## Test Execution

### Maven Test Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RelationshipServiceTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Run integration tests
mvn verify

# Run tests with coverage
mvn test jacoco:report
```

### Test Reports
```xml
<!-- JaCoCo Coverage Plugin -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Continuous Integration

### GitHub Actions Test Pipeline
```yaml
name: Test Relationship Service

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:14
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: relationship_db_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Run tests
      run: mvn test
    
    - name: Run integration tests
      run: mvn verify
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
```

## Test Metrics

### Coverage Targets
- **Line Coverage**: Minimum 80%
- **Branch Coverage**: Minimum 70%
- **Method Coverage**: Minimum 85%
- **Class Coverage**: Minimum 90%

### Performance Targets
- **API Response Time**: < 100ms for 95th percentile
- **Database Query Time**: < 50ms for 95th percentile
- **Memory Usage**: < 512MB under normal load
- **CPU Usage**: < 50% under normal load

### Quality Gates
- **Test Pass Rate**: 100%
- **Code Coverage**: Meets minimum thresholds
- **Performance**: Meets performance targets
- **Security**: No high-severity vulnerabilities
