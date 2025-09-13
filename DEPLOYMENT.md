# Relationship Service Deployment Guide

## Overview
This guide covers the deployment of the Relationship Service in different environments, including development, staging, and production.

## Prerequisites

### System Requirements
- **Java**: JDK 17 or higher
- **Memory**: Minimum 512MB, Recommended 1GB
- **Storage**: Minimum 1GB for application and logs
- **Network**: Port 8083 available

### Dependencies
- **PostgreSQL**: 14+ with `relationship_db` database
- **Redis**: For caching (optional)
- **Kafka**: For event publishing (optional)

## Environment Setup

### Development Environment

#### Local Development
```bash
# 1. Clone repository
git clone <repository-url>
cd relationship-service

# 2. Set up database
createdb relationship_db
createuser -s legacykeep

# 3. Configure application
cp src/main/resources/application.properties.example src/main/resources/application.properties

# 4. Run application
mvn spring-boot:run
```

#### Docker Development
```bash
# 1. Build image
docker build -t legacykeep/relationship-service:dev .

# 2. Run with docker-compose
docker-compose -f docker-compose.dev.yml up -d
```

### Staging Environment

#### Configuration
```properties
# application-staging.properties
server.port=8083
server.servlet.context-path=/relationship

# Database
spring.datasource.url=jdbc:postgresql://staging-db:5432/relationship_db
spring.datasource.username=legacykeep
spring.datasource.password=${DB_PASSWORD}

# JWT
relationship.jwt.secret=${JWT_SECRET}
relationship.jwt.issuer=LegacyKeep
relationship.jwt.audience=LegacyKeep-Relationships

# Logging
logging.level.com.legacykeep=INFO
logging.level.org.springframework=WARN
```

#### Deployment
```bash
# 1. Build for staging
mvn clean package -Pstaging

# 2. Deploy to staging
kubectl apply -f k8s/staging/
```

### Production Environment

#### Configuration
```properties
# application-production.properties
server.port=8083
server.servlet.context-path=/relationship

# Database
spring.datasource.url=jdbc:postgresql://prod-db-cluster:5432/relationship_db
spring.datasource.username=legacykeep
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20

# JWT
relationship.jwt.secret=${JWT_SECRET}
relationship.jwt.issuer=LegacyKeep
relationship.jwt.audience=LegacyKeep-Relationships

# Logging
logging.level.com.legacykeep=WARN
logging.level.org.springframework=ERROR
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## Docker Deployment

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy application
COPY target/relationship-service-*.jar app.jar

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/relationship/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'

services:
  relationship-service:
    build: .
    ports:
      - "8083:8083"
    environment:
      - DB_HOST=postgres
      - DB_NAME=relationship_db
      - DB_USERNAME=legacykeep
      - DB_PASSWORD=password
      - JWT_SECRET=legacykeep-jwt-secret-key-change-in-production
    depends_on:
      - postgres
      - redis
    networks:
      - legacykeep-network

  postgres:
    image: postgres:14
    environment:
      - POSTGRES_DB=relationship_db
      - POSTGRES_USER=legacykeep
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - legacykeep-network

  redis:
    image: redis:7-alpine
    networks:
      - legacykeep-network

volumes:
  postgres_data:

networks:
  legacykeep-network:
    driver: bridge
```

## Kubernetes Deployment

### Namespace
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: legacykeep
```

### ConfigMap
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: relationship-service-config
  namespace: legacykeep
data:
  application.properties: |
    server.port=8083
    server.servlet.context-path=/relationship
    spring.datasource.url=jdbc:postgresql://postgres-service:5432/relationship_db
    spring.datasource.username=legacykeep
    relationship.jwt.issuer=LegacyKeep
    relationship.jwt.audience=LegacyKeep-Relationships
    logging.level.com.legacykeep=INFO
```

### Secret
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: relationship-service-secrets
  namespace: legacykeep
type: Opaque
data:
  db-password: <base64-encoded-password>
  jwt-secret: <base64-encoded-jwt-secret>
```

### Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: relationship-service
  namespace: legacykeep
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
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: relationship-service-secrets
              key: db-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: relationship-service-secrets
              key: jwt-secret
        volumeMounts:
        - name: config
          mountPath: /app/config
        livenessProbe:
          httpGet:
            path: /relationship/actuator/health/liveness
            port: 8083
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /relationship/actuator/health/readiness
            port: 8083
          initialDelaySeconds: 30
          periodSeconds: 10
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
      volumes:
      - name: config
        configMap:
          name: relationship-service-config
```

### Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: relationship-service
  namespace: legacykeep
spec:
  selector:
    app: relationship-service
  ports:
  - port: 8083
    targetPort: 8083
    protocol: TCP
  type: ClusterIP
```

### Ingress
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: relationship-service-ingress
  namespace: legacykeep
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - api.legacykeep.com
    secretName: legacykeep-tls
  rules:
  - host: api.legacykeep.com
    http:
      paths:
      - path: /relationship
        pathType: Prefix
        backend:
          service:
            name: relationship-service
            port:
              number: 8083
```

## CI/CD Pipeline

### GitHub Actions
```yaml
name: Deploy Relationship Service

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn test
    - name: Build application
      run: mvn clean package

  deploy-staging:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - uses: actions/checkout@v3
    - name: Build Docker image
      run: docker build -t legacykeep/relationship-service:staging .
    - name: Deploy to staging
      run: kubectl apply -f k8s/staging/

  deploy-production:
    needs: deploy-staging
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - uses: actions/checkout@v3
    - name: Build Docker image
      run: docker build -t legacykeep/relationship-service:latest .
    - name: Deploy to production
      run: kubectl apply -f k8s/production/
```

## Database Migration

### Flyway Configuration
```properties
# Flyway settings
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

### Migration Commands
```bash
# Check migration status
mvn flyway:info

# Run migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate

# Clean database (development only)
mvn flyway:clean
```

## Monitoring and Logging

### Health Checks
```bash
# Application health
curl http://localhost:8083/relationship/health

# Liveness probe
curl http://localhost:8083/relationship/actuator/health/liveness

# Readiness probe
curl http://localhost:8083/relationship/actuator/health/readiness
```

### Logging Configuration
```properties
# Logging levels
logging.level.com.legacykeep=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN

# Log file configuration
logging.file.name=logs/relationship-service.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Log rotation
logging.logback.rollingpolicy.max-file-size=100MB
logging.logback.rollingpolicy.max-history=30
```

### Metrics
```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

## Security

### Network Security
- **Firewall**: Only allow necessary ports (8083)
- **SSL/TLS**: Enable HTTPS in production
- **VPN**: Use VPN for internal service communication

### Application Security
- **JWT**: Secure JWT secret management
- **Database**: Encrypted connections and credentials
- **Secrets**: Use Kubernetes secrets or external secret management

### Environment Variables
```bash
# Required environment variables
DB_HOST=localhost
DB_PORT=5432
DB_NAME=relationship_db
DB_USERNAME=legacykeep
DB_PASSWORD=secure-password
JWT_SECRET=secure-jwt-secret-key
JWT_ISSUER=LegacyKeep
JWT_AUDIENCE=LegacyKeep-Relationships
```

## Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check logs
kubectl logs -f deployment/relationship-service

# Check configuration
kubectl describe configmap relationship-service-config

# Check secrets
kubectl describe secret relationship-service-secrets
```

#### Database Connection Issues
```bash
# Test database connection
kubectl exec -it deployment/relationship-service -- curl http://localhost:8083/relationship/health

# Check database status
kubectl exec -it deployment/postgres -- psql -U legacykeep -d relationship_db -c "SELECT 1;"
```

#### Performance Issues
```bash
# Check resource usage
kubectl top pods -l app=relationship-service

# Check metrics
curl http://localhost:8083/relationship/actuator/metrics
```

### Rollback Procedures
```bash
# Rollback deployment
kubectl rollout undo deployment/relationship-service

# Check rollout status
kubectl rollout status deployment/relationship-service

# Rollback to specific revision
kubectl rollout undo deployment/relationship-service --to-revision=2
```

## Backup and Recovery

### Database Backup
```bash
# Create backup
pg_dump -h localhost -U legacykeep relationship_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore backup
psql -h localhost -U legacykeep relationship_db < backup_20240101_120000.sql
```

### Application Backup
```bash
# Backup configuration
kubectl get configmap relationship-service-config -o yaml > config-backup.yaml

# Backup secrets
kubectl get secret relationship-service-secrets -o yaml > secrets-backup.yaml
```

## Scaling

### Horizontal Scaling
```bash
# Scale deployment
kubectl scale deployment relationship-service --replicas=5

# Auto-scaling
kubectl autoscale deployment relationship-service --cpu-percent=70 --min=3 --max=10
```

### Vertical Scaling
```yaml
# Update resource limits
resources:
  requests:
    memory: "1Gi"
    cpu: "500m"
  limits:
    memory: "2Gi"
    cpu: "1000m"
```

## Maintenance

### Regular Maintenance Tasks
- **Database cleanup**: Remove old relationship history
- **Log rotation**: Manage log file sizes
- **Security updates**: Update dependencies
- **Performance tuning**: Monitor and optimize queries

### Maintenance Windows
- **Scheduled downtime**: Plan maintenance windows
- **Blue-green deployment**: Zero-downtime deployments
- **Canary releases**: Gradual rollout of changes

## Support

### Monitoring Tools
- **Prometheus**: Metrics collection
- **Grafana**: Dashboards and visualization
- **ELK Stack**: Log aggregation and analysis
- **Jaeger**: Distributed tracing

### Alerting
- **Service down**: Immediate alert
- **High error rate**: Alert on error threshold
- **Resource usage**: Alert on resource limits
- **Database issues**: Alert on connection problems

### Contact Information
- **On-call**: 24/7 support rotation
- **Escalation**: Clear escalation procedures
- **Documentation**: Keep runbooks updated
- **Training**: Regular team training sessions
