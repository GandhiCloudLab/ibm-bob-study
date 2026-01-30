import api from './api';
import { Room } from '@/types';

export const roomService = {
  getAllRooms: async (): Promise<Room[]> => {
    const response = await api.get<Room[]>('/rooms');
    return response.data;
  },

  getActiveRooms: async (): Promise<Room[]> => {
    const response = await api.get<Room[]>('/rooms/active');
    return response.data;
  },

  getRoomById: async (id: number): Promise<Room> => {
    const response = await api.get<Room>(`/rooms/${id}`);
    return response.data;
  },

  createRoom: async (room: Room): Promise<Room> => {
    const response = await api.post<Room>('/rooms', room);
    return response.data;
  },

  updateRoom: async (id: number, room: Room): Promise<Room> => {
    const response = await api.put<Room>(`/rooms/${id}`, room);
    return response.data;
  },

  deleteRoom: async (id: number): Promise<void> => {
    await api.delete(`/rooms/${id}`);
  },
};

// Made with Bob
