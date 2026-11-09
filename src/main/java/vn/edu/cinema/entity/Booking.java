package vn.edu.cinema.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    /** Danh sách ghế dạng "A1,A2" - giữ lại để xem lịch sử kể cả khi đã huỷ. */
    @Column(name = "seat_codes", nullable = false, length = 300)
    private String seatCodes;

    @Column(name = "total_price", nullable = false)
    private long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public Showtime getShowtime() { return showtime; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; }
    public String getSeatCodes() { return seatCodes; }
    public void setSeatCodes(String seatCodes) { this.seatCodes = seatCodes; }
    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
