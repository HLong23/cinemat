package vn.edu.cinema.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Giỏ ghế đang chọn - lưu trong HttpSession cho tới khi người dùng xác nhận. */
public class BookingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long showtimeId;
    private final List<String> seats;

    public BookingCart(Long showtimeId, List<String> seats) {
        this.showtimeId = showtimeId;
        this.seats = new ArrayList<>(seats);
    }

    public Long getShowtimeId() { return showtimeId; }
    public List<String> getSeats() { return seats; }
}
