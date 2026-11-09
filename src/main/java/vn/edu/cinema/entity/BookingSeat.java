package vn.edu.cinema.entity;

import jakarta.persistence.*;

/**
 * Mỗi dòng giữ chỗ một ghế của một suất chiếu. Ràng buộc unique (showtime, seat)
 * đảm bảo không thể đặt trùng ghế dù có nhiều người đặt cùng lúc.
 */
@Entity
@Table(name = "booking_seats",
        uniqueConstraints = @UniqueConstraint(name = "uk_showtime_seat", columnNames = {"showtime_id", "seat_code"}))
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(name = "seat_code", nullable = false, length = 5)
    private String seatCode;

    public BookingSeat() {
    }

    public BookingSeat(Booking booking, Showtime showtime, String seatCode) {
        this.booking = booking;
        this.showtime = showtime;
        this.seatCode = seatCode;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public Showtime getShowtime() { return showtime; }
    public String getSeatCode() { return seatCode; }
}
