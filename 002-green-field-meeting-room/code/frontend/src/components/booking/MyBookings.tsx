import React, { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Chip,
  Grid,
  Alert,
  Snackbar,
  CircularProgress,
} from '@mui/material';
import { CalendarMonth, Delete, AccessTime } from '@mui/icons-material';
import Layout from '@/components/common/Layout';
import { bookingService } from '@/services/bookingService';
import { Booking } from '@/types';

const MyBookings: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });

  useEffect(() => {
    loadBookings();
  }, []);

  const loadBookings = async () => {
    try {
      const data = await bookingService.getMyBookings();
      setBookings(data.sort((a, b) => 
        new Date(b.bookingDate).getTime() - new Date(a.bookingDate).getTime()
      ));
    } catch (error) {
      console.error('Error loading bookings:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id: number) => {
    if (window.confirm('Are you sure you want to cancel this booking?')) {
      try {
        await bookingService.cancelBooking(id);
        setSnackbar({ open: true, message: 'Booking cancelled successfully', severity: 'success' });
        loadBookings();
      } catch (error: any) {
        setSnackbar({ open: true, message: error.response?.data?.message || 'Cancel failed', severity: 'error' });
      }
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'success';
      case 'PENDING': return 'warning';
      case 'CANCELLED': return 'error';
      default: return 'default';
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
          My Bookings
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          View and manage your meeting room bookings
        </Typography>

        {bookings.length === 0 ? (
          <Alert severity="info">
            You don't have any bookings yet. Create your first booking to get started!
          </Alert>
        ) : (
          <Grid container spacing={3}>
            {bookings.map((booking) => (
              <Grid item xs={12} md={6} key={booking.id}>
                <Card>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="start" mb={2}>
                      <Typography variant="h6" fontWeight="bold">
                        {booking.roomName}
                      </Typography>
                      <Chip
                        label={booking.status}
                        color={getStatusColor(booking.status!)}
                        size="small"
                      />
                    </Box>

                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                      <CalendarMonth fontSize="small" color="action" />
                      <Typography variant="body2" color="text.secondary">
                        {booking.bookingDate}
                      </Typography>
                    </Box>

                    <Box display="flex" alignItems="center" gap={1} mb={2}>
                      <AccessTime fontSize="small" color="action" />
                      <Typography variant="body2" color="text.secondary">
                        {booking.startTime} - {booking.endTime}
                      </Typography>
                    </Box>

                    {booking.purpose && (
                      <Typography variant="body2" color="text.secondary" mb={2}>
                        Purpose: {booking.purpose}
                      </Typography>
                    )}

                    {booking.status !== 'CANCELLED' && (
                      <Button
                        variant="outlined"
                        color="error"
                        startIcon={<Delete />}
                        onClick={() => handleCancel(booking.id!)}
                        size="small"
                      >
                        Cancel Booking
                      </Button>
                    )}
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}

        <Snackbar
          open={snackbar.open}
          autoHideDuration={6000}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          <Alert severity={snackbar.severity}>
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Box>
    </Layout>
  );
};

export default MyBookings;

// Made with Bob
