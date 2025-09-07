# Relationship Service API Design

## Overview
This document defines the REST API design for the Relationship Service, following RESTful principles and industry best practices for microservices APIs.

## Base Information

### Base URL
- **Development**: `http://localhost:8083/relationship`
- **Staging**: `https://staging-api.legacykeep.com/relationship`
- **Production**: `https://api.legacykeep.com/relationship`

### API Versioning
- **Current Version**: v1
- **Versioning Strategy**: URL-based versioning (`/v1/`)
- **Backward Compatibility**: Maintained for at least 2 versions

### Content Type
- **Request**: `application/json`
- **Response**: `application/json`
- **Character Encoding**: UTF-8

## Authentication

### JWT Authentication
All protected endpoints require a valid JWT token in the Authorization header:

```http
Authorization: Bearer <jwt-token>
```

### Token Format
```json
{
  "sub": "user@example.com",
  "userId": 123,
  "iss": "LegacyKeep",
  "aud": "LegacyKeep-Relationships",
  "exp": 1640995200,
  "iat": 1640908800
}
```

## Response Format

### Standard Response Structure
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Error Response Structure
```json
{
  "success": false,
  "message": "Error description",
  "error": {
    "code": "RELATIONSHIP_NOT_FOUND",
    "details": "Additional error details"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## HTTP Status Codes

### Success Codes
- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Request successful, no content returned

### Client Error Codes
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict (e.g., duplicate relationship)
- **422 Unprocessable Entity**: Validation error

### Server Error Codes
- **500 Internal Server Error**: Unexpected server error
- **503 Service Unavailable**: Service temporarily unavailable

## API Endpoints

### 1. Health and Status

#### GET /health
Get service health status.

**Response:**
```json
{
  "success": true,
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "service": "Relationship Service",
    "version": "1.0.0",
    "database": "Connected",
    "timestamp": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 2. Relationship Management

#### POST /relationships
Create a new relationship between two users.

**Request:**
```json
{
  "user1Id": 123,
  "user2Id": 456,
  "relationshipTypeId": 1,
  "contextId": 789,
  "startDate": "2024-01-01",
  "metadata": {
    "notes": "Met at work",
    "customField": "value"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Relationship created successfully",
  "data": {
    "id": 1,
    "user1Id": 123,
    "user2Id": 456,
    "relationshipType": {
      "id": 1,
      "name": "Colleague",
      "category": "PROFESSIONAL",
      "bidirectional": true
    },
    "contextId": 789,
    "startDate": "2024-01-01",
    "endDate": null,
    "status": "ACTIVE",
    "metadata": {
      "notes": "Met at work",
      "customField": "value"
    },
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### GET /relationships/user/{userId}
Get all relationships for a specific user.

**Query Parameters:**
- `status` (optional): Filter by relationship status (ACTIVE, ENDED, SUSPENDED, PENDING)
- `category` (optional): Filter by relationship category (FAMILY, SOCIAL, PROFESSIONAL, CUSTOM)
- `contextId` (optional): Filter by context ID
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response:**
```json
{
  "success": true,
  "message": "User relationships retrieved successfully",
  "data": {
    "relationships": [
      {
        "id": 1,
        "user1Id": 123,
        "user2Id": 456,
        "relationshipType": {
          "id": 1,
          "name": "Father",
          "category": "FAMILY"
        },
        "status": "ACTIVE",
        "startDate": "2020-01-01",
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### GET /relationships/{id}
Get a specific relationship by ID.

**Response:**
```json
{
  "success": true,
  "message": "Relationship retrieved successfully",
  "data": {
    "id": 1,
    "user1Id": 123,
    "user2Id": 456,
    "relationshipType": {
      "id": 1,
      "name": "Father",
      "category": "FAMILY",
      "bidirectional": true,
      "reverseTypeId": 2
    },
    "contextId": 789,
    "startDate": "2020-01-01",
    "endDate": null,
    "status": "ACTIVE",
    "metadata": {
      "notes": "Biological father"
    },
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### PUT /relationships/{id}
Update an existing relationship.

**Request:**
```json
{
  "status": "ENDED",
  "endDate": "2024-01-01",
  "metadata": {
    "notes": "Relationship ended due to divorce",
    "reason": "Divorce"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Relationship updated successfully",
  "data": {
    "id": 1,
    "user1Id": 123,
    "user2Id": 456,
    "relationshipType": {
      "id": 1,
      "name": "Spouse",
      "category": "FAMILY"
    },
    "status": "ENDED",
    "startDate": "2020-01-01",
    "endDate": "2024-01-01",
    "metadata": {
      "notes": "Relationship ended due to divorce",
      "reason": "Divorce"
    },
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### DELETE /relationships/{id}
Delete a relationship.

**Response:**
```json
{
  "success": true,
  "message": "Relationship deleted successfully",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 3. Relationship Types

#### GET /relationship-types
Get all available relationship types.

**Query Parameters:**
- `category` (optional): Filter by category (FAMILY, SOCIAL, PROFESSIONAL, CUSTOM)
- `bidirectional` (optional): Filter by bidirectional flag (true/false)

**Response:**
```json
{
  "success": true,
  "message": "Relationship types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Father",
      "category": "FAMILY",
      "bidirectional": true,
      "reverseTypeId": 2,
      "metadata": {
        "description": "Biological or adoptive father"
      },
      "createdAt": "2024-01-01T00:00:00Z"
    },
    {
      "id": 2,
      "name": "Son",
      "category": "FAMILY",
      "bidirectional": true,
      "reverseTypeId": 1,
      "metadata": {
        "description": "Biological or adoptive son"
      },
      "createdAt": "2024-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### GET /relationship-types/{id}
Get a specific relationship type by ID.

**Response:**
```json
{
  "success": true,
  "message": "Relationship type retrieved successfully",
  "data": {
    "id": 1,
    "name": "Father",
    "category": "FAMILY",
    "bidirectional": true,
    "reverseTypeId": 2,
    "metadata": {
      "description": "Biological or adoptive father",
      "examples": ["Biological father", "Adoptive father", "Step-father"]
    },
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### POST /relationship-types
Create a new custom relationship type.

**Request:**
```json
{
  "name": "Godfather",
  "category": "CUSTOM",
  "bidirectional": true,
  "metadata": {
    "description": "Spiritual father figure",
    "examples": ["Baptismal godfather", "Spiritual mentor"]
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Relationship type created successfully",
  "data": {
    "id": 25,
    "name": "Godfather",
    "category": "CUSTOM",
    "bidirectional": true,
    "reverseTypeId": null,
    "metadata": {
      "description": "Spiritual father figure",
      "examples": ["Baptismal godfather", "Spiritual mentor"]
    },
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 4. Relationship Queries

#### GET /relationships/user/{userId}/active
Get all active relationships for a user.

**Response:**
```json
{
  "success": true,
  "message": "Active relationships retrieved successfully",
  "data": [
    {
      "id": 1,
      "user1Id": 123,
      "user2Id": 456,
      "relationshipType": {
        "id": 1,
        "name": "Father",
        "category": "FAMILY"
      },
      "status": "ACTIVE",
      "startDate": "2020-01-01"
    }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### GET /relationships/user/{userId}/history
Get relationship history for a user.

**Query Parameters:**
- `startDate` (optional): Filter from date (YYYY-MM-DD)
- `endDate` (optional): Filter to date (YYYY-MM-DD)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response:**
```json
{
  "success": true,
  "message": "Relationship history retrieved successfully",
  "data": {
    "relationships": [
      {
        "id": 1,
        "user1Id": 123,
        "user2Id": 456,
        "relationshipType": {
          "id": 1,
          "name": "Spouse",
          "category": "FAMILY"
        },
        "status": "ENDED",
        "startDate": "2020-01-01",
        "endDate": "2023-12-31",
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### GET /relationships/context/{contextId}
Get all relationships within a specific context.

**Response:**
```json
{
  "success": true,
  "message": "Context relationships retrieved successfully",
  "data": [
    {
      "id": 1,
      "user1Id": 123,
      "user2Id": 456,
      "relationshipType": {
        "id": 1,
        "name": "Brother",
        "category": "FAMILY"
      },
      "status": "ACTIVE",
      "contextId": 789
    }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### GET /relationships/type/{typeId}
Get all relationships of a specific type.

**Response:**
```json
{
  "success": true,
  "message": "Type relationships retrieved successfully",
  "data": [
    {
      "id": 1,
      "user1Id": 123,
      "user2Id": 456,
      "relationshipType": {
        "id": 1,
        "name": "Father",
        "category": "FAMILY"
      },
      "status": "ACTIVE"
    }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 5. Relationship Validation

#### POST /relationships/validate
Validate relationship rules and constraints.

**Request:**
```json
{
  "user1Id": 123,
  "user2Id": 456,
  "relationshipTypeId": 1,
  "contextId": 789
}
```

**Response:**
```json
{
  "success": true,
  "message": "Relationship validation completed",
  "data": {
    "valid": true,
    "warnings": [],
    "errors": [],
    "suggestions": [
      {
        "type": "REVERSE_RELATIONSHIP",
        "message": "Consider creating reverse relationship: Son",
        "relationshipTypeId": 2
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Error Handling

### Validation Errors
```json
{
  "success": false,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": [
      {
        "field": "user1Id",
        "message": "User ID is required"
      },
      {
        "field": "relationshipTypeId",
        "message": "Relationship type does not exist"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Business Logic Errors
```json
{
  "success": false,
  "message": "Relationship already exists",
  "error": {
    "code": "RELATIONSHIP_EXISTS",
    "details": "A relationship of this type already exists between these users"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Not Found Errors
```json
{
  "success": false,
  "message": "Relationship not found",
  "error": {
    "code": "RELATIONSHIP_NOT_FOUND",
    "details": "No relationship found with ID: 999"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Rate Limiting

### Rate Limits
- **Standard Users**: 1000 requests per hour
- **Premium Users**: 5000 requests per hour
- **Admin Users**: 10000 requests per hour

### Rate Limit Headers
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1640995200
```

### Rate Limit Exceeded Response
```json
{
  "success": false,
  "message": "Rate limit exceeded",
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "details": "Too many requests. Please try again later."
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Pagination

### Pagination Parameters
- **page**: Page number (0-based, default: 0)
- **size**: Page size (default: 20, max: 100)
- **sort**: Sort field and direction (e.g., "createdAt,desc")

### Pagination Response
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": {
    "content": [...],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 100,
      "totalPages": 5,
      "first": true,
      "last": false,
      "numberOfElements": 20
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Filtering and Sorting

### Filtering
- **Exact Match**: `?status=ACTIVE`
- **Multiple Values**: `?status=ACTIVE&status=ENDED`
- **Date Range**: `?startDate=2024-01-01&endDate=2024-12-31`
- **Null Values**: `?endDate=null`

### Sorting
- **Single Field**: `?sort=createdAt`
- **Multiple Fields**: `?sort=status,createdAt`
- **Direction**: `?sort=createdAt,desc`
- **Multiple with Direction**: `?sort=status,asc&sort=createdAt,desc`

## Webhooks (Future)

### Webhook Events
- **relationship.created**: Relationship created
- **relationship.updated**: Relationship updated
- **relationship.deleted**: Relationship deleted
- **relationship.status.changed**: Relationship status changed

### Webhook Payload
```json
{
  "event": "relationship.created",
  "timestamp": "2024-01-01T00:00:00Z",
  "data": {
    "id": 1,
    "user1Id": 123,
    "user2Id": 456,
    "relationshipTypeId": 1,
    "status": "ACTIVE"
  }
}
```

## SDK and Client Libraries

### Java SDK
```java
RelationshipServiceClient client = new RelationshipServiceClient("http://localhost:8083/relationship");

// Create relationship
CreateRelationshipRequest request = CreateRelationshipRequest.builder()
    .user1Id(123L)
    .user2Id(456L)
    .relationshipTypeId(1L)
    .build();

RelationshipResponse response = client.createRelationship(request);
```

### JavaScript SDK
```javascript
const client = new RelationshipServiceClient('http://localhost:8083/relationship');

// Create relationship
const request = {
  user1Id: 123,
  user2Id: 456,
  relationshipTypeId: 1
};

const response = await client.createRelationship(request);
```

## Testing

### Postman Collection
A complete Postman collection is available for testing all endpoints:
- Import the collection from `/docs/postman/Relationship-Service.postman_collection.json`
- Set up environment variables for different environments
- Run automated tests using Newman

### Test Data
Sample test data is available in `/docs/test-data/`:
- Sample users and relationships
- Test relationship types
- Mock data for integration tests

## Changelog

### Version 1.0.0
- Initial API release
- Core relationship management endpoints
- Relationship type management
- Basic validation and error handling
- JWT authentication
- Swagger documentation

### Planned Features
- **Version 1.1.0**: Advanced relationship queries
- **Version 1.2.0**: Relationship analytics endpoints
- **Version 1.3.0**: Webhook support
- **Version 2.0.0**: Graph-based relationship queries
