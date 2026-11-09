package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.cinema.entity.BookingSeat;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("select bs.seatCode from BookingSeat bs where bs.showtime.id = :showtimeId")
    List<String> findSeatCodesByShowtimeId(@Param("showtimeId") Long showtimeId);

    void deleteByBookingId(Long bookingId);
}
