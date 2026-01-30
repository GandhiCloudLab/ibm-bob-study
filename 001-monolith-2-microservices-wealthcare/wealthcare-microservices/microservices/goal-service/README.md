# Goal Service

Financial goal management microservice for the Wealthcare application.

## Overview

The Goal Service manages financial goals for customers, including goal creation, tracking, progress monitoring, and achievement calculations. It integrates with the Customer Service to validate customer and wealth manager relationships.

## Features

- **Goal Management**: Create, read, update, and delete financial goals
- **Goal Types**: Support for multiple goal types (RETIREMENT, EDUCATION, HOME, VACATION, EMERGENCY_FUND, OTHER)
- **Progress Tracking**: Calculate goal progress based on current vs target amounts
- **Status Management**: Track goal status (ACTIVE, ACHIEVED, CANCELLED)
- **Customer Integration**: Validate customer existence via Customer Service
- **RESTful API**: Complete CRUD operations with validation
- **H2 Database**: Embedded file-based database for data persistence
- **Health Checks**: Actuator endpoints for monitoring
- **API Documentation**: Swagger UI for interactive API testing

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **H2 Database**
- **Spring WebFlux** (WebClient for inter-service communication)
- **SpringDoc OpenAPI** (Swagger UI)
- **Spring Boot Actuator**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional, for containerization)
- Kubernetes (optional, for orchestration)

## Configuration

### Application Properties

Key configuration in `src/main/resources/application.yml`:

```yaml
server:
  port: 8083

spring:
  application:
    name: goal-service
  datasource:
    url: jdbc:h2:file:./data/goal-db
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

services:
  customer-service:
    url: http://localhost:8082
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod)
- `CUSTOMER_SERVICE_URL`: Customer Service endpoint URL

## Building the Application

### Maven Build

```bash
mvn clean package
```

### Docker Build

```bash
docker build -t wealthcare/goal-service:1.0.0 .
```

## Running the Application

### Local Development

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8083`

### Docker Run

```bash
docker run -p 8083:8083 \
  -e CUSTOMER_SERVICE_URL=http://customer-service:8082 \
  wealthcare/goal-service:1.0.0
```

### Docker Compose

```bash
cd microservices
docker-compose up goal-service
```

## API Endpoints

### Goal Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/goals` | Get all goals |
| GET | `/api/goals/{id}` | Get goal by ID |
| GET | `/api/goals/customer/{customerId}` | Get goals by customer ID |
| GET | `/api/goals/wealth-manager/{wmId}` | Get goals by wealth manager ID |
| POST | `/api/goals` | Create new goal |
| PUT | `/api/goals/{id}` | Update goal |
| DELETE | `/api/goals/{id}` | Delete goal |
| GET | `/api/goals/{id}/progress` | Get goal progress |

### Health & Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |
| GET | `/actuator/health/liveness` | Liveness probe |
| GET | `/actuator/health/readiness` | Readiness probe |
| GET | `/actuator/info` | Application info |
| GET | `/actuator/metrics` | Metrics |

### API Documentation

- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8083/v3/api-docs

## Data Model

### Goal Entity

```java
{
  "id": 1,
  "customerId": 1,
  "wealthManagerId": 1,
  "goalName": "Retirement Fund",
  "goalType": "RETIREMENT",
  "targetAmount": 1000000.00,
  "currentAmount": 250000.00,
  "targetDate": "2045-12-31",
  "status": "ACTIVE",
  "description": "Build retirement corpus",
  "createdDate": "2024-01-15",
  "lastUpdatedDate": "2024-06-15"
}
```

### Goal Types

- `RETIREMENT`: Retirement planning
- `EDUCATION`: Education funding
- `HOME`: Home purchase
- `VACATION`: Vacation planning
- `EMERGENCY_FUND`: Emergency fund
- `OTHER`: Other financial goals

### Goal Status

- `ACTIVE`: Goal is active and being tracked
- `ACHIEVED`: Goal has been achieved
- `CANCELLED`: Goal has been cancelled

## Sample Data

The service includes 12 pre-loaded sample goals:

- 4 Retirement goals
- 3 Education goals
- 2 Home purchase goals
- 1 Vacation goal
- 1 Emergency fund
- 1 Other goal

## API Usage Examples

### Create Goal

```bash
curl -X POST http://localhost:8083/api/goals \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "wealthManagerId": 1,
    "goalName": "Dream Home",
    "goalType": "HOME",
    "targetAmount": 500000.00,
    "currentAmount": 50000.00,
    "targetDate": "2028-12-31",
    "status": "ACTIVE",
    "description": "Save for down payment"
  }'
```

### Get Customer Goals

```bash
curl http://localhost:8083/api/goals/customer/1
```

### Get Goal Progress

```bash
curl http://localhost:8083/api/goals/1/progress
```

Response:
```json
{
  "goalId": 1,
  "goalName": "Retirement Fund",
  "targetAmount": 1000000.00,
  "currentAmount": 250000.00,
  "progressPercentage": 25.0,
  "remainingAmount": 750000.00,
  "status": "ACTIVE"
}
```

### Update Goal

```bash
curl -X PUT http://localhost:8083/api/goals/1 \
  -H "Content-Type: application/json" \
  -d '{
    "currentAmount": 300000.00
  }'
```

## Database

### H2 Console

Access the H2 console at: http://localhost:8083/h2-console

- **JDBC URL**: `jdbc:h2:file:./data/goal-db`
- **Username**: `sa`
- **Password**: (empty)

### Schema

The database schema is automatically created by JPA:

```sql
CREATE TABLE wc_goal (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  wealth_manager_id BIGINT NOT NULL,
  goal_name VARCHAR(100) NOT NULL,
  goal_type VARCHAR(20) NOT NULL,
  target_amount DECIMAL(15,2) NOT NULL,
  current_amount DECIMAL(15,2) DEFAULT 0,
  target_date DATE NOT NULL,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  description VARCHAR(500),
  created_date DATE NOT NULL,
  last_updated_date DATE
);
```

## Kubernetes Deployment

### Deploy to Kubernetes

```bash
kubectl apply -f k8s/deployment.yaml
```

### Verify Deployment

```bash
kubectl get pods -n wealthcare -l app=goal-service
kubectl get svc -n wealthcare goal-service
```

### Access Service

```bash
kubectl port-forward -n wealthcare svc/goal-service 8083:8083
```

### View Logs

```bash
kubectl logs -n wealthcare -l app=goal-service -f
```

## Monitoring

### Health Checks

```bash
# Overall health
curl http://localhost:8083/actuator/health

# Liveness probe
curl http://localhost:8083/actuator/health/liveness

# Readiness probe
curl http://localhost:8083/actuator/health/readiness
```

### Metrics

```bash
# All metrics
curl http://localhost:8083/actuator/metrics

# Specific metric
curl http://localhost:8083/actuator/metrics/jvm.memory.used
```

## Integration with Other Services

### Customer Service

The Goal Service integrates with Customer Service to:
- Validate customer existence when creating goals
- Verify wealth manager assignments
- Fetch customer details for goal validation

Configuration:
```yaml
services:
  customer-service:
    url: http://customer-service:8082
```

## Error Handling

The service provides comprehensive error handling:

- **400 Bad Request**: Invalid input data
- **404 Not Found**: Goal or customer not found
- **500 Internal Server Error**: Server-side errors

Example error response:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Goal not found with id: 999",
  "path": "/api/goals/999"
}
```

## Development

### Project Structure

```
goal-service/
├── src/
│   ├── main/
│   │   ├── java/com/gan/wcare/goal/
│   │   │   ├── GoalServiceApplication.java
│   │   │   ├── model/
│   │   │   │   └── Goal.java
│   │   │   ├── dto/
│   │   │   │   ├── GoalDTO.java
│   │   │   │   └── GoalProgressDTO.java
│   │   │   ├── repository/
│   │   │   │   └── GoalRepository.java
│   │   │   ├── service/
│   │   │   │   └── GoalService.java
│   │   │   └── controller/
│   │   │       └── GoalController.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql
├── Dockerfile
├── k8s/
│   └── deployment.yaml
├── pom.xml
└── README.md
```

### Adding New Features

1. Update the Goal entity if needed
2. Add new methods to GoalRepository
3. Implement business logic in GoalService
4. Add REST endpoints in GoalController
5. Update tests
6. Update API documentation

## Testing

### Manual Testing

Use Swagger UI at http://localhost:8083/swagger-ui.html for interactive testing.

### cURL Testing

```bash
# Health check
curl http://localhost:8083/actuator/health

# Get all goals
curl http://localhost:8083/api/goals

# Get customer goals
curl http://localhost:8083/api/goals/customer/1

# Create goal
curl -X POST http://localhost:8083/api/goals \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"wealthManagerId":1,"goalName":"Test Goal","goalType":"OTHER","targetAmount":10000,"currentAmount":0,"targetDate":"2025-12-31","status":"ACTIVE"}'
```

## Troubleshooting

### Service Won't Start

1. Check if port 8083 is available
2. Verify Java 17 is installed
3. Check Customer Service is running (if integration is required)
4. Review logs for errors

### Database Issues

1. Check H2 console connection
2. Verify data directory permissions
3. Check database file location: `./data/goal-db.mv.db`

### Integration Issues

1. Verify Customer Service URL configuration
2. Check network connectivity between services
3. Review service logs for connection errors

## Performance Considerations

- Database connection pooling is configured
- JPA second-level cache can be enabled for frequently accessed data
- Consider pagination for large result sets
- Monitor memory usage with Actuator metrics

## Security Considerations

- Input validation on all endpoints
- SQL injection prevention via JPA
- Consider adding authentication/authorization
- Secure inter-service communication
- Environment-specific configurations

## Future Enhancements

- Add goal milestone tracking
- Implement goal recommendations
- Add goal sharing between family members
- Integrate with Investment Service for automatic progress updates
- Add goal achievement notifications
- Implement goal templates
- Add goal analytics and reporting

## Support

For issues or questions:
1. Check the logs: `kubectl logs -n wealthcare -l app=goal-service`
2. Review the API documentation: http://localhost:8083/swagger-ui.html
3. Check health endpoints: http://localhost:8083/actuator/health

## License

Copyright © 2024 Wealthcare. All rights reserved.