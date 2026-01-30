# Wealthcare Frontend Application

A modern React-based frontend for the Wealthcare Wealth Management platform.

## Overview

This is a single-page application (SPA) built with React that provides a web interface for the Wealthcare microservices platform. It includes login functionality, dashboards for different user roles, and management interfaces for customers, goals, and investments.

## Features

- 🔐 **Authentication** - JWT-based login system
- 👥 **Role-Based Access** - Different views for Business Managers, Wealth Managers, and Customers
- 📊 **Dashboard** - Overview of key metrics and data
- 👤 **Customer Management** - Create, view, update customer profiles
- 🎯 **Goal Management** - Track and manage financial goals
- 💰 **Investment Management** - Portfolio tracking and management
- 📱 **Responsive Design** - Works on desktop and mobile devices

## Prerequisites

- Node.js 16+ and npm
- Backend microservices running (User, Customer, Goal, Investment services)

## Quick Start

### 1. Install Dependencies

```bash
cd microservices/frontend-app
npm install
```

### 2. Configure API Endpoints

Create `.env` file:

```bash
REACT_APP_USER_SERVICE_URL=http://localhost:8081
REACT_APP_CUSTOMER_SERVICE_URL=http://localhost:8082
REACT_APP_GOAL_SERVICE_URL=http://localhost:8083
REACT_APP_INVESTMENT_SERVICE_URL=http://localhost:8084
```

### 3. Start Development Server

```bash
npm start
```

The application will open at **http://localhost:3000**

### 4. Build for Production

```bash
npm run build
```

## Project Structure

```
frontend-app/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── Navbar.jsx
│   │   ├── Sidebar.jsx
│   │   ├── Card.jsx
│   │   └── Table.jsx
│   ├── pages/              # Page components
│   │   ├── Login.jsx
│   │   ├── Dashboard.jsx
│   │   ├── Customers.jsx
│   │   ├── Goals.jsx
│   │   └── Investments.jsx
│   ├── services/           # API service layer
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── customerService.js
│   │   ├── goalService.js
│   │   └── investmentService.js
│   ├── context/            # React Context for state management
│   │   └── AuthContext.jsx
│   ├── styles/             # CSS files
│   │   ├── App.css
│   │   ├── Login.css
│   │   └── Dashboard.css
│   ├── App.jsx             # Main application component
│   └── index.js            # Entry point
├── package.json
└── README.md
```

## Setup Instructions

### Step 1: Create React App (if not already created)

```bash
cd microservices
npx create-react-app frontend-app
cd frontend-app
```

### Step 2: Install Required Dependencies

```bash
npm install axios react-router-dom jwt-decode
```

### Step 3: Copy the Source Files

Copy all the files from this directory structure into your React app.

### Step 4: Enable CORS in Backend Services

Add CORS configuration to each backend service's `SecurityConfig.java` or create a `WebConfig.java`:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

## User Roles and Access

### Business Manager
- View all customers
- View all wealth managers
- Assign wealth managers to customers
- View system-wide reports

### Wealth Manager
- View assigned customers
- Create and manage financial goals
- Add and track investments
- View customer portfolios

### Customer
- View personal profile
- View financial goals
- View investment portfolio
- Track goal progress

## Default Test Users

After starting the backend services and creating users via API:

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | BUSINESS_MANAGER |
| manager1 | manager123 | WEALTH_MANAGER |
| customer1 | customer123 | CUSTOMER |

## API Integration

The frontend communicates with backend microservices via REST APIs:

### Authentication Flow

1. User enters credentials on login page
2. Frontend sends POST to `/api/auth/login`
3. Backend returns JWT token
4. Token stored in localStorage
5. Token included in all subsequent API requests

### API Endpoints Used

**User Service (Port 8081)**
- POST `/api/auth/login` - User login
- GET `/api/users` - Get all users
- POST `/api/users` - Create user

**Customer Service (Port 8082)**
- GET `/api/customers` - Get all customers
- GET `/api/customers/{id}` - Get customer by ID
- POST `/api/customers` - Create customer
- PUT `/api/customers/{id}` - Update customer

**Goal Service (Port 8083)**
- GET `/api/goals` - Get all goals
- GET `/api/goals/customer/{customerId}` - Get customer goals
- POST `/api/goals` - Create goal
- PUT `/api/goals/{id}` - Update goal

**Investment Service (Port 8084)**
- GET `/api/investments` - Get all investments
- GET `/api/investments/customer/{customerId}` - Get customer investments
- GET `/api/investments/customer/{customerId}/portfolio-summary` - Get portfolio summary
- POST `/api/investments` - Create investment
- PUT `/api/investments/{id}` - Update investment

## Development

### Running in Development Mode

```bash
npm start
```

- Hot reload enabled
- Opens browser automatically
- Proxies API requests to backend

### Building for Production

```bash
npm run build
```

Creates optimized production build in `build/` directory.

### Running Tests

```bash
npm test
```

### Code Formatting

```bash
npm run format
```

## Deployment

### Option 1: Static Hosting

Deploy the `build/` folder to:
- AWS S3 + CloudFront
- Netlify
- Vercel
- GitHub Pages

### Option 2: Docker Container

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Build and run:
```bash
docker build -t wealthcare-frontend .
docker run -p 3000:80 wealthcare-frontend
```

### Option 3: Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: frontend-app
  template:
    metadata:
      labels:
        app: frontend-app
    spec:
      containers:
      - name: frontend-app
        image: wealthcare/frontend-app:1.0.0
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-app
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 80
  selector:
    app: frontend-app
```

## Environment Variables

Create `.env` file for different environments:

### Development (.env.development)
```
REACT_APP_API_BASE_URL=http://localhost:8081
REACT_APP_USER_SERVICE_URL=http://localhost:8081
REACT_APP_CUSTOMER_SERVICE_URL=http://localhost:8082
REACT_APP_GOAL_SERVICE_URL=http://localhost:8083
REACT_APP_INVESTMENT_SERVICE_URL=http://localhost:8084
```

### Production (.env.production)
```
REACT_APP_API_BASE_URL=https://api.wealthcare.com
REACT_APP_USER_SERVICE_URL=https://api.wealthcare.com/users
REACT_APP_CUSTOMER_SERVICE_URL=https://api.wealthcare.com/customers
REACT_APP_GOAL_SERVICE_URL=https://api.wealthcare.com/goals
REACT_APP_INVESTMENT_SERVICE_URL=https://api.wealthcare.com/investments
```

## Troubleshooting

### CORS Errors

If you see CORS errors in the browser console:

1. Ensure backend services have CORS enabled
2. Check that `allowedOrigins` includes `http://localhost:3000`
3. Verify `allowCredentials` is set to `true`

### Authentication Issues

If login fails:

1. Check backend User Service is running on port 8081
2. Verify credentials are correct
3. Check browser console for error messages
4. Ensure JWT token is being stored in localStorage

### API Connection Errors

If API calls fail:

1. Verify all backend services are running
2. Check service URLs in `.env` file
3. Test API endpoints directly using curl or Postman
4. Check browser network tab for failed requests

### Build Errors

If build fails:

1. Delete `node_modules` and `package-lock.json`
2. Run `npm install` again
3. Clear npm cache: `npm cache clean --force`
4. Check Node.js version: `node --version` (should be 16+)

## Features to Implement

### Phase 1 (Current)
- ✅ Login page
- ✅ Dashboard
- ✅ Customer list and details
- ✅ Goal list and management
- ✅ Investment list and portfolio

### Phase 2 (Future)
- [ ] Advanced charts and graphs
- [ ] Real-time notifications
- [ ] Export to PDF/Excel
- [ ] Advanced search and filters
- [ ] User profile management
- [ ] Settings page

### Phase 3 (Future)
- [ ] Mobile app (React Native)
- [ ] Offline support
- [ ] Push notifications
- [ ] Multi-language support
- [ ] Dark mode

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review browser console for errors
3. Check backend service logs
4. Verify API endpoints are accessible

## License

Copyright © 2024 Wealthcare. All rights reserved.