package vn.edu.cinema.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /** Giá vé (VND) cho mỗi ghế. */
    @Column(nullable = false)
    private long price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
}
