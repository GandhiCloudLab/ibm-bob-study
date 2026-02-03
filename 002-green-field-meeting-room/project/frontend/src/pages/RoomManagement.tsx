import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  CardActions,
  Grid,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Chip,
  Alert,
  Snackbar,
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  People,
  LocationOn,
} from '@mui/icons-material';
import Layout from '@/components/common/Layout';
import { roomService } from '@/services/roomService';
import { Room } from '@/types';

const RoomManagement: React.FC = () => {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [open, setOpen] = useState(false);
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);
  const [formData, setFormData] = useState<Room>({
    name: '',
    location: '',
    capacity: 1,
    description: '',
    isActive: true,
  });
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });

  useEffect(() => {
    loadRooms();
  }, []);

  const loadRooms = async () => {
    try {
      const data = await roomService.getAllRooms();
      setRooms(data);
    } catch (error) {
      console.error('Error loading rooms:', error);
    }
  };

  const handleOpen = (room?: Room) => {
    if (room) {
      setEditingRoom(room);
      setFormData(room);
    } else {
      setEditingRoom(null);
      setFormData({
        name: '',
        location: '',
        capacity: 1,
        description: '',
        isActive: true,
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setEditingRoom(null);
  };

  const handleSubmit = async () => {
    try {
      if (editingRoom) {
        await roomService.updateRoom(editingRoom.id!, formData);
        setSnackbar({ open: true, message: 'Room updated successfully', severity: 'success' });
      } else {
        await roomService.createRoom(formData);
        setSnackbar({ open: true, message: 'Room created successfully', severity: 'success' });
      }
      handleClose();
      loadRooms();
    } catch (error: any) {
      setSnackbar({ open: true, message: error.response?.data?.message || 'Operation failed', severity: 'error' });
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this room?')) {
      try {
        await roomService.deleteRoom(id);
        setSnackbar({ open: true, message: 'Room deleted successfully', severity: 'success' });
        loadRooms();
      } catch (error: any) {
        setSnackbar({ open: true, message: error.response?.data?.message || 'Delete failed', severity: 'error' });
      }
    }
  };

  return (
    <Layout>
      <Box>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h4" fontWeight="bold">
            Room Management
          </Typography>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => handleOpen()}
            size="large"
          >
            Add Room
          </Button>
        </Box>

        <Grid container spacing={3}>
          {rooms.map((room) => (
            <Grid item xs={12} sm={6} md={4} key={room.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="start" mb={2}>
                    <Typography variant="h6" fontWeight="bold">
                      {room.name}
                    </Typography>
                    <Chip
                      label={room.isActive ? 'Active' : 'Inactive'}
                      color={room.isActive ? 'success' : 'default'}
                      size="small"
                    />
                  </Box>
                  
                  <Box display="flex" alignItems="center" gap={1} mb={1}>
                    <LocationOn fontSize="small" color="action" />
                    <Typography variant="body2" color="text.secondary">
                      {room.location}
                    </Typography>
                  </Box>
                  
                  <Box display="flex" alignItems="center" gap={1} mb={2}>
                    <People fontSize="small" color="action" />
                    <Typography variant="body2" color="text.secondary">
                      Capacity: {room.capacity} people
                    </Typography>
                  </Box>
                  
                  {room.description && (
                    <Typography variant="body2" color="text.secondary">
                      {room.description}
                    </Typography>
                  )}
                </CardContent>
                <CardActions>
                  <IconButton onClick={() => handleOpen(room)} color="primary">
                    <Edit />
                  </IconButton>
                  <IconButton onClick={() => handleDelete(room.id!)} color="error">
                    <Delete />
                  </IconButton>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>

        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
          <DialogTitle>
            {editingRoom ? 'Edit Room' : 'Add New Room'}
          </DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Room Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Location"
              value={formData.location}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Capacity"
              type="number"
              value={formData.capacity}
              onChange={(e) => setFormData({ ...formData, capacity: parseInt(e.target.value) })}
              margin="normal"
              required
              inputProps={{ min: 1 }}
            />
            <TextField
              fullWidth
              label="Description"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              margin="normal"
              multiline
              rows={3}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancel</Button>
            <Button onClick={handleSubmit} variant="contained">
              {editingRoom ? 'Update' : 'Create'}
            </Button>
          </DialogActions>
        </Dialog>

        <Snackbar
          open={snackbar.open}
          autoHideDuration={6000}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Box>
    </Layout>
  );
};

export default RoomManagement;

// Made with Bob
