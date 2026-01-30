package com.meetingroom.service;

import com.meetingroom.dto.BookingDTO;
import com.meetingroom.exception.BookingConflictException;
import com.meetingroom.exception.ResourceNotFoundException;
import com.meetingroom.model.Booking;
import com.meetingroom.model.Room;
import com.meetingroom.model.User;
import com.meetingroom.repository.BookingRepository;
import com.meetingroom.repository.RoomRepository;
import com.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<BookingDTO> getMyBookings() {
        User currentUser = authService.getCurrentUser();
        return bookingRepository.findByUserId(currentUser.getId()).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByRoom(Long roomId) {
        return bookingRepository.findByRoomId(roomId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return convertToDTO(booking);
    }

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        // Validate room exists
        Room room = roomRepository.findById(bookingDTO.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", bookingDTO.getRoomId()));

        // Validate date is not in the past
        if (bookingDTO.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book for past dates");
        }

        // Validate time range
        if (bookingDTO.getStartTime().isAfter(bookingDTO.getEndTime()) || 
            bookingDTO.getStartTime().equals(bookingDTO.getEndTime())) {
            throw new IllegalArgumentException("Invalid time range");
        }

        // Check for conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            bookingDTO.getRoomId(),
            bookingDTO.getBookingDate(),
            bookingDTO.getStartTime(),
            bookingDTO.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException(
                "Room is already booked for the selected time slot. Please choose a different time."
            );
        }

        // Create booking
        User currentUser = authService.getCurrentUser();
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setPurpose(bookingDTO.getPurpose());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        booking = bookingRepository.save(booking);
        return convertToDTO(booking);
    }

    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        User currentUser = authService.getCurrentUser();
        
        // Check if user is owner or admin
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to update this booking");
        }

        // Validate date is not in the past
        if (bookingDTO.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book for past dates");
        }

        // Validate time range
        if (bookingDTO.getStartTime().isAfter(bookingDTO.getEndTime()) || 
            bookingDTO.getStartTime().equals(bookingDTO.getEndTime())) {
            throw new IllegalArgumentException("Invalid time range");
        }

        // Check for conflicts (excluding current booking)
        List<Booking> conflicts = bookingRepository.findConflictingBookingsExcludingCurrent(
            bookingDTO.getRoomId(),
            bookingDTO.getBookingDate(),
            bookingDTO.getStartTime(),
            bookingDTO.getEndTime(),
            id
        );

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException(
                "Room is already booked for the selected time slot. Please choose a different time."
            );
        }

        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setPurpose(bookingDTO.getPurpose());

        booking = bookingRepository.save(booking);
        return convertToDTO(booking);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        User currentUser = authService.getCurrentUser();
        
        // Check if user is owner or admin
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to cancel this booking");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public boolean checkAvailability(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            roomId, date, startTime, endTime
        );
        return conflicts.isEmpty();
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomName(booking.getRoom().getName());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setPurpose(booking.getPurpose());
        dto.setStatus(booking.getStatus().name());
        return dto;
    }
}

// Made with Bob
