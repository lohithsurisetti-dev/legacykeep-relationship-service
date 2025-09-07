# Relationship Service

## Description
The Relationship Service is a microservice responsible for managing all user relationships, relationship types, and relationship history within the LegacyKeep platform. It provides a flexible and adaptable system for handling any type of relationship scenario, from traditional family structures to modern blended families and chosen families.

## Technology Stack
- Spring Boot 3.x
- Java 17
- Maven
- PostgreSQL
- JWT Authentication
- Flyway Database Migration
- Swagger/OpenAPI Documentation

## Key Features

### 🏗️ **Ultra-Flexible Relationship Model**
- **Multi-dimensional relationships**: Users can have multiple relationship roles simultaneously
- **Bidirectional relationships**: Automatic handling of reverse relationships (Father ↔ Son)
- **Temporal relationships**: Track relationship changes over time
- **Context-aware relationships**: Same people can have different relationships in different contexts
- **Custom relationship types**: Support for user-defined relationship types

### 📊 **Relationship Management**
- **Relationship Types**: Predefined and custom relationship types
- **Relationship Status**: Active, Ended, Suspended, Pending
- **Relationship Categories**: Family, Social, Professional, Custom
- **Relationship History**: Complete audit trail of relationship changes
- **Relationship Validation**: Business rules and consistency checks

### 🔗 **Service Integration**
- **User Service**: Validates user existence and retrieves user data
- **Family Circle Service**: Manages family group contexts
- **Notification Service**: Sends relationship invitations and updates
- **Event Publishing**: Publishes relationship events for other services

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 14+
- Docker (optional)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd relationship-service
```

2. **Set up the database**
```bash
# Create database
createdb relationship_db

# Create user (if needed)
createuser -s legacykeep
```

3. **Configure application properties**
```bash
# Copy and modify application properties
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

4. **Run the application**
```bash
mvn spring-boot:run
```

### Docker Setup
```bash
# Build and run with Docker Compose
docker-compose up -d
```

## API Documentation

### Base URL
- **Development**: `http://localhost:8083/relationship`
- **Production**: `https://api.legacykeep.com/relationship`

### Swagger UI
- **Development**: `http://localhost:8083/relationship/swagger-ui.html`
- **API Docs**: `http://localhost:8083/relationship/v3/api-docs`

### Health Check
- **Health Endpoint**: `http://localhost:8083/relationship/health`
- **Actuator**: `http://localhost:8083/relationship/actuator/health`

## API Endpoints

### Relationship Management
- `POST /relationships` - Create a new relationship
- `GET /relationships/user/{userId}` - Get all relationships for a user
- `GET /relationships/{id}` - Get relationship by ID
- `PUT /relationships/{id}` - Update relationship
- `DELETE /relationships/{id}` - Delete relationship
- `GET /relationships/validate` - Validate relationship rules

### Relationship Types
- `GET /relationship-types` - Get all relationship types
- `GET /relationship-types/category/{category}` - Get types by category
- `POST /relationship-types` - Create custom relationship type
- `PUT /relationship-types/{id}` - Update relationship type
- `DELETE /relationship-types/{id}` - Delete relationship type

### Relationship Queries
- `GET /relationships/user/{userId}/active` - Get active relationships
- `GET /relationships/user/{userId}/history` - Get relationship history
- `GET /relationships/context/{contextId}` - Get relationships in context
- `GET /relationships/type/{typeId}` - Get relationships by type

## Database Schema

### Core Tables

#### `relationship_types`
- **Purpose**: Defines available relationship types
- **Key Fields**: name, category, bidirectional, reverse_type_id
- **Indexes**: name (unique), category, bidirectional

#### `user_relationships`
- **Purpose**: Stores actual user relationships
- **Key Fields**: user1_id, user2_id, relationship_type_id, context_id, status
- **Indexes**: user1_id, user2_id, relationship_type_id, status, context_id

### Default Relationship Types

#### Family Relationships
- Father ↔ Son
- Mother ↔ Daughter
- Brother ↔ Sister
- Spouse ↔ Spouse
- Grandfather ↔ Grandson
- Grandmother ↔ Granddaughter
- Uncle ↔ Nephew
- Aunt ↔ Niece
- Cousin ↔ Cousin

#### Social Relationships
- Best Friend ↔ Best Friend
- Friend ↔ Friend
- Neighbor ↔ Neighbor
- Roommate ↔ Roommate

#### Professional Relationships
- Mentor ↔ Mentee
- Colleague ↔ Colleague
- Boss ↔ Employee

## Configuration

### Application Properties
```properties
# Server Configuration
server.port=8083
server.servlet.context-path=/relationship

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/relationship_db
spring.datasource.username=legacykeep
spring.datasource.password=password

# JWT Configuration (Shared with other services)
relationship.jwt.secret=legacykeep-jwt-secret-key-change-in-production-512-bits-minimum-required-for-hs512-algorithm
relationship.jwt.issuer=LegacyKeep
relationship.jwt.audience=LegacyKeep-Relationships
relationship.jwt.algorithm=HS256

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

### Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=relationship_db
DB_USERNAME=legacykeep
DB_PASSWORD=password

# JWT
JWT_SECRET=legacykeep-jwt-secret-key-change-in-production-512-bits-minimum-required-for-hs512-algorithm
JWT_ISSUER=LegacyKeep
JWT_AUDIENCE=LegacyKeep-Relationships

# Service Discovery
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

## Development

### Project Structure
```
relationship-service/
├── src/main/java/com/legacykeep/relationship/
│   ├── RelationshipServiceApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   └── DatabaseConfig.java
│   ├── entity/
│   │   ├── RelationshipType.java
│   │   ├── UserRelationship.java
│   │   └── enums/
│   ├── repository/
│   │   ├── RelationshipTypeRepository.java
│   │   └── UserRelationshipRepository.java
│   ├── service/
│   │   ├── RelationshipService.java
│   │   ├── RelationshipValidationService.java
│   │   └── RelationshipTypeService.java
│   ├── controller/
│   │   ├── RelationshipController.java
│   │   └── RelationshipTypeController.java
│   ├── dto/
│   │   ├── request/
│   │   ├── response/
│   │   └── ApiResponse.java
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtValidationService.java
│   └── exception/
│       ├── RelationshipException.java
│       ├── RelationshipValidationException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.properties
│   ├── db/migration/
│   └── templates/
├── src/test/java/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

### Building the Project
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

### Testing
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# API testing with curl
curl -X GET http://localhost:8083/relationship/health
curl -X GET http://localhost:8083/relationship/relationship-types
```

## Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t legacykeep/relationship-service:latest .

# Run container
docker run -d -p 8083:8083 \
  -e DB_HOST=localhost \
  -e DB_NAME=relationship_db \
  -e JWT_SECRET=your-secret \
  legacykeep/relationship-service:latest
```

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: relationship-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: relationship-service
  template:
    metadata:
      labels:
        app: relationship-service
    spec:
      containers:
      - name: relationship-service
        image: legacykeep/relationship-service:latest
        ports:
        - containerPort: 8083
        env:
        - name: DB_HOST
          value: "postgres-service"
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: secret
```

## Monitoring and Logging

### Health Checks
- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`
- **Custom Health**: `/health`

### Metrics
- **Prometheus**: `/actuator/prometheus`
- **Micrometer**: Built-in metrics collection
- **Custom Metrics**: Relationship creation, validation, errors

### Logging
- **Log Level**: Configurable via application.properties
- **Log Format**: JSON for structured logging
- **Log Aggregation**: ELK Stack compatible

## Security

### Authentication
- **JWT Tokens**: Shared secret with other services
- **Token Validation**: Automatic validation on all endpoints
- **Role-based Access**: Admin, User roles

### Authorization
- **Public Endpoints**: Health, API docs
- **Protected Endpoints**: All relationship operations
- **Admin Endpoints**: Relationship type management

### Data Protection
- **Input Validation**: Comprehensive validation on all inputs
- **SQL Injection Prevention**: JPA/Hibernate protection
- **XSS Protection**: Input sanitization

## Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check PostgreSQL status
brew services list | grep postgresql

# Restart PostgreSQL
brew services restart postgresql@14

# Check database exists
psql -l | grep relationship_db
```

#### JWT Validation Issues
```bash
# Verify JWT secret matches other services
# Check JWT configuration in application.properties
# Ensure JWT token is properly formatted
```

#### Port Conflicts
```bash
# Check if port 8083 is in use
lsof -i :8083

# Kill process using port
kill -9 <PID>
```

### Logs
```bash
# View application logs
tail -f logs/relationship-service.log

# View Docker logs
docker logs -f relationship-service

# View Kubernetes logs
kubectl logs -f deployment/relationship-service
```

## Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

### Code Standards
- **Java**: Follow Google Java Style Guide
- **Tests**: Minimum 80% code coverage
- **Documentation**: Update README for new features
- **Commits**: Use conventional commit messages

### Testing Requirements
- **Unit Tests**: All service methods
- **Integration Tests**: All API endpoints
- **Performance Tests**: Load testing for critical paths

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Support
For support and questions:
- **Documentation**: [Wiki](link-to-wiki)
- **Issues**: [GitHub Issues](link-to-issues)
- **Discussions**: [GitHub Discussions](link-to-discussions)
- **Email**: support@legacykeep.com

## Changelog

### Version 1.0.0 (Current)
- Initial release
- Core relationship management
- Relationship type management
- JWT authentication
- Database migration
- API documentation
- Health checks and monitoring

### Planned Features
- **Version 1.1.0**: Advanced relationship queries
- **Version 1.2.0**: Relationship analytics
- **Version 1.3.0**: Relationship templates
- **Version 2.0.0**: Graph database integration
