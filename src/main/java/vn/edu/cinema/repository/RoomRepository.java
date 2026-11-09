package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.cinema.entity.Room;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCinemaId(Long cinemaId);
    boolean existsByCinemaId(Long cinemaId);
}
