package vn.edu.cinema.dto;

import vn.edu.cinema.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookingDto(Long id,
                         String username,
                         Long showtimeId,
                         String movieTitle,
                         String cinemaName,
                         String roomName,
                         LocalDateTime startTime,
                         List<String> seats,
                         long totalPrice,
                         BookingStatus status,
                         LocalDateTime createdAt) {
}
