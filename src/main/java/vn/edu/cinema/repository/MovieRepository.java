package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.cinema.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByActiveTrueOrderByIdDesc();
    List<Movie> findByActiveTrueAndTitleContainingIgnoreCaseOrderByIdDesc(String title);
}
