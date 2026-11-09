package vn.edu.cinema.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.entity.Movie;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.exception.ResourceNotFoundException;
import vn.edu.cinema.repository.MovieRepository;
import vn.edu.cinema.repository.ShowtimeRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository repo;
    private final ShowtimeRepository showtimeRepo;

    public MovieService(MovieRepository repo, ShowtimeRepository showtimeRepo) {
        this.repo = repo;
        this.showtimeRepo = showtimeRepo;
    }

    /** Danh sách cho người xem: chỉ phim đang chiếu, có thể tìm theo tên. */
    public List<Movie> search(String q) {
        if (q == null || q.isBlank()) {
            return repo.findByActiveTrueOrderByIdDesc();
        }
        return repo.findByActiveTrueAndTitleContainingIgnoreCaseOrderByIdDesc(q.trim());
    }

    /** Danh sách cho quản trị: gồm cả phim đã ẩn. */
    public List<Movie> findAll() {
        return repo.findAll();
    }

    public Movie get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim #" + id));
    }

    /** Lấy theo danh sách id, giữ nguyên thứ tự id truyền vào. */
    public List<Movie> findByIds(List<Long> ids) {
        Map<Long, Movie> byId = repo.findAllById(ids).stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));
        return ids.stream().map(byId::get).filter(m -> m != null && m.isActive()).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Movie create(Movie movie) {
        movie.setId(null);
        return repo.save(movie);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Movie update(Long id, Movie data) {
        Movie m = get(id);
        m.setTitle(data.getTitle());
        m.setDescription(data.getDescription());
        m.setGenre(data.getGenre());
        m.setDurationMinutes(data.getDurationMinutes());
        m.setPosterUrl(data.getPosterUrl());
        m.setReleaseDate(data.getReleaseDate());
        m.setActive(data.isActive());
        return repo.save(m);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Movie m = get(id);
        if (showtimeRepo.existsByMovieId(id)) {
            throw new BusinessException("Không thể xoá phim đã có suất chiếu. Hãy chuyển sang trạng thái ẩn.");
        }
        repo.delete(m);
    }
}
