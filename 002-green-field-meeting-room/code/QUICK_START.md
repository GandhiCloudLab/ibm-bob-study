# Quick Start Guide - Meeting Room Booking Application

## 🚀 Getting Started

### Backend Setup (Spring Boot)

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

3. **Verify it's running**
   - API: http://localhost:8080/api
   - H2 Console: http://localhost:8080/h2-console
   - Swagger UI: http://localhost:8080/swagger-ui.html

### Default Test Accounts

The application comes with pre-configured test accounts:

- **Admin Account**
  - Username: `admin`
  - Password: `admin123`
  - Can manage rooms and view all bookings

- **User Account**
  - Username: `user`
  - Password: `user123`
  - Can create and manage own bookings

### Sample Rooms

Three sample rooms are automatically created:
1. Conference Room A (Floor 1, capacity: 10)
2. Meeting Room B (Floor 2, capacity: 6)
3. Board Room (Floor 3, capacity: 15)

## 📝 Testing the API

### 1. Login and Get Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@meetingroom.com",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

Save the token for subsequent requests!

### 2. List All Rooms

```bash
curl -X GET http://localhost:8080/api/rooms \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Create a Booking

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "roomId": 1,
    "bookingDate": "2026-02-15",
    "startTime": "09:00",
    "endTime": "10:00",
    "purpose": "Team standup meeting"
  }'
```

### 4. Check Room Availability

```bash
curl -X GET "http://localhost:8080/api/bookings/check-availability?roomId=1&date=2026-02-15&startTime=14:00&endTime=15:00" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. View My Bookings

```bash
curl -X GET http://localhost:8080/api/bookings/my-bookings \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 6. Create a Room (Admin Only)

```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "Innovation Lab",
    "location": "Floor 4, Building B",
    "capacity": 12,
    "description": "Creative workspace with whiteboards and collaboration tools",
    "isActive": true
  }'
```

## 🧪 Testing Conflict Detection

### Scenario: Try to book the same room at overlapping times

1. **Create first booking**
   ```bash
   curl -X POST http://localhost:8080/api/bookings \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN_HERE" \
     -d '{
       "roomId": 1,
       "bookingDate": "2026-02-20",
       "startTime": "10:00",
       "endTime": "11:00",
       "purpose": "First meeting"
     }'
   ```

2. **Try to create overlapping booking** (This should fail!)
   ```bash
   curl -X POST http://localhost:8080/api/bookings \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN_HERE" \
     -d '{
       "roomId": 1,
       "bookingDate": "2026-02-20",
       "startTime": "10:30",
       "endTime": "11:30",
       "purpose": "Second meeting"
     }'
   ```

   **Expected Response:**
   ```json
   {
     "status": 409,
     "message": "Room is already booked for the selected time slot. Please choose a different time.",
     "timestamp": "2026-01-30T02:30:00"
   }
   ```

## 🔍 H2 Database Console

Access the H2 console to view data directly:

1. Go to: http://localhost:8080/h2-console
2. Use these settings:
   - JDBC URL: `jdbc:h2:file:./data/meetingroom`
   - User Name: `sa`
   - Password: (leave empty)
3. Click "Connect"

### Useful SQL Queries

```sql
-- View all users
SELECT * FROM USERS;

-- View all rooms
SELECT * FROM ROOMS;

-- View all bookings with details
SELECT b.*, u.full_name as user_name, r.name as room_name 
FROM BOOKINGS b 
JOIN USERS u ON b.user_id = u.id 
JOIN ROOMS r ON b.room_id = r.id;

-- Check for booking conflicts
SELECT * FROM BOOKINGS 
WHERE room_id = 1 
  AND booking_date = '2026-02-20' 
  AND status != 'CANCELLED';
```

## 📚 API Documentation

### Swagger UI
Visit http://localhost:8080/swagger-ui.html for interactive API documentation.

### Key Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/auth/me` - Get current user

#### Rooms
- `GET /api/rooms` - List all rooms
- `GET /api/rooms/active` - List active rooms
- `GET /api/rooms/{id}` - Get room details
- `POST /api/rooms` - Create room (Admin)
- `PUT /api/rooms/{id}` - Update room (Admin)
- `DELETE /api/rooms/{id}` - Delete room (Admin)

#### Bookings
- `GET /api/bookings` - List all bookings
- `GET /api/bookings/my-bookings` - My bookings
- `GET /api/bookings/room/{roomId}` - Room bookings
- `GET /api/bookings/{id}` - Get booking
- `POST /api/bookings` - Create booking
- `PUT /api/bookings/{id}` - Update booking
- `DELETE /api/bookings/{id}` - Cancel booking
- `GET /api/bookings/check-availability` - Check availability

## 🐛 Troubleshooting

### Port Already in Use
If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
```

### Database Locked
If you see "Database may be already in use", stop all running instances:
```bash
# Find process using port 8080
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process
kill -9 <PID>  # Mac/Linux
taskkill /PID <PID> /F  # Windows
```

### Maven Build Issues
Clear Maven cache and rebuild:
```bash
./mvnw clean install -U
```

## 🎯 Next Steps

### Frontend Development
1. Set up React application with Vite
2. Implement authentication flow
3. Create room management UI (Admin)
4. Create booking interface (Users)
5. Add calendar view for bookings

### Deployment
1. Build Docker image
2. Deploy to IBM Code Engine
3. Configure environment variables
4. Set up persistent storage

## 📞 Support

For issues or questions:
1. Check the logs: `./mvnw spring-boot:run` shows detailed logs
2. Review BACKEND_IMPLEMENTATION_SUMMARY.md
3. Check ARCHITECTURE.md for system design
4. Review IMPLEMENTATION_GUIDE.md for detailed steps

## ✅ Verification Checklist

- [ ] Backend starts without errors
- [ ] Can access H2 console
- [ ] Can login with admin credentials
- [ ] Can create a room (as admin)
- [ ] Can create a booking
- [ ] Conflict detection works
- [ ] Can view bookings
- [ ] Can cancel booking
- [ ] Swagger UI is accessible

Happy coding! 🚀