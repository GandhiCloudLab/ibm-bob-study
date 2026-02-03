import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './contexts/AuthContext';
import Login from './components/auth/Login';
import Dashboard from './pages/Dashboard';
import RoomManagement from './pages/RoomManagement';
import CreateBooking from './pages/CreateBooking';
import MyBookings from './components/booking/MyBookings';
import AllBookings from './pages/AllBookings';
import { CircularProgress, Box } from '@mui/material';

const PrivateRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }
  
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

const AdminRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, user, loading } = useAuth();
  
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }
  
  if (user?.role !== 'ADMIN') {
    return <Navigate to="/dashboard" />;
  }
  
  return <>{children}</>;
};

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/dashboard" element={
        <PrivateRoute>
          <Dashboard />
        </PrivateRoute>
      } />
      <Route path="/rooms" element={
        <AdminRoute>
          <RoomManagement />
        </AdminRoute>
      } />
      <Route path="/bookings/create" element={
        <PrivateRoute>
          <CreateBooking />
        </PrivateRoute>
      } />
      <Route path="/bookings/my" element={
        <PrivateRoute>
          <MyBookings />
        </PrivateRoute>
      } />
      <Route path="/bookings/all" element={
        <PrivateRoute>
          <AllBookings />
        </PrivateRoute>
      } />
      <Route path="/" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
}

export default App;

// Made with Bob
