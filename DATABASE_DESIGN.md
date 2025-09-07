# Relationship Service Database Design

## Overview
This document describes the database design for the Relationship Service, including table structures, relationships, indexes, and migration strategies.

## Database Technology
- **Database**: PostgreSQL 14+
- **ORM**: JPA/Hibernate
- **Migration**: Flyway
- **Connection Pool**: HikariCP

## Database Schema

### 1. relationship_types Table

#### Purpose
Defines all available relationship types in the system, including predefined and custom types.

#### Table Structure
```sql
CREATE TABLE relationship_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    bidirectional BOOLEAN NOT NULL DEFAULT false,
    reverse_type_id BIGINT REFERENCES relationship_types(id),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### Column Descriptions
- **id**: Primary key, auto-incrementing
- **name**: Unique name of the relationship type (e.g., "Father", "Son", "Spouse")
- **category**: Category of relationship (FAMILY, SOCIAL, PROFESSIONAL, CUSTOM)
- **bidirectional**: Whether this relationship type works both ways
- **reverse_type_id**: Reference to the reverse relationship type (e.g., Father → Son)
- **metadata**: JSON field for additional properties and descriptions
- **created_at**: Timestamp when the record was created
- **updated_at**: Timestamp when the record was last updated

#### Indexes
```sql
-- Primary key index (automatic)
CREATE UNIQUE INDEX idx_relationship_types_id ON relationship_types(id);

-- Unique constraint on name
CREATE UNIQUE INDEX idx_relationship_types_name ON relationship_types(name);

-- Category index for filtering
CREATE INDEX idx_relationship_types_category ON relationship_types(category);

-- Bidirectional flag index
CREATE INDEX idx_relationship_types_bidirectional ON relationship_types(bidirectional);

-- Reverse type reference index
CREATE INDEX idx_relationship_types_reverse_type_id ON relationship_types(reverse_type_id);
```

### 2. user_relationships Table

#### Purpose
Stores actual relationships between users with their properties and status.

#### Table Structure
```sql
CREATE TABLE user_relationships (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    relationship_type_id BIGINT NOT NULL REFERENCES relationship_types(id),
    context_id BIGINT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_user_relationships_different_users CHECK (user1_id != user2_id),
    CONSTRAINT chk_user_relationships_date_range CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_user_relationships_status CHECK (status IN ('ACTIVE', 'ENDED', 'SUSPENDED', 'PENDING'))
);
```

#### Column Descriptions
- **id**: Primary key, auto-incrementing
- **user1_id**: ID of the first user in the relationship
- **user2_id**: ID of the second user in the relationship
- **relationship_type_id**: Reference to the relationship type
- **context_id**: Optional context (e.g., family circle ID)
- **start_date**: When the relationship began
- **end_date**: When the relationship ended (NULL for active relationships)
- **status**: Current status of the relationship
- **metadata**: JSON field for additional relationship properties
- **created_at**: Timestamp when the record was created
- **updated_at**: Timestamp when the record was last updated

#### Indexes
```sql
-- Primary key index (automatic)
CREATE UNIQUE INDEX idx_user_relationships_id ON user_relationships(id);

-- User relationship indexes
CREATE INDEX idx_user_relationships_user1_id ON user_relationships(user1_id);
CREATE INDEX idx_user_relationships_user2_id ON user_relationships(user2_id);

-- Composite index for user relationships
CREATE INDEX idx_user_relationships_users ON user_relationships(user1_id, user2_id);

-- Relationship type index
CREATE INDEX idx_user_relationships_type_id ON user_relationships(relationship_type_id);

-- Status index
CREATE INDEX idx_user_relationships_status ON user_relationships(status);

-- Context index
CREATE INDEX idx_user_relationships_context_id ON user_relationships(context_id);

-- Date range indexes
CREATE INDEX idx_user_relationships_start_date ON user_relationships(start_date);
CREATE INDEX idx_user_relationships_end_date ON user_relationships(end_date);

-- Composite index for active relationships
CREATE INDEX idx_user_relationships_active ON user_relationships(status, start_date) 
WHERE status = 'ACTIVE';

-- Composite index for user active relationships
CREATE INDEX idx_user_relationships_user_active ON user_relationships(user1_id, status, start_date) 
WHERE status = 'ACTIVE';
```

#### Unique Constraints
```sql
-- Prevent duplicate relationships
CREATE UNIQUE INDEX idx_user_relationships_unique ON user_relationships(user1_id, user2_id, relationship_type_id, context_id);
```

## Default Data

### Predefined Relationship Types

#### Family Relationships
```sql
-- Insert family relationship types
INSERT INTO relationship_types (name, category, bidirectional, reverse_type_id, metadata) VALUES
('Father', 'FAMILY', true, 2, '{"description": "Biological or adoptive father", "examples": ["Biological father", "Adoptive father", "Step-father"]}'),
('Son', 'FAMILY', true, 1, '{"description": "Biological or adoptive son", "examples": ["Biological son", "Adoptive son", "Step-son"]}'),
('Mother', 'FAMILY', true, 4, '{"description": "Biological or adoptive mother", "examples": ["Biological mother", "Adoptive mother", "Step-mother"]}'),
('Daughter', 'FAMILY', true, 3, '{"description": "Biological or adoptive daughter", "examples": ["Biological daughter", "Adoptive daughter", "Step-daughter"]}'),
('Brother', 'FAMILY', true, 5, '{"description": "Male sibling", "examples": ["Biological brother", "Half-brother", "Step-brother"]}'),
('Sister', 'FAMILY', true, 6, '{"description": "Female sibling", "examples": ["Biological sister", "Half-sister", "Step-sister"]}'),
('Spouse', 'FAMILY', true, 7, '{"description": "Married partner", "examples": ["Husband", "Wife", "Civil partner"]}'),
('Grandfather', 'FAMILY', true, 8, '{"description": "Father of parent", "examples": ["Paternal grandfather", "Maternal grandfather"]}'),
('Grandmother', 'FAMILY', true, 9, '{"description": "Mother of parent", "examples": ["Paternal grandmother", "Maternal grandmother"]}'),
('Uncle', 'FAMILY', true, 10, '{"description": "Brother of parent", "examples": ["Paternal uncle", "Maternal uncle"]}'),
('Aunt', 'FAMILY', true, 11, '{"description": "Sister of parent", "examples": ["Paternal aunt", "Maternal aunt"]}'),
('Cousin', 'FAMILY', true, 12, '{"description": "Child of uncle or aunt", "examples": ["First cousin", "Second cousin"]}');
```

#### Social Relationships
```sql
-- Insert social relationship types
INSERT INTO relationship_types (name, category, bidirectional, metadata) VALUES
('Best Friend', 'SOCIAL', true, '{"description": "Closest friend", "examples": ["Childhood friend", "College friend", "Work friend"]}'),
('Friend', 'SOCIAL', true, '{"description": "General friend", "examples": ["Close friend", "Acquaintance", "Social friend"]}'),
('Neighbor', 'SOCIAL', true, '{"description": "Person living nearby", "examples": ["Next-door neighbor", "Apartment neighbor"]}'),
('Roommate', 'SOCIAL', true, '{"description": "Person sharing living space", "examples": ["College roommate", "Apartment roommate"]}');
```

#### Professional Relationships
```sql
-- Insert professional relationship types
INSERT INTO relationship_types (name, category, bidirectional, metadata) VALUES
('Mentor', 'PROFESSIONAL', true, '{"description": "Professional guide and advisor", "examples": ["Career mentor", "Business mentor", "Academic mentor"]}'),
('Mentee', 'PROFESSIONAL', true, '{"description": "Person being mentored", "examples": ["Career mentee", "Business mentee", "Academic mentee"]}'),
('Colleague', 'PROFESSIONAL', true, '{"description": "Work associate", "examples": ["Team member", "Department colleague", "Project partner"]}'),
('Boss', 'PROFESSIONAL', true, '{"description": "Supervisor or manager", "examples": ["Direct manager", "Department head", "CEO"]}'),
('Employee', 'PROFESSIONAL', true, '{"description": "Person under supervision", "examples": ["Direct report", "Team member", "Staff member"]}');
```

## Database Migrations

### Migration Strategy
- **Flyway**: Database migration tool
- **Versioning**: Sequential version numbers (V1, V2, V3, etc.)
- **Rollback**: Manual rollback scripts for complex changes
- **Baseline**: Support for existing databases

### Migration Files

#### V1__Create_initial_tables.sql
```sql
-- Create relationship_types table
CREATE TABLE relationship_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    bidirectional BOOLEAN NOT NULL DEFAULT false,
    reverse_type_id BIGINT REFERENCES relationship_types(id),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_relationships table
CREATE TABLE user_relationships (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    relationship_type_id BIGINT NOT NULL REFERENCES relationship_types(id),
    context_id BIGINT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_user_relationships_different_users CHECK (user1_id != user2_id),
    CONSTRAINT chk_user_relationships_date_range CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_user_relationships_status CHECK (status IN ('ACTIVE', 'ENDED', 'SUSPENDED', 'PENDING'))
);

-- Create indexes
CREATE INDEX idx_relationship_types_name ON relationship_types(name);
CREATE INDEX idx_relationship_types_category ON relationship_types(category);
CREATE INDEX idx_relationship_types_bidirectional ON relationship_types(bidirectional);
CREATE INDEX idx_relationship_types_reverse_type_id ON relationship_types(reverse_type_id);

CREATE INDEX idx_user_relationships_user1_id ON user_relationships(user1_id);
CREATE INDEX idx_user_relationships_user2_id ON user_relationships(user2_id);
CREATE INDEX idx_user_relationships_users ON user_relationships(user1_id, user2_id);
CREATE INDEX idx_user_relationships_type_id ON user_relationships(relationship_type_id);
CREATE INDEX idx_user_relationships_status ON user_relationships(status);
CREATE INDEX idx_user_relationships_context_id ON user_relationships(context_id);
CREATE INDEX idx_user_relationships_start_date ON user_relationships(start_date);
CREATE INDEX idx_user_relationships_end_date ON user_relationships(end_date);

-- Create unique constraints
CREATE UNIQUE INDEX idx_user_relationships_unique ON user_relationships(user1_id, user2_id, relationship_type_id, context_id);

-- Create partial indexes for performance
CREATE INDEX idx_user_relationships_active ON user_relationships(status, start_date) 
WHERE status = 'ACTIVE';

CREATE INDEX idx_user_relationships_user_active ON user_relationships(user1_id, status, start_date) 
WHERE status = 'ACTIVE';
```

#### V2__Insert_default_relationship_types.sql
```sql
-- Insert family relationship types
INSERT INTO relationship_types (name, category, bidirectional, reverse_type_id, metadata) VALUES
('Father', 'FAMILY', true, 2, '{"description": "Biological or adoptive father", "examples": ["Biological father", "Adoptive father", "Step-father"]}'),
('Son', 'FAMILY', true, 1, '{"description": "Biological or adoptive son", "examples": ["Biological son", "Adoptive son", "Step-son"]}'),
('Mother', 'FAMILY', true, 4, '{"description": "Biological or adoptive mother", "examples": ["Biological mother", "Adoptive mother", "Step-mother"]}'),
('Daughter', 'FAMILY', true, 3, '{"description": "Biological or adoptive daughter", "examples": ["Biological daughter", "Adoptive daughter", "Step-daughter"]}'),
('Brother', 'FAMILY', true, 5, '{"description": "Male sibling", "examples": ["Biological brother", "Half-brother", "Step-brother"]}'),
('Sister', 'FAMILY', true, 6, '{"description": "Female sibling", "examples": ["Biological sister", "Half-sister", "Step-sister"]}'),
('Spouse', 'FAMILY', true, 7, '{"description": "Married partner", "examples": ["Husband", "Wife", "Civil partner"]}'),
('Grandfather', 'FAMILY', true, 8, '{"description": "Father of parent", "examples": ["Paternal grandfather", "Maternal grandfather"]}'),
('Grandmother', 'FAMILY', true, 9, '{"description": "Mother of parent", "examples": ["Paternal grandmother", "Maternal grandmother"]}'),
('Uncle', 'FAMILY', true, 10, '{"description": "Brother of parent", "examples": ["Paternal uncle", "Maternal uncle"]}'),
('Aunt', 'FAMILY', true, 11, '{"description": "Sister of parent", "examples": ["Paternal aunt", "Maternal aunt"]}'),
('Cousin', 'FAMILY', true, 12, '{"description": "Child of uncle or aunt", "examples": ["First cousin", "Second cousin"]}');

-- Insert social relationship types
INSERT INTO relationship_types (name, category, bidirectional, metadata) VALUES
('Best Friend', 'SOCIAL', true, '{"description": "Closest friend", "examples": ["Childhood friend", "College friend", "Work friend"]}'),
('Friend', 'SOCIAL', true, '{"description": "General friend", "examples": ["Close friend", "Acquaintance", "Social friend"]}'),
('Neighbor', 'SOCIAL', true, '{"description": "Person living nearby", "examples": ["Next-door neighbor", "Apartment neighbor"]}'),
('Roommate', 'SOCIAL', true, '{"description": "Person sharing living space", "examples": ["College roommate", "Apartment roommate"]}');

-- Insert professional relationship types
INSERT INTO relationship_types (name, category, bidirectional, metadata) VALUES
('Mentor', 'PROFESSIONAL', true, '{"description": "Professional guide and advisor", "examples": ["Career mentor", "Business mentor", "Academic mentor"]}'),
('Mentee', 'PROFESSIONAL', true, '{"description": "Person being mentored", "examples": ["Career mentee", "Business mentee", "Academic mentee"]}'),
('Colleague', 'PROFESSIONAL', true, '{"description": "Work associate", "examples": ["Team member", "Department colleague", "Project partner"]}'),
('Boss', 'PROFESSIONAL', true, '{"description": "Supervisor or manager", "examples": ["Direct manager", "Department head", "CEO"]}'),
('Employee', 'PROFESSIONAL', true, '{"description": "Person under supervision", "examples": ["Direct report", "Team member", "Staff member"]}');
```

## Performance Optimization

### Query Optimization

#### Common Query Patterns
1. **Get user relationships**: `WHERE user1_id = ? OR user2_id = ?`
2. **Get active relationships**: `WHERE status = 'ACTIVE'`
3. **Get relationships by type**: `WHERE relationship_type_id = ?`
4. **Get relationships in context**: `WHERE context_id = ?`

#### Index Strategy
- **Composite Indexes**: For multi-column queries
- **Partial Indexes**: For filtered queries (active relationships)
- **Covering Indexes**: Include frequently accessed columns

### Connection Pooling
```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
```

### Caching Strategy
- **L1 Cache**: Hibernate session cache
- **L2 Cache**: Hibernate second-level cache (Redis)
- **Application Cache**: Spring Cache for frequently accessed data

## Data Integrity

### Constraints
- **Primary Keys**: All tables have auto-incrementing primary keys
- **Foreign Keys**: Referential integrity between tables
- **Check Constraints**: Business rule validation
- **Unique Constraints**: Prevent duplicate relationships

### Validation Rules
- **User IDs**: Must be different (user1_id != user2_id)
- **Date Range**: end_date >= start_date
- **Status Values**: Only allowed status values
- **Relationship Types**: Must exist in relationship_types table

## Backup and Recovery

### Backup Strategy
- **Full Backup**: Daily full database backup
- **Incremental Backup**: Hourly incremental backups
- **Transaction Log Backup**: Continuous transaction log backup

### Recovery Procedures
- **Point-in-Time Recovery**: Restore to specific timestamp
- **Disaster Recovery**: Cross-region backup replication
- **Data Validation**: Post-recovery data integrity checks

## Monitoring and Maintenance

### Performance Monitoring
- **Query Performance**: Slow query logging and analysis
- **Index Usage**: Monitor index effectiveness
- **Connection Pool**: Monitor connection pool metrics
- **Database Size**: Monitor table and index sizes

### Maintenance Tasks
- **Statistics Update**: Regular statistics updates for query optimization
- **Index Rebuilding**: Periodic index maintenance
- **Vacuum**: Regular VACUUM and ANALYZE operations
- **Archiving**: Archive old relationship history data

## Security

### Data Protection
- **Encryption at Rest**: Database-level encryption
- **Encryption in Transit**: SSL/TLS for connections
- **Access Control**: Role-based database access
- **Audit Logging**: Track all database changes

### Compliance
- **GDPR**: Data protection and privacy compliance
- **Data Retention**: Configurable data retention policies
- **Data Anonymization**: Support for data anonymization
- **Right to be Forgotten**: Data deletion capabilities

## Scalability Considerations

### Horizontal Scaling
- **Read Replicas**: Multiple read replicas for query distribution
- **Sharding**: Potential for user-based sharding
- **Partitioning**: Table partitioning by date or user ID

### Vertical Scaling
- **Resource Optimization**: CPU, memory, and storage optimization
- **Query Optimization**: Continuous query performance tuning
- **Index Optimization**: Regular index analysis and optimization

## Future Enhancements

### Planned Improvements
- **Graph Database**: Migration to Neo4j for complex relationship queries
- **Full-Text Search**: PostgreSQL full-text search for relationship metadata
- **JSONB Indexing**: Advanced JSONB indexing for metadata queries
- **Materialized Views**: Pre-computed relationship aggregations

### Technology Evolution
- **PostgreSQL 15+**: Upgrade to latest PostgreSQL version
- **Partitioning**: Advanced table partitioning strategies
- **Parallel Processing**: Parallel query execution
- **Machine Learning**: AI-powered relationship insights
