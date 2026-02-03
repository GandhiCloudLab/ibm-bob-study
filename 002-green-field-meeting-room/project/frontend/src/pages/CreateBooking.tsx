import React, { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  MenuItem,
  Alert,
  Snackbar,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import Layout from '@/components/common/Layout';
import { roomService } from '@/services/roomService';
import { bookingService } from '@/services/bookingService';
import { Room } from '@/types';

const CreateBooking: React.FC = () => {
  const navigate = useNavigate();
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  
  const [formData, setFormData] = useState({
    roomId: '',
    bookingDate: '',
    startTime: '',
    endTime: '',
    purpose: '',
  });

  const [errors, setErrors] = useState({
    roomId: '',
    bookingDate: '',
    startTime: '',
    endTime: '',
  });

  useEffect(() => {
    loadRooms();
  }, []);

  const loadRooms = async () => {
    try {
      const data = await roomService.getAllRooms();
      setRooms(data.filter(room => room.isActive));
    } catch (error) {
      console.error('Error loading rooms:', error);
      setSnackbar({ open: true, message: 'Failed to load rooms', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const validateForm = (): boolean => {
    const newErrors = {
      roomId: '',
      bookingDate: '',
      startTime: '',
      endTime: '',
    };

    if (!formData.roomId) {
      newErrors.roomId = 'Please select a room';
    }

    if (!formData.bookingDate) {
      newErrors.bookingDate = 'Please select a date';
    } else {
      const selectedDate = new Date(formData.bookingDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      if (selectedDate < today) {
        newErrors.bookingDate = 'Cannot book for past dates';
      }
    }

    if (!formData.startTime) {
      newErrors.startTime = 'Please select start time';
    }

    if (!formData.endTime) {
      newErrors.endTime = 'Please select end time';
    }

    if (formData.startTime && formData.endTime) {
      if (formData.startTime >= formData.endTime) {
        newErrors.endTime = 'End time must be after start time';
      }
    }

    setErrors(newErrors);
    return !Object.values(newErrors).some(error => error !== '');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    try {
      await bookingService.createBooking({
        roomId: parseInt(formData.roomId),
        bookingDate: formData.bookingDate,
        startTime: formData.startTime,
        endTime: formData.endTime,
        purpose: formData.purpose,
      });

      setSnackbar({ open: true, message: 'Booking created successfully!', severity: 'success' });
      setTimeout(() => {
        navigate('/bookings/my');
      }, 1500);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Failed to create booking';
      setSnackbar({ open: true, message, severity: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleChange = (field: string, value: string) => {
    setFormData({ ...formData, [field]: value });
    setErrors({ ...errors, [field]: '' });
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
          Create New Booking
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Book a meeting room for your team
        </Typography>

        <Card sx={{ maxWidth: 600, mt: 3 }}>
          <CardContent>
            <form onSubmit={handleSubmit}>
              <TextField
                select
                fullWidth
                label="Select Room"
                value={formData.roomId}
                onChange={(e) => handleChange('roomId', e.target.value)}
                error={!!errors.roomId}
                helperText={errors.roomId}
                margin="normal"
                required
              >
                {rooms.map((room) => (
                  <MenuItem key={room.id} value={room.id}>
                    {room.name} - {room.location} (Capacity: {room.capacity})
                  </MenuItem>
                ))}
              </TextField>

              <TextField
                fullWidth
                type="date"
                label="Booking Date"
                value={formData.bookingDate}
                onChange={(e) => handleChange('bookingDate', e.target.value)}
                error={!!errors.bookingDate}
                helperText={errors.bookingDate}
                margin="normal"
                required
                InputLabelProps={{ shrink: true }}
                inputProps={{
                  min: new Date().toISOString().split('T')[0],
                }}
              />

              <TextField
                fullWidth
                type="time"
                label="Start Time"
                value={formData.startTime}
                onChange={(e) => handleChange('startTime', e.target.value)}
                error={!!errors.startTime}
                helperText={errors.startTime}
                margin="normal"
                required
                InputLabelProps={{ shrink: true }}
              />

              <TextField
                fullWidth
                type="time"
                label="End Time"
                value={formData.endTime}
                onChange={(e) => handleChange('endTime', e.target.value)}
                error={!!errors.endTime}
                helperText={errors.endTime}
                margin="normal"
                required
                InputLabelProps={{ shrink: true }}
              />

              <TextField
                fullWidth
                multiline
                rows={3}
                label="Purpose (Optional)"
                value={formData.purpose}
                onChange={(e) => handleChange('purpose', e.target.value)}
                margin="normal"
                placeholder="e.g., Team standup, Client meeting, etc."
              />

              <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                <Button
                  type="submit"
                  variant="contained"
                  fullWidth
                  disabled={submitting}
                >
                  {submitting ? <CircularProgress size={24} /> : 'Create Booking'}
                </Button>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => navigate('/bookings/my')}
                  disabled={submitting}
                >
                  Cancel
                </Button>
              </Box>
            </form>
          </CardContent>
        </Card>

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

export default CreateBooking;

// Made with Bob
