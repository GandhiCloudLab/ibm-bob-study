export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
}

export interface Room {
  id?: number;
  name: string;
  location: string;
  capacity: number;
  description?: string;
  isActive?: boolean;
}

export interface Booking {
  id?: number;
  roomId: number;
  userId?: number;
  roomName?: string;
  userName?: string;
  bookingDate: string;
  startTime: string;
  endTime: string;
  purpose?: string;
  status?: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
}

export interface BookingFormData {
  roomId: number;
  bookingDate: Date | null;
  startTime: Date | null;
  endTime: Date | null;
  purpose: string;
}

// Made with Bob
