package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.cinema.entity.Booking;
import vn.edu.cinema.entity.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserUsernameOrderByCreatedAtDesc(String username);
    List<Booking> findAllByOrderByCreatedAtDesc();
    boolean existsByShowtimeId(Long showtimeId);
    long countByStatus(BookingStatus status);

    @Query("select coalesce(sum(b.totalPrice), 0) from Booking b where b.status = :status")
    long sumRevenue(@Param("status") BookingStatus status);
}
