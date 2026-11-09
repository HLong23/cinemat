package vn.edu.cinema.dto;

import vn.edu.cinema.entity.Showtime;

import java.util.List;

public record SeatMapDto(Showtime showtime, List<List<SeatDto>> rows) {

    public record SeatDto(String code, boolean taken) {
    }
}
