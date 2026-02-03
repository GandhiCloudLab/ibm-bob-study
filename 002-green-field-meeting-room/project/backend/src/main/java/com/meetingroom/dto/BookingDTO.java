package com.meetingroom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    
    private Long id;
    
    @NotNull
    private Long roomId;
    
    private Long userId;
    
    private String roomName;
    
    private String userName;
    
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;
    
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    @Size(max = 500)
    private String purpose;
    
    private String status;
}

// Made with Bob
