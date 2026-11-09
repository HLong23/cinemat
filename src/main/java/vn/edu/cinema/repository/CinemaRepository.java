package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.cinema.entity.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
}
