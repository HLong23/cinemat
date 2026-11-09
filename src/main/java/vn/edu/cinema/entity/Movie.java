package vn.edu.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @Size(max = 100)
    @Column(length = 100)
    private String genre;

    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    @Max(value = 600, message = "Thời lượng tối đa 600 phút")
    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 90;

    @Size(max = 500)
    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
