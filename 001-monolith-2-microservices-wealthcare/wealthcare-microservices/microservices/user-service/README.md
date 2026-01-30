# User Service

User authentication and management microservice for Wealthcare application.

## Overview

The User Service handles:
- User authentication with JWT tokens
- User profile management
- Role-based access control (Business Manager, Wealth Manager, Customer)
- User CRUD operations

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: H2 (file-based)
- **Security**: Spring Security + JWT
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Validate JWT token
- `GET /api/auth/user-info` - Get user info from token

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `PUT /api/users/{userId}/profile` - Update user profile ID

## Running Locally

### Prerequisites
- Java 17
- Maven 3.9+

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

The service will start on port 8081.

### Access Swagger UI
```
http://localhost:8081/swagger-ui.html
```

### Access H2 Console
```
http://localhost:8081/h2-console
JDBC URL: jdbc:h2:file:./data/user-db
Username: sa
Password: (leave empty)
```

## Docker

### Build Image
```bash
docker build -t wealthcare/user-service:1.0.0 .
```

### Run Container
```bash
docker run -p 8081:8081 \
  -v $(pwd)/data:/app/data \
  wealthcare/user-service:1.0.0
```

## Kubernetes Deployment

### Deploy to Kubernetes
```bash
# Create namespace
kubectl create namespace wealthcare

# Apply manifests
kubectl apply -f k8s/deployment.yaml

# Check status
kubectl get pods -n wealthcare
kubectl get svc -n wealthcare
```

### Access Service
```bash
# Port forward
kubectl port-forward -n wealthcare svc/user-service 8081:8081

# Test
curl http://localhost:8081/actuator/health
```

## Sample Users

All passwords are the same as the username (BCrypt encoded):

### Business Managers
- harry / harry
- charlie / charlie
- oliver / oliver

### Wealth Managers
- jerald / jerald
- charlotte / charlotte
- mia / mia
- tod / tod
- sophia / sophia
- sam / sam

### Customers
- william / william
- sandy / sandy
- david / david
- richard / richard
- emma / emma
- tom / tom

## Testing

### Login Example
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "william",
    "password": "william"
  }'
```

### Get Users (requires authentication)
```bash
curl -X GET http://localhost:8081/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/user-db
  
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours
```

## Health Checks

- Liveness: `GET /actuator/health/liveness`
- Readiness: `GET /actuator/health/readiness`
- Full Health: `GET /actuator/health`

## Monitoring

Prometheus metrics available at:
```
GET /actuator/prometheus
```

## Development

### Project Structure
```
src/main/java/com/gan/wcare/user/
├── controller/          # REST controllers
├── dto/                 # Data transfer objects
├── model/               # JPA entities
├── repository/          # Spring Data repositories
├── security/            # Security configuration
├── service/             # Business logic
└── UserServiceApplication.java
```

### Adding New Features

1. Create entity in `model/`
2. Create repository in `repository/`
3. Create service in `service/`
4. Create controller in `controller/`
5. Add tests

## Troubleshooting

### Database Issues
- Check H2 console for data
- Verify file permissions for `./data` directory
- Check logs for SQL errors

### Authentication Issues
- Verify JWT secret configuration
- Check token expiration
- Validate user credentials

### Connection Issues
- Verify port 8081 is available
- Check firewall settings
- Review application logs

## License

Apache-2.0