# Meeting Room Booking Application

A modern web-based meeting room booking system built with React and Spring Boot, designed for medium-sized organizations (50-200 users).

## 🎯 Features

### Admin Features
- Create, update, and delete meeting rooms
- View all bookings across the organization
- Manage room details (name, location, capacity, description)
- User management

### User Features
- Book meeting rooms for specific time slots
- View personal bookings
- View all bookings (calendar view)
- Cancel own bookings
- Check room availability in real-time

### Core Capabilities
- ✅ JWT-based authentication
- ✅ Role-based access control (Admin/User)
- ✅ Automatic booking conflict detection
- ✅ Real-time availability checking
- ✅ Responsive design for mobile and desktop
- ✅ Form validation and error handling

## 🏗️ Tech Stack

### Frontend
- **React 18+** with TypeScript
- **Material-UI** for UI components
- **React Router** for navigation
- **Axios** for API calls
- **React Hook Form** for form handling
- **Vite** as build tool

### Backend
- **Spring Boot 3.x** with Java 17+
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **H2 Database** (file-based for production)
- **Maven** for build management
- **SpringDoc OpenAPI** for API documentation

### Deployment
- **IBM Code Engine** for hosting
- **Podman/Docker** for containerization (M1 Mac compatible)
- **IBM Container Registry** for image storage
- **Nginx** for serving frontend

## 📁 Project Structure

```
meeting-room-booking/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/meetingroom/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── repository/
│   │   │   │       ├── model/
│   │   │   │       ├── dto/
│   │   │   │       ├── security/
│   │   │   │       └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/               # React application
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── auth/
│   │   │   ├── admin/
│   │   │   ├── booking/
│   │   │   └── user/
│   │   ├── contexts/
│   │   ├── services/
│   │   ├── hooks/
│   │   ├── types/
│   │   ├── utils/
│   │   └── App.tsx
│   ├── package.json
│   └── Dockerfile
│
├── ARCHITECTURE.md         # Detailed architecture documentation
├── IMPLEMENTATION_GUIDE.md # Step-by-step implementation guide
└── README.md              # This file
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.6+
- Podman (for M1 Mac) or Docker (for deployment)
- IBM Cloud CLI with Code Engine plugin (for cloud deployment)

### Backend Setup

```bash
# Navigate to backend directory
cd backend

# Run the application
./mvnw spring-boot:run

# The backend will start at http://localhost:8080
# H2 Console available at http://localhost:8080/h2-console
```

### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# The frontend will start at http://localhost:5173
```

### Default Credentials (Development)
- **Admin**: username: `admin`, password: `admin123`
- **User**: username: `user`, password: `user123`

## 📚 Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Complete system architecture, database schema, API design, and deployment strategy
- **[IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md)** - Step-by-step implementation guide with code examples and best practices
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Comprehensive testing guide with test scenarios and validation steps
- **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - IBM Code Engine deployment guide optimized for Mac M1 Pro

## 🔑 Key Features Explained

### Booking Conflict Detection
The system automatically prevents double-booking by checking for time slot overlaps:
- Validates room availability before confirming bookings
- Checks for overlapping time ranges on the same date
- Returns clear error messages when conflicts are detected

### Authentication & Authorization
- JWT-based stateless authentication
- Role-based access control (RBAC)
- Protected routes on both frontend and backend
- Automatic token refresh mechanism

### User Experience
- Intuitive calendar interface for viewing bookings
- Real-time availability checking
- Form validation with helpful error messages
- Responsive design for all screen sizes

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token
- `GET /api/auth/me` - Get current user information

### Rooms (Admin only for create/update/delete)
- `GET /api/rooms` - List all rooms
- `POST /api/rooms` - Create new room
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room

### Bookings
- `GET /api/bookings` - List all bookings
- `POST /api/bookings` - Create new booking
- `GET /api/bookings/my-bookings` - Get user's bookings
- `DELETE /api/bookings/{id}` - Cancel booking
- `GET /api/bookings/check-availability` - Check room availability

## 🐳 Docker Deployment

### Build Images

```bash
# Backend
cd backend
./mvnw clean package
docker build -t meetingroom-backend .

# Frontend
cd frontend
npm run build
docker build -t meetingroom-frontend .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

## ☁️ IBM Code Engine Deployment

### Quick Deploy (Automated Script)

```bash
# Make script executable
chmod +x deploy-to-ibm-cloud.sh

# Run deployment script
./deploy-to-ibm-cloud.sh
```

The script will:
1. Login to IBM Cloud
2. Target your resource group (`itz-wxo-697b4e2bf2289c92dfa7cf`)
3. Build images using Podman or Code Engine Build
4. Push to IBM Container Registry (`icr-itz-3uehbja7`)
5. Deploy both backend and frontend applications
6. Configure CORS and environment variables

### Manual Deployment

See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) for detailed manual deployment instructions.

**Your Configuration:**
- Resource Group: `itz-wxo-697b4e2bf2289c92dfa7cf`
- Registry Namespace: `icr-itz-3uehbja7`
- Registry: `us.icr.io`

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📊 Database Schema

### Core Tables
- **USER** - User accounts with roles
- **ROOM** - Meeting room details
- **BOOKING** - Booking records with time slots

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed schema and relationships.

## 🔒 Security Features

- Password hashing with BCrypt
- JWT token-based authentication
- CORS configuration
- Input validation on both client and server
- SQL injection prevention via JPA
- Role-based access control

## 📈 Performance Considerations

- Database indexing on frequently queried columns
- Connection pooling for database connections
- Lazy loading of components in frontend
- Pagination for large data sets
- Caching strategy for frequently accessed data

## 🛠️ Development Workflow

1. **Local Development**: Run backend and frontend separately
2. **Testing**: Write unit and integration tests
3. **Build**: Create production builds
4. **Deploy**: Deploy to IBM Code Engine

## 📝 Implementation Timeline

- **Week 1-2**: Backend setup, database, authentication
- **Week 3-4**: Room and booking APIs
- **Week 5-6**: Frontend development
- **Week 7**: Testing and bug fixes
- **Week 8**: Deployment and final testing

**Total**: 8 weeks for MVP

## 🚧 Future Enhancements

### Phase 2
- Email notifications for bookings
- Calendar integration (Google Calendar, Outlook)
- Recurring bookings
- Room equipment management

### Phase 3
- Mobile application
- Advanced analytics and reporting
- Booking approval workflow
- Audit logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Support

For questions or issues, please contact the development team or create an issue in the repository.

---

**Built with ❤️ for efficient meeting room management**