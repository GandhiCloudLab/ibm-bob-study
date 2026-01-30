# Investment Service

Investment portfolio management microservice for the Wealthcare application.

## Overview

The Investment Service manages customer investment portfolios, including stocks, bonds, mutual funds, ETFs, real estate, gold, and cryptocurrency investments. It provides comprehensive portfolio tracking, performance analysis, and integration with financial goals.

## Features

- **Investment Management**: Create, read, update, and delete investments
- **Investment Types**: Support for multiple asset classes
  - Stocks
  - Bonds
  - Mutual Funds
  - ETFs
  - Real Estate
  - Gold
  - Cryptocurrency
  - Other
- **Portfolio Tracking**: Track investment performance and gains/losses
- **Portfolio Summary**: Aggregated portfolio analytics by customer
- **Goal Integration**: Link investments to financial goals
- **Price Updates**: Update current market prices for investments
- **Customer Integration**: Validate customer existence via Customer Service
- **Goal Validation**: Validate goal existence via Goal Service
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
  port: 8084

spring:
  application:
    name: investment-service
  datasource:
    url: jdbc:h2:file:./data/investment-db
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
  goal-service:
    url: http://localhost:8083
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod)
- `CUSTOMER_SERVICE_URL`: Customer Service endpoint URL
- `GOAL_SERVICE_URL`: Goal Service endpoint URL

## Building the Application

### Maven Build

```bash
mvn clean package
```

### Docker Build

```bash
docker build -t wealthcare/investment-service:1.0.0 .
```

## Running the Application

### Local Development

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8084`

### Docker Run

```bash
docker run -p 8084:8084 \
  -e CUSTOMER_SERVICE_URL=http://customer-service:8082 \
  -e GOAL_SERVICE_URL=http://goal-service:8083 \
  wealthcare/investment-service:1.0.0
```

### Docker Compose

```bash
cd microservices
docker-compose up investment-service
```

## API Endpoints

### Investment Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/investments` | Get all investments |
| GET | `/api/investments/{id}` | Get investment by ID |
| GET | `/api/investments/customer/{customerId}` | Get investments by customer ID |
| GET | `/api/investments/customer/{customerId}/active` | Get active investments by customer |
| GET | `/api/investments/goal/{goalId}` | Get investments by goal ID |
| GET | `/api/investments/type/{investmentType}` | Get investments by type |
| GET | `/api/investments/customer/{customerId}/portfolio-summary` | Get portfolio summary |
| POST | `/api/investments` | Create new investment |
| PUT | `/api/investments/{id}` | Update investment |
| PATCH | `/api/investments/{id}/current-price` | Update current price |
| PATCH | `/api/investments/{investmentId}/link-goal/{goalId}` | Link investment to goal |
| DELETE | `/api/investments/{id}` | Delete investment |

### Health & Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |
| GET | `/actuator/health/liveness` | Liveness probe |
| GET | `/actuator/health/readiness` | Readiness probe |
| GET | `/actuator/info` | Application info |
| GET | `/actuator/metrics` | Metrics |

### API Documentation

- **Swagger UI**: http://localhost:8084/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8084/v3/api-docs

## Data Model

### Investment Entity

```java
{
  "id": 1,
  "customerId": 1,
  "goalId": 1,
  "investmentName": "HDFC Bank Shares",
  "investmentType": "STOCKS",
  "symbol": "HDFCBANK",
  "quantity": 100.00,
  "purchasePrice": 1500.00,
  "currentPrice": 1650.00,
  "purchaseDate": "2023-01-15",
  "maturityDate": null,
  "description": "Blue chip banking stock",
  "status": "ACTIVE",
  "createdDate": "2023-01-15",
  "lastUpdatedDate": "2024-06-15",
  "totalInvestment": 150000.00,
  "currentValue": 165000.00,
  "gainLoss": 15000.00,
  "gainLossPercentage": 10.00
}
```

### Investment Types

- `STOCKS`: Equity shares
- `BONDS`: Fixed income securities
- `MUTUAL_FUNDS`: Mutual fund units
- `ETF`: Exchange Traded Funds
- `REAL_ESTATE`: Real estate investments
- `GOLD`: Gold investments (physical/ETF/bonds)
- `CRYPTO`: Cryptocurrency
- `OTHER`: Other investment types

### Investment Status

- `ACTIVE`: Investment is currently held
- `SOLD`: Investment has been sold
- `MATURED`: Investment has matured

## Sample Data

The service includes 27 pre-loaded sample investments across 6 customers:

- **Customer 1 (Rajesh Kumar)**: 4 investments (Stocks, Mutual Funds, PPF)
- **Customer 2 (Priya Sharma)**: 4 investments (Stocks, Mutual Funds, Gold ETF, FD)
- **Customer 3 (Amit Patel)**: 3 investments (Stocks, ETF, Real Estate)
- **Customer 4 (Sneha Reddy)**: 3 investments (Mutual Funds, NSC)
- **Customer 5 (Vikram Singh)**: 4 investments (Stocks, Mutual Funds, Crypto, Bonds)
- **Customer 6 (Ananya Iyer)**: 4 investments (Stocks, Mutual Funds, Gold Bonds, FD)
- **Historical**: 3 sold/matured investments

## API Usage Examples

### Create Investment

```bash
curl -X POST http://localhost:8084/api/investments \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "goalId": 1,
    "investmentName": "Apple Inc Shares",
    "investmentType": "STOCKS",
    "symbol": "AAPL",
    "quantity": 50,
    "purchasePrice": 150.00,
    "currentPrice": 175.00,
    "purchaseDate": "2024-01-15",
    "description": "US tech stock investment",
    "status": "ACTIVE"
  }'
```

### Get Customer Investments

```bash
curl http://localhost:8084/api/investments/customer/1
```

### Get Portfolio Summary

```bash
curl http://localhost:8084/api/investments/customer/1/portfolio-summary
```

Response:
```json
{
  "customerId": 1,
  "totalInvestments": 4,
  "totalInvested": 500000.00,
  "currentValue": 550000.00,
  "totalGainLoss": 50000.00,
  "totalGainLossPercentage": 10.00,
  "investmentsByType": {
    "STOCKS": 315000.00,
    "MUTUAL_FUNDS": 36250.00,
    "BONDS": 165000.00
  },
  "countByType": {
    "STOCKS": 2,
    "MUTUAL_FUNDS": 1,
    "BONDS": 1
  }
}
```

### Update Current Price

```bash
curl -X PATCH "http://localhost:8084/api/investments/1/current-price?currentPrice=1700.00"
```

### Link Investment to Goal

```bash
curl -X PATCH http://localhost:8084/api/investments/5/link-goal/2
```

### Update Investment

```bash
curl -X PUT http://localhost:8084/api/investments/1 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 150,
    "currentPrice": 1680.00,
    "description": "Increased position in HDFC Bank"
  }'
```

## Database

### H2 Console

Access the H2 console at: http://localhost:8084/h2-console

- **JDBC URL**: `jdbc:h2:file:./data/investment-db`
- **Username**: `sa`
- **Password**: (empty)

### Schema

The database schema is automatically created by JPA:

```sql
CREATE TABLE wc_investment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  goal_id BIGINT,
  investment_name VARCHAR(100) NOT NULL,
  investment_type VARCHAR(50) NOT NULL,
  symbol VARCHAR(20) NOT NULL,
  quantity DECIMAL(15,4) NOT NULL,
  purchase_price DECIMAL(15,2) NOT NULL,
  current_price DECIMAL(15,2),
  purchase_date DATE NOT NULL,
  maturity_date DATE,
  description VARCHAR(500),
  status VARCHAR(20) DEFAULT 'ACTIVE',
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
kubectl get pods -n wealthcare -l app=investment-service
kubectl get svc -n wealthcare investment-service
```

### Access Service

```bash
kubectl port-forward -n wealthcare svc/investment-service 8084:8084
```

### View Logs

```bash
kubectl logs -n wealthcare -l app=investment-service -f
```

## Monitoring

### Health Checks

```bash
# Overall health
curl http://localhost:8084/actuator/health

# Liveness probe
curl http://localhost:8084/actuator/health/liveness

# Readiness probe
curl http://localhost:8084/actuator/health/readiness
```

### Metrics

```bash
# All metrics
curl http://localhost:8084/actuator/metrics

# Specific metric
curl http://localhost:8084/actuator/metrics/jvm.memory.used
```

## Integration with Other Services

### Customer Service

The Investment Service integrates with Customer Service to:
- Validate customer existence when creating investments
- Ensure data consistency

Configuration:
```yaml
services:
  customer-service:
    url: http://customer-service:8082
```

### Goal Service

The Investment Service integrates with Goal Service to:
- Validate goal existence when linking investments
- Support goal-based investment tracking

Configuration:
```yaml
services:
  goal-service:
    url: http://goal-service:8083
```

## Business Logic

### Portfolio Calculations

The service automatically calculates:

1. **Total Investment**: `purchasePrice × quantity`
2. **Current Value**: `currentPrice × quantity` (or total investment if no current price)
3. **Gain/Loss**: `currentValue - totalInvestment`
4. **Gain/Loss %**: `(gainLoss / totalInvestment) × 100`

### Portfolio Summary

Aggregates all active investments for a customer:
- Total number of investments
- Total amount invested
- Current portfolio value
- Total gain/loss (amount and percentage)
- Breakdown by investment type
- Count of investments by type

## Error Handling

The service provides comprehensive error handling:

- **400 Bad Request**: Invalid input data
- **404 Not Found**: Investment, customer, or goal not found
- **500 Internal Server Error**: Server-side errors

Example error response:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Investment not found with id: 999",
  "path": "/api/investments/999"
}
```

## Development

### Project Structure

```
investment-service/
├── src/
│   ├── main/
│   │   ├── java/com/gan/wcare/investment/
│   │   │   ├── InvestmentServiceApplication.java
│   │   │   ├── model/
│   │   │   │   └── Investment.java
│   │   │   ├── dto/
│   │   │   │   ├── InvestmentDTO.java
│   │   │   │   └── PortfolioSummaryDTO.java
│   │   │   ├── repository/
│   │   │   │   └── InvestmentRepository.java
│   │   │   ├── service/
│   │   │   │   └── InvestmentService.java
│   │   │   └── controller/
│   │   │       └── InvestmentController.java
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

1. Update the Investment entity if needed
2. Add new methods to InvestmentRepository
3. Implement business logic in InvestmentService
4. Add REST endpoints in InvestmentController
5. Update tests
6. Update API documentation

## Testing

### Manual Testing

Use Swagger UI at http://localhost:8084/swagger-ui.html for interactive testing.

### cURL Testing

```bash
# Health check
curl http://localhost:8084/actuator/health

# Get all investments
curl http://localhost:8084/api/investments

# Get customer investments
curl http://localhost:8084/api/investments/customer/1

# Get portfolio summary
curl http://localhost:8084/api/investments/customer/1/portfolio-summary

# Create investment
curl -X POST http://localhost:8084/api/investments \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"investmentName":"Test Stock","investmentType":"STOCKS","symbol":"TEST","quantity":100,"purchasePrice":50.00,"currentPrice":55.00,"purchaseDate":"2024-01-15","status":"ACTIVE"}'
```

## Troubleshooting

### Service Won't Start

1. Check if port 8084 is available
2. Verify Java 17 is installed
3. Check Customer Service and Goal Service are running (if integration is required)
4. Review logs for errors

### Database Issues

1. Check H2 console connection
2. Verify data directory permissions
3. Check database file location: `./data/investment-db.mv.db`

### Integration Issues

1. Verify Customer Service and Goal Service URLs
2. Check network connectivity between services
3. Review service logs for connection errors

## Performance Considerations

- Database connection pooling is configured
- JPA second-level cache can be enabled for frequently accessed data
- Consider pagination for large result sets
- Monitor memory usage with Actuator metrics
- Use indexes on frequently queried fields (customerId, goalId, symbol)

## Security Considerations

- Input validation on all endpoints
- SQL injection prevention via JPA
- Consider adding authentication/authorization
- Secure inter-service communication
- Environment-specific configurations
- Sensitive data encryption for financial information

## Future Enhancements

- Real-time price updates via external APIs
- Automated portfolio rebalancing suggestions
- Tax calculation and reporting
- Dividend tracking
- Transaction history
- Performance benchmarking against market indices
- Risk analysis and portfolio optimization
- Integration with trading platforms
- Mobile app support
- Email/SMS notifications for price alerts
- Advanced analytics and reporting

## Support

For issues or questions:
1. Check the logs: `kubectl logs -n wealthcare -l app=investment-service`
2. Review the API documentation: http://localhost:8084/swagger-ui.html
3. Check health endpoints: http://localhost:8084/actuator/health

## License

Copyright © 2024 Wealthcare. All rights reserved.