package vn.edu.cinema.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(@NotNull Long showtimeId, @NotEmpty List<String> seats) {
}
