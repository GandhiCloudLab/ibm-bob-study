package com.meetingroom.service;

import com.meetingroom.dto.RoomDTO;
import com.meetingroom.exception.ResourceNotFoundException;
import com.meetingroom.model.Room;
import com.meetingroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<RoomDTO> getActiveRooms() {
        return roomRepository.findByIsActiveTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        return convertToDTO(room);
    }

    public RoomDTO createRoom(RoomDTO roomDTO) {
        if (roomRepository.existsByName(roomDTO.getName())) {
            throw new RuntimeException("Room with name '" + roomDTO.getName() + "' already exists");
        }

        Room room = new Room();
        room.setName(roomDTO.getName());
        room.setLocation(roomDTO.getLocation());
        room.setCapacity(roomDTO.getCapacity());
        room.setDescription(roomDTO.getDescription());
        room.setIsActive(roomDTO.getIsActive() != null ? roomDTO.getIsActive() : true);

        room = roomRepository.save(room);
        return convertToDTO(room);
    }

    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        if (!room.getName().equals(roomDTO.getName()) && roomRepository.existsByName(roomDTO.getName())) {
            throw new RuntimeException("Room with name '" + roomDTO.getName() + "' already exists");
        }

        room.setName(roomDTO.getName());
        room.setLocation(roomDTO.getLocation());
        room.setCapacity(roomDTO.getCapacity());
        room.setDescription(roomDTO.getDescription());
        if (roomDTO.getIsActive() != null) {
            room.setIsActive(roomDTO.getIsActive());
        }

        room = roomRepository.save(room);
        return convertToDTO(room);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        
        // Soft delete
        room.setIsActive(false);
        roomRepository.save(room);
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setLocation(room.getLocation());
        dto.setCapacity(room.getCapacity());
        dto.setDescription(room.getDescription());
        dto.setIsActive(room.getIsActive());
        return dto;
    }
}

// Made with Bob
