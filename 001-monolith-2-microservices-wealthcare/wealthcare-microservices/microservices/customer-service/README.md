# Customer Service

Customer profile management microservice for Wealthcare application.

## Overview

The Customer Service handles:
- Customer profile management
- Family information (spouse and children)
- Wealth manager assignments
- Customer CRUD operations
- Integration with User Service

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: H2 (file-based)
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Inter-service Communication**: WebClient

## API Endpoints

### Customer Management
- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/user/{userId}` - Get customer by user ID
- `GET /api/customers/wealth-manager/{wealthManagerId}` - Get customers by wealth manager
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

## Running Locally

### Prerequisites
- Java 17
- Maven 3.9+
- User Service running on port 8081 (for inter-service communication)

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

The service will start on port 8082.

### Access Swagger UI
```
http://localhost:8082/swagger-ui.html
```

### Access H2 Console
```
http://localhost:8082/h2-console
JDBC URL: jdbc:h2:file:./data/customer-db
Username: sa
Password: (leave empty)
```

## Docker

### Build Image
```bash
docker build -t wealthcare/customer-service:1.0.0 .
```

### Run Container
```bash
docker run -p 8082:8082 \
  -e USER_SERVICE_URL=http://user-service:8081 \
  -v $(pwd)/data:/app/data \
  wealthcare/customer-service:1.0.0
```

## Kubernetes Deployment

### Deploy to Kubernetes
```bash
# Create namespace (if not exists)
kubectl create namespace wealthcare

# Apply manifests
kubectl apply -f k8s/deployment.yaml

# Check status
kubectl get pods -n wealthcare -l app=customer-service
kubectl get svc -n wealthcare
```

### Access Service
```bash
# Port forward
kubectl port-forward -n wealthcare svc/customer-service 8082:8082

# Test
curl http://localhost:8082/actuator/health
```

## Sample Data

The service comes with 6 pre-loaded customers:

1. **William Joseph** (User ID: 10, WM ID: 1)
   - Male, 32 years, Chicago
   - Married with 1 child

2. **Sandy Thomas** (User ID: 11, WM ID: 1)
   - Male, 38 years, New York
   - Married with 1 child

3. **David John** (User ID: 12, WM ID: 2)
   - Male, 39 years, Chicago
   - Married with 2 children

4. **Richard Charles** (User ID: 13, WM ID: 3)
   - Male, 35 years, New York
   - Married with 2 children

5. **Emma Francis** (User ID: 14, WM ID: 4)
   - Female, 32 years, Chicago
   - Married with 1 child

6. **Tom Alex** (User ID: 15, WM ID: 6)
   - Male, 32 years, Chicago
   - Married with 1 child

## Testing

### Get All Customers
```bash
curl -X GET http://localhost:8082/api/customers
```

### Get Customer by ID
```bash
curl -X GET http://localhost:8082/api/customers/1
```

### Get Customers by Wealth Manager
```bash
curl -X GET http://localhost:8082/api/customers/wealth-manager/1
```

### Create Customer
```bash
curl -X POST http://localhost:8082/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 16,
    "wealthManagerId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "age": 35,
    "avgIncome": 1200000,
    "married": true,
    "city": "Boston",
    "phone": "1234567890",
    "emailId": "john.doe@example.com",
    "country": "US",
    "zipCode": "02101"
  }'
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/customer-db

services:
  user-service:
    url: http://user-service:8081
```

## Inter-Service Communication

The Customer Service communicates with:

### User Service
- **Purpose**: Update user profile with customer ID after creation
- **Endpoint**: `PUT /api/users/{userId}/profile`
- **Method**: WebClient (non-blocking)

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
src/main/java/com/gan/wcare/customer/
├── controller/          # REST controllers
├── dto/                 # Data transfer objects
├── model/               # JPA entities
├── repository/          # Spring Data repositories
├── service/             # Business logic
└── CustomerServiceApplication.java
```

### Customer Entity Fields

**Personal Information:**
- firstName, lastName, gender, age, avgIncome

**Marital Status:**
- married (boolean)

**Spouse Information:**
- spouseFirstName, spouseLastName, spouseGender, spouseAge, spouseAvgIncome

**Children Information:**
- noOfChildren
- child1FirstName, child1LastName, child1Gender, child1Age
- child2FirstName, child2LastName, child2Gender, child2Age

**Contact Information:**
- city, phone, emailId, country, zipCode

**Relationships:**
- userId (reference to User Service)
- wealthManagerId (reference to Wealth Manager)

## Error Handling

The service handles common errors:
- Customer not found (404)
- Duplicate user ID (400)
- Duplicate email (400)
- Invalid data (400)
- User service communication failures (logged, non-blocking)

## Troubleshooting

### Database Issues
- Check H2 console for data
- Verify file permissions for `./data` directory
- Check logs for SQL errors

### Inter-Service Communication Issues
- Verify User Service is running
- Check USER_SERVICE_URL configuration
- Review WebClient logs

### Connection Issues
- Verify port 8082 is available
- Check firewall settings
- Review application logs

## License

Apache-2.0