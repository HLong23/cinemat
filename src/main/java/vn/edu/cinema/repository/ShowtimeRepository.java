package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.cinema.entity.Showtime;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findAllByOrderByStartTimeAsc();
    List<Showtime> findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(Long movieId, LocalDateTime after);
    List<Showtime> findByRoomId(Long roomId);
    boolean existsByMovieId(Long movieId);
    boolean existsByRoomId(Long roomId);
}
