# Relationship Service Documentation

## Overview

The Relationship Service is a microservice within the LegacyKeep platform that manages user relationships and relationship types. It provides comprehensive APIs for creating, managing, and querying relationships between users in the system.

## Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL 15+
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven 3.9+
- **Documentation**: OpenAPI 3.0 (Swagger)

### Service Configuration
- **Port**: 8083
- **Context Path**: `/relationship`
- **Database**: `legacykeep_relationship`
- **Profile**: `dev` (default)

## Database Schema

### Tables

#### 1. relationship_types
Stores different types of relationships that can exist between users.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | bigint | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| name | varchar(100) | NOT NULL, UNIQUE | Name of the relationship type |
| category | varchar(50) | NOT NULL, CHECK | Category: FAMILY, SOCIAL, PROFESSIONAL, CUSTOM |
| bidirectional | boolean | NOT NULL | Whether the relationship is bidirectional |
| reverse_type_id | bigint | FOREIGN KEY | Reference to reverse relationship type |
| metadata | jsonb | NULLABLE | Additional metadata as JSON |
| created_at | timestamp | NOT NULL | Creation timestamp |
| updated_at | timestamp | NOT NULL | Last update timestamp |

#### 2. user_relationships
Stores actual relationships between users.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | bigint | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| user1_id | bigint | NOT NULL | First user ID |
| user2_id | bigint | NOT NULL | Second user ID |
| relationship_type_id | bigint | NOT NULL, FOREIGN KEY | Reference to relationship type |
| context_id | bigint | NULLABLE | Context identifier |
| start_date | date | NULLABLE | Relationship start date |
| end_date | date | NULLABLE | Relationship end date |
| status | varchar(20) | NOT NULL, CHECK | Status: ACTIVE, ENDED, SUSPENDED, PENDING |
| metadata | jsonb | NULLABLE | Additional metadata as JSON |
| created_at | timestamp | NOT NULL | Creation timestamp |
| updated_at | timestamp | NOT NULL | Last update timestamp |

## API Endpoints

### Health Check Endpoints

#### GET /health
Basic health check endpoint.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-09-13T17:08:10Z"
}
```

#### GET /health/detailed
Detailed health check with database connectivity.

**Response:**
```json
{
  "status": "UP",
  "database": "UP",
  "timestamp": "2025-09-13T17:08:10Z"
}
```

### Relationship Type Endpoints

#### GET /v1/relationship-types
Get all relationship types with pagination.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response:**
```json
{
  "success": true,
  "message": "Relationship types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Friend",
      "category": "SOCIAL",
      "bidirectional": true,
      "reverseTypeId": null,
      "metadata": "{\"description\": \"Close personal relationship\"}",
      "createdAt": "2025-09-13T17:05:43Z",
      "updatedAt": "2025-09-13T17:05:43Z"
    }
  ],
  "timestamp": "2025-09-13T17:05:54Z"
}
```

#### GET /v1/relationship-types/{id}
Get relationship type by ID.

**Path Parameters:**
- `id`: Relationship type ID

**Response:**
```json
{
  "success": true,
  "message": "Relationship type retrieved successfully",
  "data": {
    "id": 1,
    "name": "Friend",
    "category": "SOCIAL",
    "bidirectional": true,
    "reverseTypeId": null,
    "metadata": "{\"description\": \"Close personal relationship\"}",
    "createdAt": "2025-09-13T17:05:43Z",
    "updatedAt": "2025-09-13T17:05:43Z"
  },
  "timestamp": "2025-09-13T17:06:04Z"
}
```

#### GET /v1/relationship-types/name/{name}
Get relationship type by name.

**Path Parameters:**
- `name`: Relationship type name

#### POST /v1/relationship-types
Create a new relationship type.

**Request Body:**
```json
{
  "name": "Friend",
  "category": "SOCIAL",
  "bidirectional": true,
  "reverseTypeId": null,
  "metadata": "{\"description\": \"Close personal relationship\"}"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Relationship type created successfully",
  "data": {
    "id": 1,
    "name": "Friend",
    "category": "SOCIAL",
    "bidirectional": true,
    "reverseTypeId": null,
    "metadata": "{\"description\": \"Close personal relationship\"}",
    "createdAt": "2025-09-13T17:05:43Z",
    "updatedAt": "2025-09-13T17:05:43Z"
  },
  "timestamp": "2025-09-13T17:05:43Z"
}
```

#### PUT /v1/relationship-types/{id}
Update an existing relationship type.

**Path Parameters:**
- `id`: Relationship type ID

**Request Body:**
```json
{
  "name": "Best Friend",
  "metadata": "{\"description\": \"Closest personal relationship\"}"
}
```

#### GET /v1/relationship-types/search
Search relationship types by name.

**Query Parameters:**
- `name`: Search term for relationship type name

### User Relationship Endpoints

#### GET /v1/relationships/user/{userId}
Get all relationships for a specific user.

**Path Parameters:**
- `userId`: User ID

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `status` (optional): Filter by status (ACTIVE, ENDED, SUSPENDED, PENDING)

**Response:**
```json
{
  "success": true,
  "message": "User relationships retrieved successfully",
  "data": {
    "relationships": [
      {
        "id": 1,
        "user1Id": 1,
        "user2Id": 2,
        "relationshipType": {
          "id": 1,
          "name": "Best Friend",
          "category": "SOCIAL",
          "bidirectional": true,
          "reverseTypeId": null,
          "metadata": "{\"description\": \"Closest personal relationship\"}",
          "createdAt": "2025-09-13T17:05:43Z",
          "updatedAt": "2025-09-13T17:06:10Z"
        },
        "contextId": null,
        "startDate": "2024-01-01",
        "endDate": null,
        "status": "ACTIVE",
        "metadata": "{\"notes\": \"Met at work\"}",
        "createdAt": "2025-09-13T17:06:22Z",
        "updatedAt": "2025-09-13T17:06:22Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "2025-09-13T17:06:31Z"
}
```

#### GET /v1/relationships/between/{user1Id}/{user2Id}
Get relationships between two specific users.

**Path Parameters:**
- `user1Id`: First user ID
- `user2Id`: Second user ID

#### GET /v1/relationships/{id}
Get relationship by ID.

**Path Parameters:**
- `id`: Relationship ID

#### POST /v1/relationships
Create a new user relationship.

**Request Body:**
```json
{
  "user1Id": 1,
  "user2Id": 2,
  "relationshipTypeId": 1,
  "contextId": null,
  "startDate": "2024-01-01",
  "endDate": null,
  "status": "ACTIVE",
  "metadata": "{\"notes\": \"Met at work\"}"
}
```

#### PUT /v1/relationships/{id}
Update an existing relationship.

**Path Parameters:**
- `id`: Relationship ID

**Request Body:**
```json
{
  "status": "ENDED",
  "endDate": "2024-12-31",
  "metadata": "{\"notes\": \"Friendship ended\", \"reason\": \"Moved away\"}"
}
```

#### GET /v1/relationships/user/{userId}/stats
Get relationship statistics for a user.

**Path Parameters:**
- `userId`: User ID

**Response:**
```json
{
  "success": true,
  "message": "User relationship statistics retrieved successfully",
  "data": {
    "endedRelationships": 1,
    "activeRelationships": 0,
    "totalRelationships": 1
  },
  "timestamp": "2025-09-13T17:06:50Z"
}
```

#### GET /v1/relationships/exists/{user1Id}/{user2Id}
Check if a relationship exists between two users.

**Path Parameters:**
- `user1Id`: First user ID
- `user2Id`: Second user ID

**Response:**
```json
{
  "success": true,
  "message": "Relationship existence check completed",
  "data": {
    "exists": true
  },
  "timestamp": "2025-09-13T17:06:50Z"
}
```

## Error Handling

### Error Response Format
All error responses follow a consistent format:

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2025-09-13T17:08:10Z"
}
```

### Common Error Scenarios

#### 404 Not Found
- Relationship type not found
- User relationship not found

#### 409 Conflict
- Duplicate relationship creation
- Duplicate relationship type name

#### 400 Bad Request
- Invalid request body
- Validation errors

#### 500 Internal Server Error
- Database connection issues
- Unexpected server errors

## Configuration

### Application Properties
```properties
# Server Configuration
server.port=8083
server.servlet.context-path=/relationship

# Application Configuration
spring.application.name=relationship-service
spring.profiles.active=dev

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/legacykeep_relationship
spring.datasource.username=lohithsurisetti
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Logging Configuration
logging.level.com.legacykeep.relationship=DEBUG
logging.level.org.springframework.web=DEBUG
```

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 15+
- Git

### Local Development

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd relationship-service
   ```

2. **Set up the database:**
   ```bash
   createdb legacykeep_relationship
   ```

3. **Configure application properties:**
   Update `application.properties` with your database credentials.

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the service:**
   - Base URL: `http://localhost:8083/relationship`
   - Health Check: `http://localhost:8083/relationship/health`

### Testing

#### Manual Testing
Use the provided test scripts or curl commands:

```bash
# Health check
curl http://localhost:8083/relationship/health

# Create relationship type
curl -X POST http://localhost:8083/relationship/v1/relationship-types \
  -H "Content-Type: application/json" \
  -d '{"name": "Friend", "category": "SOCIAL", "bidirectional": true}'

# Get all relationship types
curl http://localhost:8083/relationship/v1/relationship-types
```

#### Automated Testing
```bash
mvn test
```

## Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/relationship-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `SPRING_DATASOURCE_URL`: Database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SERVER_PORT`: Application port (default: 8083)

## Monitoring and Logging

### Health Checks
- Basic health: `/health`
- Detailed health: `/health/detailed`

### Logging
- Application logs: `logs/relationship-service.log`
- Log level: DEBUG for development, INFO for production

### Metrics
- Spring Boot Actuator endpoints available
- Custom metrics for relationship operations

## Security

### Authentication
- JWT token validation (configured but not enforced in dev mode)
- Spring Security configuration available

### Authorization
- Role-based access control (future implementation)
- API key authentication (future implementation)

## Performance Considerations

### Database Optimization
- Indexes on frequently queried columns
- Connection pooling with HikariCP
- Query optimization for large datasets

### Caching
- Redis integration available for future implementation
- In-memory caching for relationship types

## Future Enhancements

### Planned Features
1. **Bulk Operations**: Bulk create/update relationships
2. **Advanced Search**: Complex relationship queries
3. **Relationship Analytics**: Relationship insights and statistics
4. **Event Integration**: Kafka integration for relationship events
5. **Caching Layer**: Redis integration for performance
6. **Audit Trail**: Complete audit logging for relationship changes

### API Versioning
- Current version: v1
- Future versions will maintain backward compatibility
- Deprecation notices will be provided for breaking changes

## Troubleshooting

### Common Issues

#### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials
- Ensure database exists

#### Port Conflicts
- Change `server.port` in application.properties
- Check if port 8083 is available

#### JSONB Mapping Issues
- Ensure proper `@JdbcTypeCode(SqlTypes.JSON)` annotation
- Verify JSON format in metadata fields

### Debug Mode
Enable debug logging:
```properties
logging.level.com.legacykeep.relationship=DEBUG
logging.level.org.springframework.web=DEBUG
```

## Support

For issues and questions:
1. Check the logs for error details
2. Verify database connectivity
3. Test with health check endpoints
4. Review API documentation

---

**Last Updated**: September 13, 2025
**Version**: 1.0.0
**Author**: LegacyKeep Development Team
