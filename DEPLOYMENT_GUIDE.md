# Relationship Service Deployment Guide

## Overview

This guide provides step-by-step instructions for deploying the Relationship Service in different environments.

## Prerequisites

### System Requirements
- **Java**: OpenJDK 17 or higher
- **Memory**: Minimum 512MB RAM, Recommended 1GB+
- **Storage**: Minimum 100MB free space
- **Network**: Port 8083 available

### Software Dependencies
- **PostgreSQL**: Version 15 or higher
- **Maven**: Version 3.9 or higher (for building from source)
- **Git**: For source code management

## Environment Setup

### 1. Database Setup

#### Create Database
```bash
# Connect to PostgreSQL as superuser
psql -U postgres

# Create database and user
CREATE DATABASE legacykeep_relationship;
CREATE USER lohithsurisetti WITH PASSWORD '';
GRANT ALL PRIVILEGES ON DATABASE legacykeep_relationship TO lohithsurisetti;

# Connect to the new database
\c legacykeep_relationship

# Grant schema privileges
GRANT ALL ON SCHEMA public TO lohithsurisetti;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO lohithsurisetti;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO lohithsurisetti;
```

#### Verify Database Connection
```bash
# Test connection
psql -h localhost -U lohithsurisetti -d legacykeep_relationship -c "SELECT version();"
```

### 2. Application Configuration

#### Development Environment
```properties
# application-dev.properties
server.port=8083
server.servlet.context-path=/relationship

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

# Logging
logging.level.com.legacykeep.relationship=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### Production Environment
```properties
# application-prod.properties
server.port=8083
server.servlet.context-path=/relationship

spring.application.name=relationship-service
spring.profiles.active=prod

# Database Configuration
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/legacykeep_relationship}
spring.datasource.username=${DATABASE_USERNAME:lohithsurisetti}
spring.datasource.password=${DATABASE_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging
logging.level.com.legacykeep.relationship=INFO
logging.level.org.springframework.web=WARN
logging.file.name=logs/relationship-service.log
```

## Deployment Methods

### 1. Local Development Deployment

#### Build from Source
```bash
# Clone repository
git clone <repository-url>
cd relationship-service

# Build application
mvn clean package -DskipTests

# Run application
java -jar target/relationship-service-1.0.0.jar

# Or run with Maven
mvn spring-boot:run
```

#### Verify Deployment
```bash
# Health check
curl http://localhost:8083/relationship/health

# Detailed health check
curl http://localhost:8083/relationship/health/detailed
```

### 2. Docker Deployment

#### Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy application JAR
COPY target/relationship-service-1.0.0.jar app.jar

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8083/relationship/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Build and Run Docker Container
```bash
# Build Docker image
docker build -t relationship-service:1.0.0 .

# Run container
docker run -d \
  --name relationship-service \
  -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/legacykeep_relationship \
  -e SPRING_DATASOURCE_USERNAME=lohithsurisetti \
  -e SPRING_DATASOURCE_PASSWORD= \
  relationship-service:1.0.0

# Check container status
docker ps
docker logs relationship-service
```

### 3. Docker Compose Deployment

#### Create docker-compose.yml
```yaml
version: '3.8'

services:
  relationship-service:
    build: .
    ports:
      - "8083:8083"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/legacykeep_relationship
      - SPRING_DATASOURCE_USERNAME=lohithsurisetti
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - postgres
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8083/relationship/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=legacykeep_relationship
      - POSTGRES_USER=lohithsurisetti
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

#### Deploy with Docker Compose
```bash
# Start services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs relationship-service

# Stop services
docker-compose down
```

### 4. Production Deployment

#### Systemd Service (Linux)
```bash
# Create service file
sudo nano /etc/systemd/system/relationship-service.service
```

```ini
[Unit]
Description=LegacyKeep Relationship Service
After=network.target

[Service]
Type=simple
User=legacykeep
Group=legacykeep
WorkingDirectory=/opt/legacykeep/relationship-service
ExecStart=/usr/bin/java -jar relationship-service-1.0.0.jar
Restart=always
RestartSec=10
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/legacykeep_relationship
Environment=SPRING_DATASOURCE_USERNAME=lohithsurisetti
Environment=SPRING_DATASOURCE_PASSWORD=your_password

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable relationship-service
sudo systemctl start relationship-service

# Check status
sudo systemctl status relationship-service
```

#### Nginx Reverse Proxy
```nginx
# /etc/nginx/sites-available/relationship-service
server {
    listen 80;
    server_name relationship.legacykeep.com;

    location / {
        proxy_pass http://localhost:8083;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://localhost:8083/relationship/health;
        access_log off;
    }
}
```

## Environment Variables

### Required Variables
```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/legacykeep_relationship
DATABASE_USERNAME=lohithsurisetti
DATABASE_PASSWORD=your_password

# Application Configuration
SERVER_PORT=8083
SPRING_PROFILES_ACTIVE=prod
```

### Optional Variables
```bash
# Logging
LOG_LEVEL=INFO
LOG_FILE=logs/relationship-service.log

# Connection Pool
DB_POOL_MAX_SIZE=20
DB_POOL_MIN_IDLE=5
DB_POOL_TIMEOUT=30000

# JVM Options
JAVA_OPTS=-Xmx1g -Xms512m
```

## Monitoring and Health Checks

### Health Check Endpoints
```bash
# Basic health check
curl http://localhost:8083/relationship/health

# Detailed health check
curl http://localhost:8083/relationship/health/detailed
```

### Monitoring Setup
```bash
# Create monitoring script
cat > monitor.sh << 'EOF'
#!/bin/bash

SERVICE_URL="http://localhost:8083/relationship"
LOG_FILE="monitor.log"

while true; do
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    # Check basic health
    if curl -s "$SERVICE_URL/health" | grep -q "UP"; then
        echo "$timestamp - Service is UP" >> $LOG_FILE
    else
        echo "$timestamp - Service is DOWN" >> $LOG_FILE
        # Send alert or restart service
    fi
    
    sleep 30
done
EOF

chmod +x monitor.sh
./monitor.sh &
```

## Backup and Recovery

### Database Backup
```bash
# Create backup
pg_dump -h localhost -U lohithsurisetti -d legacykeep_relationship > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore backup
psql -h localhost -U lohithsurisetti -d legacykeep_relationship < backup_20250913_170000.sql
```

### Application Backup
```bash
# Backup application files
tar -czf relationship-service-backup-$(date +%Y%m%d).tar.gz \
  target/relationship-service-1.0.0.jar \
  application.properties \
  logs/
```

## Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check logs
tail -f logs/relationship-service.log

# Check port availability
netstat -tlnp | grep 8083

# Check Java version
java -version
```

#### Database Connection Issues
```bash
# Test database connection
psql -h localhost -U lohithsurisetti -d legacykeep_relationship -c "SELECT 1;"

# Check database status
sudo systemctl status postgresql
```

#### Memory Issues
```bash
# Check memory usage
free -h
ps aux | grep java

# Adjust JVM settings
export JAVA_OPTS="-Xmx1g -Xms512m"
```

### Log Analysis
```bash
# Search for errors
grep -i error logs/relationship-service.log

# Monitor real-time logs
tail -f logs/relationship-service.log | grep -i error

# Check startup logs
grep -i "started" logs/relationship-service.log
```

## Security Considerations

### Database Security
```bash
# Create dedicated database user
CREATE USER relationship_service WITH PASSWORD 'strong_password';
GRANT CONNECT ON DATABASE legacykeep_relationship TO relationship_service;
GRANT USAGE ON SCHEMA public TO relationship_service;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO relationship_service;
```

### Application Security
```bash
# Run as non-root user
sudo useradd -r -s /bin/false legacykeep
sudo chown -R legacykeep:legacykeep /opt/legacykeep/relationship-service
```

### Network Security
```bash
# Firewall rules
sudo ufw allow 8083/tcp
sudo ufw allow from 192.168.1.0/24 to any port 8083
```

## Performance Tuning

### JVM Tuning
```bash
# Production JVM settings
export JAVA_OPTS="-server -Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Database Tuning
```sql
-- PostgreSQL configuration
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
```

## Rollback Procedures

### Application Rollback
```bash
# Stop current service
sudo systemctl stop relationship-service

# Deploy previous version
cp relationship-service-0.9.0.jar /opt/legacykeep/relationship-service/

# Start service
sudo systemctl start relationship-service
```

### Database Rollback
```bash
# Restore from backup
psql -h localhost -U lohithsurisetti -d legacykeep_relationship < backup_previous.sql
```

---

**Last Updated**: September 13, 2025
**Version**: 1.0.0
**Deployment Status**: ✅ TESTED AND VERIFIED
