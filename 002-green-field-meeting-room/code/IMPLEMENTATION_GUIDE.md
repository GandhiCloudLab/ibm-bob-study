# Meeting Room Booking - Implementation Guide

## Quick Start Commands

### Backend (Spring Boot)

```bash
# Create Spring Boot project
# Visit https://start.spring.io/ with these dependencies:
# - Spring Web
# - Spring Data JPA
# - Spring Security
# - H2 Database
# - Lombok
# - Validation
# - Spring Boot DevTools

# Or use Spring Initializr CLI
spring init --dependencies=web,data-jpa,security,h2,lombok,validation \
  --build=maven --java-version=17 --packaging=jar \
  --group-id=com.meetingroom --artifact-id=backend \
  --name=MeetingRoomBooking backend

cd backend
./mvnw spring-boot:run
```

### Frontend (React + Vite)

```bash
# Create React project with Vite
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install

# Install dependencies
npm install axios react-router-dom @mui/material @emotion/react @emotion/styled
npm install react-hook-form yup @hookform/resolvers
npm install date-fns
npm install -D @types/node

npm run dev
```

## Key Implementation Steps

### 1. Backend Setup

#### application.properties
```properties
# Server Configuration
server.port=8080

# H2 Database Configuration
spring.datasource.url=jdbc:h2:file:./data/meetingroom
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console (disable in production)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
jwt.secret=your-secret-key-change-in-production
jwt.expiration=3600000

# CORS Configuration
cors.allowed.origins=http://localhost:5173
```

#### Key Classes to Implement

1. **User Entity** (`model/User.java`)
   - Fields: id, username, password, email, fullName, role
   - Use `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
   - Hash password with BCrypt

2. **Room Entity** (`model/Room.java`)
   - Fields: id, name, location, capacity, description, isActive
   - Add validation annotations

3. **Booking Entity** (`model/Booking.java`)
   - Fields: id, user, room, bookingDate, startTime, endTime, purpose, status
   - Use `@ManyToOne` for relationships
   - Add unique constraint for conflict prevention

4. **JWT Token Provider** (`security/JwtTokenProvider.java`)
   - Generate token
   - Validate token
   - Extract username from token

5. **Security Configuration** (`config/SecurityConfig.java`)
   - Configure JWT filter
   - Set up authentication manager
   - Define public and protected endpoints

6. **Booking Service** (`service/BookingService.java`)
   - Implement conflict detection logic
   - Query for overlapping bookings

### 2. Frontend Setup

#### Key Components

1. **AuthContext** (`contexts/AuthContext.tsx`)
```typescript
interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
}
```

2. **API Service** (`services/api.ts`)
```typescript
// Configure axios with base URL and interceptors
// Add JWT token to all requests
// Handle 401 errors (token expiration)
```

3. **Booking Calendar** (`components/booking/BookingCalendar.tsx`)
   - Display weekly/daily view
   - Show available time slots
   - Highlight booked slots
   - Click to book

4. **Room Management** (`components/admin/RoomList.tsx`)
   - CRUD operations for rooms
   - Admin-only access

### 3. Conflict Detection Algorithm

```java
public boolean hasConflict(Long roomId, LocalDate date, 
                          LocalTime startTime, LocalTime endTime) {
    List<Booking> existingBookings = bookingRepository
        .findByRoomIdAndBookingDate(roomId, date);
    
    for (Booking booking : existingBookings) {
        // Check if time ranges overlap
        if (startTime.isBefore(booking.getEndTime()) && 
            endTime.isAfter(booking.getStartTime())) {
            return true; // Conflict found
        }
    }
    return false; // No conflict
}
```

### 4. API Request Examples

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### Create Room (Admin)
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name":"Conference Room A",
    "location":"Floor 1",
    "capacity":10,
    "description":"Main conference room"
  }'
```

#### Create Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "roomId":1,
    "bookingDate":"2026-02-01",
    "startTime":"09:00",
    "endTime":"10:00",
    "purpose":"Team meeting"
  }'
```

#### Check Availability
```bash
curl -X GET "http://localhost:8080/api/bookings/check-availability?roomId=1&date=2026-02-01&startTime=09:00&endTime=10:00" \
  -H "Authorization: Bearer <token>"
```

## Testing Strategy

### Backend Tests

1. **Unit Tests**
   - Service layer logic
   - Conflict detection algorithm
   - JWT token generation/validation

2. **Integration Tests**
   - API endpoints
   - Database operations
   - Authentication flow

```java
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateBooking() throws Exception {
        // Test implementation
    }
}
```

### Frontend Tests

1. **Component Tests**
   - Render tests
   - User interaction tests
   - Form validation

2. **Integration Tests**
   - API integration
   - Authentication flow
   - Booking flow

```typescript
describe('BookingForm', () => {
  it('should submit booking successfully', async () => {
    // Test implementation
  });
});
```

## Deployment Checklist

### Pre-deployment

- [ ] Update JWT secret in production
- [ ] Configure CORS for production domain
- [ ] Set up H2 file-based database with persistent volume
- [ ] Enable HTTPS
- [ ] Disable H2 console in production
- [ ] Set up database backup strategy
- [ ] Configure logging
- [ ] Add health check endpoints

### Docker Build

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

### IBM Code Engine Deployment

```bash
# Login to IBM Cloud
ibmcloud login

# Target Code Engine project
ibmcloud ce project select --name <project-name>

# Deploy backend
ibmcloud ce application create \
  --name meetingroom-backend \
  --image <registry>/meetingroom-backend:latest \
  --port 8080 \
  --min-scale 1 \
  --max-scale 5 \
  --env JWT_SECRET=<secret> \
  --env SPRING_PROFILES_ACTIVE=prod

# Deploy frontend
ibmcloud ce application create \
  --name meetingroom-frontend \
  --image <registry>/meetingroom-frontend:latest \
  --port 80 \
  --min-scale 1 \
  --max-scale 3
```

## Common Issues and Solutions

### Issue: CORS errors in development
**Solution**: Configure CORS in Spring Boot to allow localhost:5173

### Issue: JWT token expiration
**Solution**: Implement refresh token mechanism or increase expiration time

### Issue: Booking conflicts not detected
**Solution**: Verify time overlap logic and database constraints

### Issue: H2 database file locked
**Solution**: Use `AUTO_SERVER=TRUE` in connection URL

### Issue: Frontend can't connect to backend
**Solution**: Check API base URL configuration and CORS settings

## Performance Optimization Tips

1. **Backend**
   - Add database indexes on frequently queried columns
   - Implement pagination for list endpoints
   - Use connection pooling
   - Cache frequently accessed data

2. **Frontend**
   - Lazy load components
   - Implement virtual scrolling for large lists
   - Optimize bundle size
   - Use React.memo for expensive components

3. **Database**
   - Add indexes: `CREATE INDEX idx_booking_room_date ON booking(room_id, booking_date)`
   - Optimize queries with proper JPA fetch strategies

## Security Best Practices

1. Never commit secrets to version control
2. Use environment variables for sensitive data
3. Implement rate limiting on authentication endpoints
4. Validate all inputs on both client and server
5. Use HTTPS in production
6. Implement CSRF protection
7. Set secure HTTP headers
8. Regular security audits and dependency updates

## Monitoring and Logging

### Backend Logging
```java
@Slf4j
public class BookingService {
    public Booking createBooking(BookingDTO dto) {
        log.info("Creating booking for room {} on {}", 
                 dto.getRoomId(), dto.getBookingDate());
        // Implementation
    }
}
```

### Health Check Endpoint
```java
@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
```

## Next Steps After MVP

1. Add comprehensive error handling
2. Implement audit logging
3. Add email notifications
4. Create admin dashboard with analytics
5. Implement booking approval workflow
6. Add calendar export functionality
7. Mobile responsive improvements
8. Performance monitoring and optimization