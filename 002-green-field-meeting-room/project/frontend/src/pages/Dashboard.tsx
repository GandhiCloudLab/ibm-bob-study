import React, { useEffect, useState } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  CircularProgress,
} from '@mui/material';
import {
  MeetingRoom,
  CalendarMonth,
  Add,
  AdminPanelSettings,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { roomService } from '@/services/roomService';
import { bookingService } from '@/services/bookingService';
import Layout from '@/components/common/Layout';
import { Room, Booking } from '@/types';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [rooms, setRooms] = useState<Room[]>([]);
  const [myBookings, setMyBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [roomsData, bookingsData] = await Promise.all([
        roomService.getActiveRooms(),
        bookingService.getMyBookings(),
      ]);
      setRooms(roomsData);
      setMyBookings(bookingsData.filter(b => b.status !== 'CANCELLED'));
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
          <CircularProgress />
        </Box>
      </Layout>
    );
  }

  return (
    <Layout>
      <Box>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Welcome! Manage your meeting room bookings efficiently.
        </Typography>

        {/* Stats Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography variant="h4" color="white" fontWeight="bold">
                      {rooms.length}
                    </Typography>
                    <Typography variant="body2" color="rgba(255,255,255,0.9)">
                      Available Rooms
                    </Typography>
                  </Box>
                  <MeetingRoom sx={{ fontSize: 48, color: 'rgba(255,255,255,0.8)' }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{ background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' }}>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography variant="h4" color="white" fontWeight="bold">
                      {myBookings.length}
                    </Typography>
                    <Typography variant="body2" color="rgba(255,255,255,0.9)">
                      My Bookings
                    </Typography>
                  </Box>
                  <CalendarMonth sx={{ fontSize: 48, color: 'rgba(255,255,255,0.8)' }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {isAdmin && (
            <Grid item xs={12} sm={6} md={3}>
              <Card sx={{ background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }}>
                <CardContent>
                  <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Box>
                      <Typography variant="h4" color="white" fontWeight="bold">
                        Admin
                      </Typography>
                      <Typography variant="body2" color="rgba(255,255,255,0.9)">
                        Manage System
                      </Typography>
                    </Box>
                    <AdminPanelSettings sx={{ fontSize: 48, color: 'rgba(255,255,255,0.8)' }} />
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          )}
        </Grid>

        {/* Quick Actions */}
        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom fontWeight="bold">
              Quick Actions
            </Typography>
            <Grid container spacing={2}>
              <Grid item>
                <Button
                  variant="contained"
                  startIcon={<Add />}
                  onClick={() => navigate('/bookings')}
                  size="large"
                >
                  New Booking
                </Button>
              </Grid>
              <Grid item>
                <Button
                  variant="outlined"
                  startIcon={<CalendarMonth />}
                  onClick={() => navigate('/my-bookings')}
                  size="large"
                >
                  View My Bookings
                </Button>
              </Grid>
              {isAdmin && (
                <Grid item>
                  <Button
                    variant="outlined"
                    startIcon={<MeetingRoom />}
                    onClick={() => navigate('/rooms')}
                    size="large"
                  >
                    Manage Rooms
                  </Button>
                </Grid>
              )}
            </Grid>
          </CardContent>
        </Card>

        {/* Recent Bookings */}
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom fontWeight="bold">
              My Recent Bookings
            </Typography>
            {myBookings.length === 0 ? (
              <Typography color="text.secondary">
                No bookings yet. Create your first booking to get started!
              </Typography>
            ) : (
              <Box>
                {myBookings.slice(0, 5).map((booking) => (
                  <Box
                    key={booking.id}
                    sx={{
                      p: 2,
                      mb: 1,
                      border: '1px solid',
                      borderColor: 'divider',
                      borderRadius: 1,
                      '&:hover': {
                        backgroundColor: 'action.hover',
                      },
                    }}
                  >
                    <Typography variant="subtitle1" fontWeight="bold">
                      {booking.roomName}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {booking.bookingDate} • {booking.startTime} - {booking.endTime}
                    </Typography>
                    {booking.purpose && (
                      <Typography variant="body2" color="text.secondary">
                        {booking.purpose}
                      </Typography>
                    )}
                  </Box>
                ))}
              </Box>
            )}
          </CardContent>
        </Card>
      </Box>
    </Layout>
  );
};

export default Dashboard;

// Made with Bob
