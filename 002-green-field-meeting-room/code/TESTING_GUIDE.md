# Meeting Room Booking Application - Testing Guide

## Prerequisites

Before testing, ensure you have:
- Java 17+ installed
- Node.js 18+ and npm installed
- Maven installed (or use the included Maven wrapper)

## Quick Start Testing

### 1. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

**Expected Output:**
```
Started MeetingRoomApplication in X.XXX seconds
```

### 2. Start the Frontend

Open a new terminal:

```bash
cd frontend
npm install  # If not already done
npm run dev
```

The frontend will start on `http://localhost:5173`

**Expected Output:**
```
VITE v5.x.x  ready in XXX ms
➜  Local:   http://localhost:5173/
```

### 3. Access the Application

Open your browser and navigate to: `http://localhost:5173`

## Test Credentials

The application comes with pre-configured test users:

### Admin User
- **Username:** `admin`
- **Password:** `admin123`
- **Capabilities:** Full access to all features including room management

### Regular User
- **Username:** `user`
- **Password:** `user123`
- **Capabilities:** Can create bookings and view their own bookings

## Complete Testing Workflow

### Phase 1: Authentication Testing

#### Test 1.1: Admin Login
1. Navigate to `http://localhost:5173`
2. Enter credentials: `admin` / `admin123`
3. Click "Sign In"
4. **Expected:** Redirect to Dashboard with admin navigation options

#### Test 1.2: User Login
1. Logout (click profile icon → Logout)
2. Enter credentials: `user` / `user123`
3. Click "Sign In"
4. **Expected:** Redirect to Dashboard without "Manage Rooms" option

#### Test 1.3: Invalid Login
1. Logout
2. Enter invalid credentials
3. **Expected:** Error message "Invalid username or password"

### Phase 2: Room Management (Admin Only)

#### Test 2.1: Create Room
1. Login as admin
2. Navigate to "Manage Rooms"
3. Click "Add New Room"
4. Fill in details:
   - Name: "Conference Room A"
   - Location: "Building 1, Floor 2"
   - Capacity: 10
   - Description: "Large conference room with projector"
5. Click "Save"
6. **Expected:** Success message and room appears in list

#### Test 2.2: Edit Room
1. Find "Conference Room A" in the list
2. Click "Edit" button
3. Change capacity to 12
4. Click "Save"
5. **Expected:** Success message and updated capacity displayed

#### Test 2.3: Delete Room
1. Click "Delete" on any room
2. Confirm deletion
3. **Expected:** Success message and room removed from list

#### Test 2.4: Create Multiple Rooms
Create at least 3 rooms for booking tests:
- "Meeting Room 1" - Capacity 6
- "Meeting Room 2" - Capacity 8
- "Board Room" - Capacity 15

### Phase 3: Booking Creation

#### Test 3.1: Create Valid Booking
1. Login as user
2. Navigate to "Create Booking"
3. Fill in details:
   - Room: "Meeting Room 1"
   - Date: Tomorrow's date
   - Start Time: 10:00
   - End Time: 11:00
   - Purpose: "Team standup meeting"
4. Click "Create Booking"
5. **Expected:** Success message and redirect to "My Bookings"

#### Test 3.2: Conflict Detection
1. Try to create another booking:
   - Same room: "Meeting Room 1"
   - Same date: Tomorrow
   - Overlapping time: 10:30 - 11:30
2. Click "Create Booking"
3. **Expected:** Error message "Room already booked for selected time"

#### Test 3.3: Different Room Same Time (Should Work)
1. Create booking:
   - Room: "Meeting Room 2"
   - Same date and time as Test 3.1
2. **Expected:** Success - different rooms can be booked simultaneously

#### Test 3.4: Same Room Different Time (Should Work)
1. Create booking:
   - Room: "Meeting Room 1"
   - Same date as Test 3.1
   - Time: 14:00 - 15:00
2. **Expected:** Success - same room at different times

#### Test 3.5: Past Date Validation
1. Try to create booking with yesterday's date
2. **Expected:** Error or date picker prevents past dates

### Phase 4: Booking Management

#### Test 4.1: View My Bookings
1. Navigate to "My Bookings"
2. **Expected:** See all bookings created by current user
3. Verify booking details are correct

#### Test 4.2: Cancel Booking
1. In "My Bookings", click "Cancel Booking"
2. Confirm cancellation
3. **Expected:** Booking status changes to "CANCELLED"

#### Test 4.3: View All Bookings
1. Navigate to "All Bookings"
2. **Expected:** See all bookings from all users
3. Verify you can see bookings from other users

#### Test 4.4: Filter Bookings
1. In "All Bookings", use status filter
2. Select "Confirmed"
3. **Expected:** Only confirmed bookings displayed

### Phase 5: Dashboard Testing

#### Test 5.1: Admin Dashboard
1. Login as admin
2. View Dashboard
3. **Expected:** See statistics cards showing:
   - Total Rooms
   - Total Bookings
   - Active Bookings
   - My Bookings

#### Test 5.2: User Dashboard
1. Login as regular user
2. View Dashboard
3. **Expected:** See relevant statistics for user

### Phase 6: Edge Cases and Error Handling

#### Test 6.1: Network Error Handling
1. Stop the backend server
2. Try to create a booking
3. **Expected:** Error message about connection failure
4. Restart backend and retry

#### Test 6.2: Token Expiration
1. Login and wait for token to expire (default: 24 hours)
2. Or manually clear localStorage
3. Try to access protected route
4. **Expected:** Redirect to login page

#### Test 6.3: Unauthorized Access
1. Login as regular user
2. Try to access `/rooms` directly in URL
3. **Expected:** Redirect to dashboard (no access to admin routes)

#### Test 6.4: Form Validation
1. Try to create booking with:
   - Empty fields
   - End time before start time
   - Invalid date format
2. **Expected:** Appropriate validation errors

### Phase 7: UI/UX Testing

#### Test 7.1: Responsive Design
1. Resize browser window
2. Test on mobile viewport (375px width)
3. **Expected:** UI adapts properly, no horizontal scroll

#### Test 7.2: Navigation
1. Test all navigation links
2. Verify active route highlighting
3. **Expected:** Smooth navigation, correct active states

#### Test 7.3: Loading States
1. Observe loading indicators during API calls
2. **Expected:** Spinners/skeletons shown during data fetch

#### Test 7.4: Success/Error Messages
1. Verify all actions show appropriate feedback
2. **Expected:** Snackbar notifications for all operations

## API Testing (Optional)

### Using cURL

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### Get Rooms (with token)
```bash
curl -X GET http://localhost:8080/api/rooms \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### Create Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "bookingDate": "2026-02-01",
    "startTime": "10:00",
    "endTime": "11:00",
    "purpose": "Team Meeting"
  }'
```

## Database Verification

### Access H2 Console

1. Navigate to: `http://localhost:8080/h2-console`
2. Use these settings:
   - JDBC URL: `jdbc:h2:mem:meetingroom`
   - Username: `sa`
   - Password: (leave empty)
3. Click "Connect"

### Verify Data

```sql
-- Check users
SELECT * FROM users;

-- Check rooms
SELECT * FROM rooms;

-- Check bookings
SELECT * FROM bookings;

-- Check booking conflicts
SELECT b1.*, b2.* 
FROM bookings b1, bookings b2 
WHERE b1.room_id = b2.room_id 
  AND b1.booking_date = b2.booking_date
  AND b1.id != b2.id
  AND b1.start_time < b2.end_time 
  AND b1.end_time > b2.start_time;
```

## Performance Testing

### Load Testing Scenario

1. Create 10+ rooms
2. Create 50+ bookings across different dates
3. Navigate through all pages
4. **Expected:** Smooth performance, no lag

### Concurrent Booking Test

1. Open application in 2 different browsers
2. Login as different users
3. Try to book same room at same time simultaneously
4. **Expected:** One succeeds, other gets conflict error

## Common Issues and Solutions

### Issue 1: Backend Won't Start
**Solution:** Check if port 8080 is already in use
```bash
lsof -i :8080
kill -9 <PID>
```

### Issue 2: Frontend Won't Start
**Solution:** Check if port 5173 is in use, or delete node_modules and reinstall
```bash
rm -rf node_modules package-lock.json
npm install
```

### Issue 3: CORS Errors
**Solution:** Verify backend CORS configuration allows `http://localhost:5173`

### Issue 4: 401 Unauthorized
**Solution:** Token may be expired, logout and login again

### Issue 5: TypeScript Errors
**Solution:** Ensure all dependencies are installed
```bash
cd frontend
npm install
```

## Test Checklist

Use this checklist to track your testing progress:

- [ ] Admin login successful
- [ ] User login successful
- [ ] Invalid login shows error
- [ ] Create room (admin)
- [ ] Edit room (admin)
- [ ] Delete room (admin)
- [ ] Create valid booking
- [ ] Booking conflict detected
- [ ] Cancel booking
- [ ] View my bookings
- [ ] View all bookings
- [ ] Filter bookings by status
- [ ] Dashboard shows correct stats
- [ ] Unauthorized access blocked
- [ ] Form validation works
- [ ] Responsive design works
- [ ] Navigation works correctly
- [ ] Loading states display
- [ ] Success/error messages show
- [ ] Token expiration handled
- [ ] Database data correct

## Success Criteria

The application passes testing if:

1. ✅ All authentication flows work correctly
2. ✅ Admin can manage rooms (CRUD operations)
3. ✅ Users can create bookings
4. ✅ Booking conflicts are detected and prevented
5. ✅ Users can view and cancel their bookings
6. ✅ All bookings are visible in the all bookings page
7. ✅ Dashboard displays accurate statistics
8. ✅ Role-based access control works
9. ✅ UI is responsive and user-friendly
10. ✅ Error handling is graceful

## Next Steps After Testing

Once all tests pass:

1. Review and fix any bugs found
2. Optimize performance if needed
3. Prepare for deployment to IBM Code Engine
4. Set up production database (PostgreSQL)
5. Configure environment variables for production
6. Set up CI/CD pipeline

## Support

If you encounter issues during testing:

1. Check the browser console for errors
2. Check backend logs in the terminal
3. Verify database state in H2 console
4. Review the IMPLEMENTATION_GUIDE.md for setup details
5. Check ARCHITECTURE.md for system design clarification