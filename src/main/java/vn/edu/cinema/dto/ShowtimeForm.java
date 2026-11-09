package vn.edu.cinema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import vn.edu.cinema.entity.Showtime;

import java.time.LocalDateTime;

public class ShowtimeForm {

    private Long id;

    @NotNull(message = "Vui lòng chọn phim")
    private Long movieId;

    @NotNull(message = "Vui lòng chọn phòng")
    private Long roomId;

    @NotNull(message = "Vui lòng chọn giờ chiếu")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @Min(value = 0, message = "Giá vé không được âm")
    private long price = 90000;

    public static ShowtimeForm from(Showtime s) {
        ShowtimeForm f = new ShowtimeForm();
        f.id = s.getId();
        f.movieId = s.getMovie().getId();
        f.roomId = s.getRoom().getId();
        f.startTime = s.getStartTime();
        f.price = s.getPrice();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
}
