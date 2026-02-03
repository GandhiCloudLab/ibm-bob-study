#!/bin/bash

# Meeting Room Booking Application - IBM Code Engine Deployment Script
# Optimized for Mac M1 Pro with Podman
# Resource Group: itz-wxo-697b4e2bf2289c92dfa7cf
# Registry Namespace: icr-itz-3uehbja7

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
RESOURCE_GROUP="itz-wxo-697b4e2bf2289c92dfa7cf"
REGISTRY_NAMESPACE="icr-itz-3uehbja7"
REGISTRY_REGION="us.icr.io"
PROJECT_NAME="meeting-room-app"
REGION="us-south"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Meeting Room Booking - IBM Cloud Deploy${NC}"
echo -e "${GREEN}========================================${NC}"

# Step 1: Login and Target
echo -e "\n${YELLOW}Step 1: Setting up IBM Cloud CLI${NC}"
echo "Logging in to IBM Cloud..."
ibmcloud login || { echo -e "${RED}Login failed${NC}"; exit 1; }

echo "Targeting resource group: $RESOURCE_GROUP"
ibmcloud target -g $RESOURCE_GROUP -r $REGION

echo "Setting up Container Registry..."
ibmcloud cr region-set $REGION
ibmcloud cr login

# Step 2: Create or Select Code Engine Project
echo -e "\n${YELLOW}Step 2: Setting up Code Engine Project${NC}"
if ibmcloud ce project get --name $PROJECT_NAME &> /dev/null; then
    echo "Project $PROJECT_NAME already exists, selecting it..."
    ibmcloud ce project select --name $PROJECT_NAME
else
    echo "Creating new project: $PROJECT_NAME"
    ibmcloud ce project create --name $PROJECT_NAME
    ibmcloud ce project select --name $PROJECT_NAME
fi

# Step 3: Create Registry Secret (if not exists)
echo -e "\n${YELLOW}Step 3: Setting up Registry Secret${NC}"
if ibmcloud ce registry get --name icr-secret &> /dev/null; then
    echo "Registry secret already exists"
else
    echo "Creating registry secret..."
    echo "Please enter your IBM Cloud API key:"
    read -s API_KEY
    ibmcloud ce registry create \
        --name icr-secret \
        --server $REGISTRY_REGION \
        --username iamapikey \
        --password $API_KEY
fi

# Step 4: Build Method Selection
echo -e "\n${YELLOW}Step 4: Choose Build Method${NC}"
echo "1) Use Podman (local build)"
echo "2) Use Code Engine Build (recommended for M1)"
read -p "Select option (1 or 2): " BUILD_METHOD

if [ "$BUILD_METHOD" == "1" ]; then
    # Podman Build
    echo -e "\n${YELLOW}Building with Podman...${NC}"
    
    # Check if Podman is installed
    if ! command -v podman &> /dev/null; then
        echo -e "${RED}Podman not found. Install with: brew install podman${NC}"
        exit 1
    fi
    
    # Check if Podman machine is running
    if ! podman machine list | grep -q "Currently running"; then
        echo "Starting Podman machine..."
        podman machine start
    fi
    
    # Build Backend
    echo "Building backend image..."
    cd backend
    podman build --platform linux/amd64 -t $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-backend:latest .
    echo "Pushing backend image..."
    podman push $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-backend:latest
    cd ..
    
    # Build Frontend
    echo "Building frontend image..."
    cd frontend
    podman build --platform linux/amd64 -t $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-frontend:latest .
    echo "Pushing frontend image..."
    podman push $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-frontend:latest
    cd ..
    
elif [ "$BUILD_METHOD" == "2" ]; then
    # Code Engine Build
    echo -e "\n${YELLOW}Building with Code Engine...${NC}"
    
    # Build Backend
    echo "Creating backend build..."
    if ibmcloud ce build get --name meeting-room-backend-build &> /dev/null; then
        echo "Backend build exists, updating..."
        ibmcloud ce build update --name meeting-room-backend-build \
            --source . \
            --context-dir backend \
            --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-backend:latest
    else
        ibmcloud ce build create \
            --name meeting-room-backend-build \
            --source . \
            --context-dir backend \
            --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-backend:latest \
            --registry-secret icr-secret \
            --size large
    fi
    
    echo "Submitting backend build..."
    ibmcloud ce buildrun submit --build meeting-room-backend-build --wait
    
    # Build Frontend
    echo "Creating frontend build..."
    if ibmcloud ce build get --name meeting-room-frontend-build &> /dev/null; then
        echo "Frontend build exists, updating..."
        ibmcloud ce build update --name meeting-room-frontend-build \
            --source . \
            --context-dir frontend \
            --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-frontend:latest
    else
        ibmcloud ce build create \
            --name meeting-room-frontend-build \
            --source . \
            --context-dir frontend \
            --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-frontend:latest \
            --registry-secret icr-secret \
            --size medium
    fi
    
    echo "Submitting frontend build..."
    ibmcloud ce buildrun submit --build meeting-room-frontend-build --wait
else
    echo -e "${RED}Invalid option${NC}"
    exit 1
fi

# Step 5: Deploy Backend
echo -e "\n${YELLOW}Step 5: Deploying Backend Application${NC}"
if ibmcloud ce application get --name meeting-room-backend &> /dev/null; then
    echo "Backend application exists, updating..."
    ibmcloud ce application update \
        --name meeting-room-backend \
        --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-backend:latest
else
    echo "Creating backend application..."
    ibmcloud ce application create \
        --name meeting-room-backend \
        --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-backend:latest \
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
fi

# Get backend URL
BACKEND_URL=$(ibmcloud ce application get --name meeting-room-backend --output url)
echo -e "${GREEN}Backend URL: $BACKEND_URL${NC}"

# Step 6: Deploy Frontend
echo -e "\n${YELLOW}Step 6: Deploying Frontend Application${NC}"

# Update frontend environment
echo "VITE_API_URL=$BACKEND_URL" > frontend/.env.production

if ibmcloud ce application get --name meeting-room-frontend &> /dev/null; then
    echo "Frontend application exists, updating..."
    ibmcloud ce application update \
        --name meeting-room-frontend \
        --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-frontend:latest
else
    echo "Creating frontend application..."
    ibmcloud ce application create \
        --name meeting-room-frontend \
        --image $REGISTRY_REGION/$REGISTRY_NAMESPACE/meeting-room-frontend:latest \
        --registry-secret icr-secret \
        --port 80 \
        --min-scale 1 \
        --max-scale 3 \
        --cpu 0.25 \
        --memory 512M
fi

# Get frontend URL
FRONTEND_URL=$(ibmcloud ce application get --name meeting-room-frontend --output url)
echo -e "${GREEN}Frontend URL: $FRONTEND_URL${NC}"

# Step 7: Update CORS
echo -e "\n${YELLOW}Step 7: Updating CORS Configuration${NC}"
ibmcloud ce application update \
    --name meeting-room-backend \
    --env CORS_ALLOWED_ORIGINS=$FRONTEND_URL

# Step 8: Summary
echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "\n${YELLOW}Application URLs:${NC}"
echo -e "Frontend: ${GREEN}$FRONTEND_URL${NC}"
echo -e "Backend:  ${GREEN}$BACKEND_URL${NC}"
echo -e "\n${YELLOW}Test Credentials:${NC}"
echo -e "Admin:    username=${GREEN}admin${NC}, password=${GREEN}admin123${NC}"
echo -e "User:     username=${GREEN}user${NC}, password=${GREEN}user123${NC}"
echo -e "\n${YELLOW}Next Steps:${NC}"
echo "1. Open the frontend URL in your browser"
echo "2. Login with test credentials"
echo "3. Test the application features"
echo "4. Monitor logs: ibmcloud ce application logs --name meeting-room-backend"
echo -e "\n${GREEN}Happy testing!${NC}"

# Made with Bob
