# WealthPlan Application Architecture

## System Overview

The WealthPlan application is a modern wealth management system that has been migrated from a JSP-based monolithic architecture to a React + Spring Boot microservices architecture with JWT authentication.

## High-Level Architecture Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        Browser[Web Browser]
    end

    subgraph "Frontend - React SPA"
        ReactApp[React Application<br/>Port: 3000]
        
        subgraph "Components"
            Login[Login Component]
            Layout[Layout Component]
            ProtectedRoute[Protected Route]
        end
        
        subgraph "Pages"
            BMPages[Business Manager Pages<br/>- Customer List<br/>- Wealth Manager List]
            WMPages[Wealth Manager Pages<br/>- Customer List<br/>- Financial Plans<br/>- Portfolio<br/>- Profile]
            CusPages[Customer Pages<br/>- Financial Plans<br/>- Portfolio<br/>- Profile]
        end
        
        subgraph "Services"
            AuthService[Auth Service]
            APIService[API Service<br/>Axios HTTP Client]
        end
    end

    subgraph "Backend - Spring Boot"
        SpringBoot[Spring Boot Application<br/>Port: 9020<br/>Context: /api]
        
        subgraph "Security Layer"
            SecurityConfig[Security Config<br/>CORS + JWT]
            JwtFilter[JWT Authentication Filter]
            JwtProvider[JWT Token Provider]
        end
        
        subgraph "REST Controllers"
            AuthController[Auth Controller<br/>/auth/*]
            BMController[Business Manager Controller<br/>/api/bm/*]
            WMController[Wealth Manager Controller<br/>/api/wm/*]
            CusController[Customer Controller<br/>/api/customer/*]
        end
        
        subgraph "Service Layer"
            LoginService[Login Service]
            BMService[Business Manager Service]
            WMService[Wealth Manager Service]
            CustomerService[Customer Service]
            GoalService[Goal Service]
            InvestmentService[Investment Service]
            QuoteService[Finance Quote Service]
        end
        
        subgraph "Data Access Layer"
            UserRepo[User Repository]
            BMRepo[Business Manager Repository]
            WMRepo[Wealth Manager Repository]
            CusRepo[Customer Repository]
            GoalRepo[Goal Repository]
            InvRepo[Investment Repository]
        end
    end

    subgraph "Data Layer"
        H2DB[(H2 In-Memory Database<br/>JDBC: jdbc:h2:mem:/wcare)]
        
        subgraph "Database Tables"
            UsersTable[WcUsers<br/>- id, username, password<br/>- email, role]
            BMTable[WcBusinessManager<br/>- id, userId, firstName<br/>- lastName, startDate]
            WMTable[WcWealthManager<br/>- id, userId, firstName<br/>- lastName, contact info]
            CusTable[WcCustomer<br/>- id, userId, wmId<br/>- personal & family info]
            GoalTable[WcGoal<br/>- id, customerId, wmId<br/>- goal details, targets]
            InvTable[WcInvestment<br/>- id, goalId<br/>- investment details]
        end
    end

    Browser --> ReactApp
    ReactApp --> Login
    ReactApp --> Layout
    ReactApp --> ProtectedRoute
    
    Login --> AuthService
    BMPages --> APIService
    WMPages --> APIService
    CusPages --> APIService
    
    AuthService --> APIService
    APIService -->|HTTP/REST + JWT| SpringBoot
    
    SpringBoot --> SecurityConfig
    SecurityConfig --> JwtFilter
    JwtFilter --> JwtProvider
    
    JwtFilter --> AuthController
    JwtFilter --> BMController
    JwtFilter --> WMController
    JwtFilter --> CusController
    
    AuthController --> LoginService
    BMController --> BMService
    WMController --> WMService
    WMController --> CustomerService
    WMController --> GoalService
    WMController --> InvestmentService
    CusController --> CustomerService
    CusController --> GoalService
    
    LoginService --> UserRepo
    BMService --> BMRepo
    BMService --> CusRepo
    BMService --> WMRepo
    WMService --> WMRepo
    CustomerService --> CusRepo
    GoalService --> GoalRepo
    InvestmentService --> InvRepo
    
    UserRepo --> H2DB
    BMRepo --> H2DB
    WMRepo --> H2DB
    CusRepo --> H2DB
    GoalRepo --> H2DB
    InvRepo --> H2DB
    
    H2DB --> UsersTable
    H2DB --> BMTable
    H2DB --> WMTable
    H2DB --> CusTable
    H2DB --> GoalTable
    H2DB --> InvTable

    style Browser fill:#e1f5ff
    style ReactApp fill:#61dafb
    style SpringBoot fill:#6db33f
    style H2DB fill:#1021ff
    style SecurityConfig fill:#ffd700
    style JwtFilter fill:#ffd700
    style JwtProvider fill:#ffd700
```

## Authentication Flow

```mermaid
sequenceDiagram
    participant User
    participant React as React Frontend
    participant Auth as Auth Controller
    participant Login as Login Service
    participant JWT as JWT Provider
    participant DB as Database

    User->>React: Enter credentials
    React->>Auth: POST /auth/login
    Auth->>Login: processLogin()
    Login->>DB: Query user credentials
    DB-->>Login: User data
    Login-->>Auth: LoginInfo
    Auth->>JWT: generateToken()
    JWT-->>Auth: JWT Token
    Auth-->>React: LoginResponse + Token
    React->>React: Store token in localStorage
    React-->>User: Redirect to dashboard
    
    Note over React,Auth: Subsequent API Calls
    User->>React: Access protected resource
    React->>Auth: API Request + Bearer Token
    Auth->>JWT: validateToken()
    JWT-->>Auth: Token valid
    Auth->>Login: Process request
    Login-->>Auth: Response data
    Auth-->>React: JSON Response
    React-->>User: Display data
```

## Role-Based Access Control

```mermaid
graph LR
    subgraph "User Roles"
        BM[Business Manager<br/>ROLE_BM]
        WM[Wealth Manager<br/>ROLE_WM]
        CUS[Customer<br/>ROLE_CUSTOMER]
    end
    
    subgraph "Business Manager Access"
        BM1[View All Customers]
        BM2[View All Wealth Managers]
        BM3[Manage Assignments]
    end
    
    subgraph "Wealth Manager Access"
        WM1[View Assigned Customers]
        WM2[Create Financial Plans]
        WM3[Add Investments]
        WM4[View Customer Portfolio]
        WM5[Manage Profile]
    end
    
    subgraph "Customer Access"
        CUS1[View Own Financial Plans]
        CUS2[View Own Portfolio]
        CUS3[View Own Profile]
        CUS4[Track Goals]
    end
    
    BM --> BM1
    BM --> BM2
    BM --> BM3
    
    WM --> WM1
    WM --> WM2
    WM --> WM3
    WM --> WM4
    WM --> WM5
    
    CUS --> CUS1
    CUS --> CUS2
    CUS --> CUS3
    CUS --> CUS4

    style BM fill:#ff6b6b
    style WM fill:#4ecdc4
    style CUS fill:#95e1d3
```

## Data Model Relationships

```mermaid
erDiagram
    WcUsers ||--o| WcBusinessManager : "has profile"
    WcUsers ||--o| WcWealthManager : "has profile"
    WcUsers ||--o| WcCustomer : "has profile"
    WcWealthManager ||--o{ WcCustomer : "manages"
    WcCustomer ||--o{ WcGoal : "has"
    WcWealthManager ||--o{ WcGoal : "creates"
    WcGoal ||--o{ WcInvestment : "contains"

    WcUsers {
        int id PK
        string username
        string password
        string emailId
        string role
    }

    WcBusinessManager {
        int id PK
        int wcUserId FK
        string firstName
        string lastName
        date startDate
    }

    WcWealthManager {
        int id PK
        int wcUserId FK
        string firstName
        string lastName
        string gender
        string city
        string phone
        string emailId
        date startDate
        string country
        string zipCode
    }

    WcCustomer {
        int id PK
        int wcUserId FK
        int wcWealthManagerId FK
        string firstName
        string lastName
        string gender
        int age
        double avgIncome
        boolean married
        string spouseInfo
        int noOfChildren
        string childrenInfo
        string contactInfo
    }

    WcGoal {
        int id PK
        int wcCustomerId FK
        int wcWealthManagerId FK
        string goalReference
        string goalDesc
        date startDate
        date targetDate
        double targetAmount
    }

    WcInvestment {
        int id PK
        int wcGoalId FK
        date investmentDate
        string investmentType
        double investmentAmount
    }
```

## API Endpoints Structure

```mermaid
graph TB
    subgraph "Public Endpoints"
        Login[POST /auth/login]
        Logout[POST /auth/logout]
        Validate[GET /auth/validate]
    end
    
    subgraph "Business Manager Endpoints - /api/bm/*"
        BMCus[GET /api/bm/customers]
        BMCusId[GET /api/bm/customers/:id]
        BMWM[GET /api/bm/wealth-managers]
        BMWMId[GET /api/bm/wealth-managers/:id]
    end
    
    subgraph "Wealth Manager Endpoints - /api/wm/*"
        WMCus[GET /api/wm/customers]
        WMFP[GET /api/wm/customers/:id/financial-plans]
        WMFPCreate[POST /api/wm/customers/:id/financial-plans]
        WMFPDel[DELETE /api/wm/financial-plans/:id]
        WMInv[POST /api/wm/financial-plans/:id/investments]
        WMPort[GET /api/wm/customers/:id/portfolio]
    end
    
    subgraph "Customer Endpoints - /api/customer/*"
        CusFP[GET /api/customer/financial-plans]
        CusFPId[GET /api/customer/financial-plans/:id]
        CusPort[GET /api/customer/portfolio]
        CusProf[GET /api/customer/profile]
    end

    style Login fill:#90EE90
    style Logout fill:#90EE90
    style Validate fill:#90EE90
```

## Technology Stack

```mermaid
graph LR
    subgraph "Frontend Stack"
        React[React 19.2.4]
        TS[TypeScript 4.9.5]
        Router[React Router 7.13.0]
        Axios[Axios 1.13.5]
        CSS[CSS Modules]
    end
    
    subgraph "Backend Stack"
        SB[Spring Boot 2.7.18]
        Java[Java 11]
        JPA[Spring Data JPA]
        Sec[Spring Security]
        JWT[JWT JJWT]
        Valid[Validation API]
    end
    
    subgraph "Database"
        H2[H2 In-Memory DB]
    end
    
    subgraph "Build Tools"
        Maven[Maven]
        NPM[NPM]
    end

    React --> TS
    React --> Router
    React --> Axios
    React --> CSS
    
    SB --> Java
    SB --> JPA
    SB --> Sec
    SB --> JWT
    SB --> Valid
    
    SB --> H2
    
    style React fill:#61dafb
    style SB fill:#6db33f
    style H2 fill:#1021ff
```

## Deployment Architecture

```mermaid
graph TB
    subgraph "Development Environment"
        DevFE[React Dev Server<br/>localhost:3000]
        DevBE[Spring Boot<br/>localhost:9020]
        DevDB[(H2 In-Memory DB)]
    end
    
    subgraph "Production Environment"
        LB[Load Balancer]
        
        subgraph "Frontend Tier"
            CDN[CDN / Web Server<br/>Static Files]
        end
        
        subgraph "Backend Tier"
            BE1[Spring Boot Instance 1]
            BE2[Spring Boot Instance 2]
            BE3[Spring Boot Instance N]
        end
        
        subgraph "Database Tier"
            ProdDB[(Production Database<br/>PostgreSQL/MySQL)]
        end
    end
    
    User[End Users] --> LB
    LB --> CDN
    LB --> BE1
    LB --> BE2
    LB --> BE3
    
    BE1 --> ProdDB
    BE2 --> ProdDB
    BE3 --> ProdDB
    
    DevFE -.->|Build| CDN
    DevBE -.->|Package| BE1

    style CDN fill:#ff6b6b
    style BE1 fill:#6db33f
    style BE2 fill:#6db33f
    style BE3 fill:#6db33f
    style ProdDB fill:#1021ff
```

## Security Architecture

```mermaid
graph TB
    subgraph "Security Layers"
        CORS[CORS Configuration<br/>Allowed Origins]
        JWT[JWT Token Validation]
        RBAC[Role-Based Access Control]
        HTTPS[HTTPS/TLS]
    end
    
    subgraph "Authentication Flow"
        Login[Login Request]
        Validate[Validate Credentials]
        Generate[Generate JWT Token]
        Store[Store in localStorage]
    end
    
    subgraph "Authorization Flow"
        Request[API Request + Token]
        Extract[Extract Token from Header]
        Verify[Verify Token Signature]
        CheckRole[Check User Role]
        Allow[Allow/Deny Access]
    end
    
    HTTPS --> CORS
    CORS --> JWT
    JWT --> RBAC
    
    Login --> Validate
    Validate --> Generate
    Generate --> Store
    
    Request --> Extract
    Extract --> Verify
    Verify --> CheckRole
    CheckRole --> Allow

    style CORS fill:#ffd700
    style JWT fill:#ffd700
    style RBAC fill:#ffd700
    style HTTPS fill:#90EE90
```

## Key Features

### 1. **Separation of Concerns**
- Frontend and backend are completely decoupled
- Independent scaling and deployment
- Clear API contracts

### 2. **Stateless Authentication**
- JWT tokens eliminate server-side session management
- Tokens contain user identity and roles
- Automatic token validation on each request

### 3. **Role-Based Access**
- Three distinct user roles: Business Manager, Wealth Manager, Customer
- Each role has specific permissions and accessible endpoints
- Protected routes in frontend and backend

### 4. **Modern Tech Stack**
- React with TypeScript for type safety
- Spring Boot for rapid development
- H2 database for development (easily switchable to production DB)

### 5. **RESTful API Design**
- Standard HTTP methods (GET, POST, DELETE)
- JSON request/response format
- Consistent error handling

## Configuration

### Backend Configuration
- **Port**: 9020
- **Context Path**: /api
- **JWT Secret**: Configurable in application.properties
- **Token Expiration**: 24 hours (86400000 ms)
- **CORS**: Configured for localhost:3000, localhost:3001

### Frontend Configuration
- **Port**: 3000
- **API Base URL**: http://localhost:9020
- **Token Storage**: localStorage
- **Auto-redirect**: On authentication failure

## Migration Benefits

1. **Scalability**: Frontend and backend can scale independently
2. **Performance**: SPA provides faster user experience
3. **Security**: JWT-based stateless authentication
4. **Maintainability**: Clear separation of concerns
5. **Modern UX**: React provides rich, interactive interface
6. **API-First**: Backend can serve multiple clients
7. **Type Safety**: TypeScript reduces runtime errors
8. **Deployment Flexibility**: Can deploy to different environments

---

*This architecture document was generated for the WealthPlan application migration from JSP to React + Spring Boot.*
