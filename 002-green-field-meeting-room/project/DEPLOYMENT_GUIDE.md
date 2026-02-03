# Meeting Room Booking Application - IBM Code Engine Deployment Guide

## Overview

This guide provides step-by-step instructions for deploying the Meeting Room Booking application to IBM Code Engine, including both backend (Spring Boot) and frontend (React) components.

**Optimized for**: Mac M1 Pro (Apple Silicon) using Podman and IBM Container Registry

## Prerequisites

Before deploying, ensure you have:

1. **IBM Cloud Account**: Sign up at https://cloud.ibm.com
2. **IBM Cloud CLI**: Install from https://cloud.ibm.com/docs/cli
   ```bash
   curl -fsSL https://clis.cloud.ibm.com/install/osx | sh
   ```
3. **Code Engine Plugin**:
   ```bash
   ibmcloud plugin install code-engine
   ibmcloud plugin install container-registry
   ```
4. **Podman** (Docker alternative for M1 Mac):
   ```bash
   brew install podman
   podman machine init
   podman machine start
   ```

## Your IBM Cloud Configuration

- **Resource Group**: `itz-wxo-697b4e2bf2289c92dfa7cf`
- **Container Registry Namespace**: `icr-itz-3uehbja7`
- **Registry Region**: `us.icr.io` (adjust if different)

## Architecture on IBM Code Engine

```
┌─────────────────────────────────────────────────────────┐
│                    IBM Code Engine                       │
│                                                          │
│  ┌──────────────────┐         ┌──────────────────┐     │
│  │  Frontend App    │         │   Backend API    │     │
│  │  (React/Vite)    │────────▶│  (Spring Boot)   │     │
│  │  Port: 8080      │         │  Port: 8080      │     │
│  └──────────────────┘         └──────────────────┘     │
│                                        │                 │
│                                        ▼                 │
│                              ┌──────────────────┐       │
│                              │  PostgreSQL DB   │       │
│                              │  (Hyper Protect) │       │
│                              └──────────────────┘       │
└─────────────────────────────────────────────────────────┘
```

## Step 1: Prepare Container Images (M1 Mac Optimized)

### 1.1 Backend Dockerfile (ARM64 Compatible)

The backend Dockerfile is already created at `backend/Dockerfile` and is compatible with M1:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.meetingroom.MeetingRoomApplication"]
```

### 1.2 Frontend Dockerfile (ARM64 Compatible)

The frontend Dockerfile is already created at `frontend/Dockerfile`:

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 1.3 Build and Push Images Using Podman (M1 Mac)

```bash
# Login to IBM Container Registry
ibmcloud login
ibmcloud cr region-set us-south
ibmcloud cr login

# Build Backend Image with Podman (ARM64 compatible)
cd backend
podman build --platform linux/amd64 -t us.icr.io/icr-itz-3uehbja7/meeting-room-backend:latest .
podman push us.icr.io/icr-itz-3uehbja7/meeting-room-backend:latest

# Build Frontend Image with Podman
cd ../frontend
podman build --platform linux/amd64 -t us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest .
podman push us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest
```

### 1.4 Alternative: Use IBM Code Engine Build (Recommended for M1)

This method builds images directly in IBM Cloud, avoiding local build issues:

```bash
# Build Backend using Code Engine
ibmcloud ce build create \
  --name meeting-room-backend-build \
  --source . \
  --context-dir backend \
  --image us.icr.io/icr-itz-3uehbja7/meeting-room-backend:latest \
  --registry-secret icr-secret \
  --size large

# Trigger the build
ibmcloud ce buildrun submit --build meeting-room-backend-build

# Build Frontend using Code Engine
ibmcloud ce build create \
  --name meeting-room-frontend-build \
  --source . \
  --context-dir frontend \
  --image us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest \
  --registry-secret icr-secret \
  --size medium

# Trigger the build
ibmcloud ce buildrun submit --build meeting-room-frontend-build
```

## Step 2: Set Up IBM Cloud (Your Configuration)

### 2.1 Login to IBM Cloud

```bash
# Login to IBM Cloud
ibmcloud login

# Or use SSO if configured
ibmcloud login --sso
```

### 2.2 Target Your Resource Group and Region

```bash
# Target your specific resource group
ibmcloud target -g itz-wxo-697b4e2bf2289c92dfa7cf

# Set region (us-south recommended)
ibmcloud target -r us-south

# Verify your target
ibmcloud target
```

### 2.3 Set Up Container Registry

```bash
# Set Container Registry region
ibmcloud cr region-set us-south

# Login to Container Registry
ibmcloud cr login

# Verify your namespace exists
ibmcloud cr namespace-list

# If namespace doesn't exist, create it (yours already exists: icr-itz-3uehbja7)
# ibmcloud cr namespace-add icr-itz-3uehbja7
```

### 2.4 Create Code Engine Project

```bash
# Create a new Code Engine project
ibmcloud ce project create --name meeting-room-app

# Select the project
ibmcloud ce project select --name meeting-room-app

# Verify project is selected
ibmcloud ce project current
```

### 2.5 Create Registry Secret for Code Engine

```bash
# Create API key for registry access
ibmcloud iam api-key-create meeting-room-registry-key -d "API key for Code Engine to access ICR"

# Save the API key from output, then create registry secret
ibmcloud ce registry create \
  --name icr-secret \
  --server us.icr.io \
  --username iamapikey \
  --password YOUR_API_KEY_HERE
```

## Step 3: Set Up PostgreSQL Database

### 3.1 Create Hyper Protect DBaaS for PostgreSQL

```bash
# Create PostgreSQL instance
ibmcloud resource service-instance-create meeting-room-db \
  databases-for-postgresql standard us-south \
  -p '{"members_memory_allocation_mb": "1024", "members_disk_allocation_mb": "5120"}'

# Get connection details
ibmcloud resource service-key-create meeting-room-db-key \
  --instance-name meeting-room-db
```

### 3.2 Note Database Credentials

Save these values from the service key:
- `hostname`
- `port`
- `username`
- `password`
- `database`

## Step 4: Deploy Backend Application

### 4.1 Create Backend Application (Using Your Registry)

```bash
# Deploy backend from IBM Container Registry
ibmcloud ce application create \
  --name meeting-room-backend \
  --image us.icr.io/icr-itz-3uehbja7/meeting-room-backend:latest \
  --registry-secret icr-secret \
  --port 8080 \
  --min-scale 1 \
  --max-scale 3 \
  --cpu 0.5 \
  --memory 1G \
  --env SPRING_PROFILES_ACTIVE=prod \
  --env SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_DB_HOST:YOUR_DB_PORT/YOUR_DB_NAME \
  --env SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME \
  --env SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD \
  --env JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits \
  --env JWT_EXPIRATION=86400000
```

### 4.2 Alternative: Deploy from Code Engine Build

If you used Code Engine build in Step 1.4:

```bash
# Wait for build to complete
ibmcloud ce buildrun list

# Deploy once build is successful
ibmcloud ce application create \
  --name meeting-room-backend \
  --image us.icr.io/icr-itz-3uehbja7/meeting-room-backend:latest \
  --registry-secret icr-secret \
  --port 8080 \
  --min-scale 1 \
  --max-scale 3 \
  --cpu 0.5 \
  --memory 1G \
  --env SPRING_PROFILES_ACTIVE=prod \
  --env SPRING_DATASOURCE_URL=jdbc:h2:mem:meetingroom \
  --env SPRING_DATASOURCE_USERNAME=sa \
  --env SPRING_DATASOURCE_PASSWORD= \
  --env JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits \
  --env JWT_EXPIRATION=86400000
```

**Note**: The above uses H2 in-memory database for quick testing. For production, use PostgreSQL (see Step 3).

### 4.3 Get Backend URL

```bash
ibmcloud ce application get --name meeting-room-backend

# Or get just the URL
ibmcloud ce application get --name meeting-room-backend --output url
```

Save this URL (e.g., `https://meeting-room-backend.xxx.us-south.codeengine.appdomain.cloud`)

## Step 5: Deploy Frontend Application

### 5.1 Update Frontend Environment

Before building the frontend image, update the API URL:

```bash
# Get your backend URL first
BACKEND_URL=$(ibmcloud ce application get --name meeting-room-backend --output url)

# Create production environment file
echo "VITE_API_URL=$BACKEND_URL" > frontend/.env.production
```

### 5.2 Build and Push Frontend Image with Podman

```bash
cd frontend

# Build for AMD64 (Code Engine uses x86_64)
podman build --platform linux/amd64 -t us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest .

# Push to IBM Container Registry
podman push us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest
```

### 5.3 Alternative: Use Code Engine Build for Frontend

```bash
# Create build configuration
ibmcloud ce build create \
  --name meeting-room-frontend-build \
  --source . \
  --context-dir frontend \
  --image us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest \
  --registry-secret icr-secret \
  --size medium

# Submit build
ibmcloud ce buildrun submit --build meeting-room-frontend-build

# Monitor build progress
ibmcloud ce buildrun logs --follow --buildrun meeting-room-frontend-build-run-xxxxx
```

### 5.4 Deploy Frontend Application

```bash
ibmcloud ce application create \
  --name meeting-room-frontend \
  --image us.icr.io/icr-itz-3uehbja7/meeting-room-frontend:latest \
  --registry-secret icr-secret \
  --port 80 \
  --min-scale 1 \
  --max-scale 3 \
  --cpu 0.25 \
  --memory 512M
```

### 5.5 Get Frontend URL and Test

```bash
# Get the frontend URL
FRONTEND_URL=$(ibmcloud ce application get --name meeting-room-frontend --output url)
echo "Frontend URL: $FRONTEND_URL"

# Open in browser
open $FRONTEND_URL
```

Access your application at the provided URL!

### 5.6 Update Backend CORS (Important!)

```bash
# Update backend to allow frontend origin
ibmcloud ce application update \
  --name meeting-room-backend \
  --env CORS_ALLOWED_ORIGINS=$FRONTEND_URL
```

## Step 6: Configure Production Settings

### 6.1 Update Backend application.properties

Create `backend/src/main/resources/application-prod.properties`:

```properties
# Database Configuration
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# CORS Configuration
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:*}

# Logging
logging.level.root=INFO
logging.level.com.meetingroom=INFO
```

### 6.2 Add PostgreSQL Dependency

Add to `backend/pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Step 7: Set Up Custom Domain (Optional)

### 7.1 Create Custom Domain Mapping

```bash
# Map custom domain to frontend
ibmcloud ce application update \
  --name meeting-room-frontend \
  --domain your-domain.com

# Map custom domain to backend
ibmcloud ce application update \
  --name meeting-room-backend \
  --domain api.your-domain.com
```

### 7.2 Configure DNS

Add CNAME records in your DNS provider:
- `your-domain.com` → Frontend Code Engine URL
- `api.your-domain.com` → Backend Code Engine URL

## Step 8: Enable HTTPS and Security

### 8.1 HTTPS is Automatic

Code Engine automatically provides HTTPS certificates for all applications.

### 8.2 Update CORS Configuration

```bash
ibmcloud ce application update \
  --name meeting-room-backend \
  --env CORS_ALLOWED_ORIGINS=https://your-frontend-url.com
```

## Step 9: Set Up Monitoring and Logging

### 9.1 View Application Logs

```bash
# Backend logs
ibmcloud ce application logs --name meeting-room-backend

# Frontend logs
ibmcloud ce application logs --name meeting-room-frontend
```

### 9.2 Monitor Application Health

```bash
# Check backend status
ibmcloud ce application get --name meeting-room-backend

# Check frontend status
ibmcloud ce application get --name meeting-room-frontend
```

### 9.3 Set Up Log Analysis (Optional)

```bash
# Create Log Analysis instance
ibmcloud resource service-instance-create meeting-room-logs \
  logdna standard us-south

# Bind to Code Engine project
ibmcloud ce project update \
  --binding meeting-room-logs
```

## Step 10: Continuous Deployment

### 10.1 Set Up GitHub Actions

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to IBM Code Engine

on:
  push:
    branches: [ main ]

jobs:
  deploy-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Build Backend Docker Image
        run: |
          cd backend
          docker build -t ${{ secrets.DOCKER_USERNAME }}/meeting-room-backend:latest .
      
      - name: Push to Docker Hub
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker push ${{ secrets.DOCKER_USERNAME }}/meeting-room-backend:latest
      
      - name: Deploy to Code Engine
        run: |
          ibmcloud login --apikey ${{ secrets.IBM_CLOUD_API_KEY }}
          ibmcloud ce project select --name meeting-room-app
          ibmcloud ce application update --name meeting-room-backend \
            --image ${{ secrets.DOCKER_USERNAME }}/meeting-room-backend:latest

  deploy-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Build Frontend Docker Image
        run: |
          cd frontend
          docker build -t ${{ secrets.DOCKER_USERNAME }}/meeting-room-frontend:latest .
      
      - name: Push to Docker Hub
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker push ${{ secrets.DOCKER_USERNAME }}/meeting-room-frontend:latest
      
      - name: Deploy to Code Engine
        run: |
          ibmcloud login --apikey ${{ secrets.IBM_CLOUD_API_KEY }}
          ibmcloud ce project select --name meeting-room-app
          ibmcloud ce application update --name meeting-room-frontend \
            --image ${{ secrets.DOCKER_USERNAME }}/meeting-room-frontend:latest
```

## Step 11: Scaling and Performance

### 11.1 Configure Auto-scaling

```bash
# Update backend scaling
ibmcloud ce application update \
  --name meeting-room-backend \
  --min-scale 2 \
  --max-scale 10 \
  --concurrency 100

# Update frontend scaling
ibmcloud ce application update \
  --name meeting-room-frontend \
  --min-scale 2 \
  --max-scale 10
```

### 11.2 Optimize Resource Allocation

```bash
# Increase backend resources for better performance
ibmcloud ce application update \
  --name meeting-room-backend \
  --cpu 1 \
  --memory 2G
```

## Step 12: Backup and Disaster Recovery

### 12.1 Database Backups

```bash
# Enable automatic backups (configured in PostgreSQL service)
ibmcloud resource service-instance-update meeting-room-db \
  --parameters '{"backup_retention_days": 7}'
```

### 12.2 Application Versioning

```bash
# Tag Docker images with versions
docker tag yourusername/meeting-room-backend:latest \
  yourusername/meeting-room-backend:v1.0.0

# Deploy specific version
ibmcloud ce application update \
  --name meeting-room-backend \
  --image yourusername/meeting-room-backend:v1.0.0
```

## Troubleshooting

### Issue 1: Application Not Starting

```bash
# Check application logs
ibmcloud ce application logs --name meeting-room-backend --tail 100

# Check application events
ibmcloud ce application events --name meeting-room-backend
```

### Issue 2: Database Connection Failed

- Verify database credentials in environment variables
- Check if database is accessible from Code Engine
- Ensure PostgreSQL driver is included in dependencies

### Issue 3: CORS Errors

- Update CORS_ALLOWED_ORIGINS environment variable
- Ensure frontend URL is whitelisted in backend

### Issue 4: Out of Memory

```bash
# Increase memory allocation
ibmcloud ce application update \
  --name meeting-room-backend \
  --memory 2G
```

## Cost Optimization

### Tips to Reduce Costs

1. **Use Appropriate Scaling**: Set min-scale to 0 for dev environments
2. **Right-size Resources**: Start with smaller CPU/memory and scale up if needed
3. **Use Free Tier**: Code Engine offers free tier for small workloads
4. **Monitor Usage**: Regularly check resource consumption

```bash
# Set min-scale to 0 for dev environment
ibmcloud ce application update \
  --name meeting-room-backend \
  --min-scale 0 \
  --max-scale 3
```

## Security Best Practices

1. **Use Secrets for Sensitive Data**:
```bash
# Create secret for database credentials
ibmcloud ce secret create db-credentials \
  --from-literal SPRING_DATASOURCE_PASSWORD=your-password

# Reference secret in application
ibmcloud ce application update \
  --name meeting-room-backend \
  --env-from-secret db-credentials
```

2. **Enable Network Policies**: Restrict traffic between services
3. **Regular Updates**: Keep dependencies and base images updated
4. **Use Strong JWT Secrets**: Generate cryptographically secure secrets
5. **Enable HTTPS Only**: Disable HTTP access

## Monitoring Checklist

- [ ] Application health checks configured
- [ ] Logging enabled and monitored
- [ ] Alerts set up for errors
- [ ] Database backups verified
- [ ] SSL certificates valid
- [ ] Auto-scaling working correctly
- [ ] Resource usage within limits
- [ ] Security patches applied

## Support and Resources

- **IBM Code Engine Docs**: https://cloud.ibm.com/docs/codeengine
- **IBM Cloud Support**: https://cloud.ibm.com/unifiedsupport
- **Community Forum**: https://community.ibm.com/community/user/cloud/home

## Next Steps

After successful deployment:

1. Test all application features in production
2. Set up monitoring and alerting
3. Configure backup and disaster recovery
4. Implement CI/CD pipeline
5. Optimize performance based on usage patterns
6. Plan for scaling as user base grows

## Estimated Costs

**Small Deployment (50-100 users)**:
- Backend: ~$20-30/month
- Frontend: ~$10-15/month
- PostgreSQL: ~$30-50/month
- **Total**: ~$60-95/month

**Medium Deployment (200-500 users)**:
- Backend: ~$50-80/month
- Frontend: ~$20-30/month
- PostgreSQL: ~$80-120/month
- **Total**: ~$150-230/month

*Note: Costs vary based on actual usage, region, and resource allocation.*