package com.meetingroom.repository;

import com.meetingroom.model.Booking;
import com.meetingroom.model.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByRoomId(Long roomId);
    
    List<Booking> findByRoomIdAndBookingDate(Long roomId, LocalDate bookingDate);
    
    List<Booking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Booking> findByStatus(BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.bookingDate = :date " +
           "AND b.status != 'CANCELLED' " +
           "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(
        @Param("roomId") Long roomId,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.bookingDate = :date " +
           "AND b.status != 'CANCELLED' " +
           "AND b.id != :bookingId " +
           "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookingsExcludingCurrent(
        @Param("roomId") Long roomId,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime,
        @Param("bookingId") Long bookingId
    );
}

// Made with Bob
