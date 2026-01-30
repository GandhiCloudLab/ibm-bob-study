import api from './api';
import { Booking } from '@/types';

// Simple date formatting helper (replaces date-fns)
const formatDate = (date: Date | string): string => {
  if (typeof date === 'string') return date;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const formatTime = (time: Date | string): string => {
  if (typeof time === 'string') return time;
  const d = new Date(time);
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}`;
};

export const bookingService = {
  getAllBookings: async (): Promise<Booking[]> => {
    const response = await api.get<Booking[]>('/bookings');
    return response.data;
  },

  getMyBookings: async (): Promise<Booking[]> => {
    const response = await api.get<Booking[]>('/bookings/my-bookings');
    return response.data;
  },

  getBookingsByRoom: async (roomId: number): Promise<Booking[]> => {
    const response = await api.get<Booking[]>(`/bookings/room/${roomId}`);
    return response.data;
  },

  getBookingById: async (id: number): Promise<Booking> => {
    const response = await api.get<Booking>(`/bookings/${id}`);
    return response.data;
  },

  createBooking: async (booking: Booking): Promise<Booking> => {
    const response = await api.post<Booking>('/bookings', booking);
    return response.data;
  },

  updateBooking: async (id: number, booking: Booking): Promise<Booking> => {
    const response = await api.put<Booking>(`/bookings/${id}`, booking);
    return response.data;
  },

  cancelBooking: async (id: number): Promise<void> => {
    await api.delete(`/bookings/${id}`);
  },

  checkAvailability: async (
    roomId: number,
    date: Date | string,
    startTime: Date | string,
    endTime: Date | string
  ): Promise<boolean> => {
    const response = await api.get<{ available: boolean }>('/bookings/check-availability', {
      params: {
        roomId,
        date: formatDate(date),
        startTime: formatTime(startTime),
        endTime: formatTime(endTime),
      },
    });
    return response.data.available;
  },
};

// Made with Bob
