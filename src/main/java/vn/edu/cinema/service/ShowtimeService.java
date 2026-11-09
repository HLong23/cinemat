package vn.edu.cinema.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.dto.SeatMapDto;
import vn.edu.cinema.dto.SeatMapDto.SeatDto;
import vn.edu.cinema.dto.ShowtimeForm;
import vn.edu.cinema.entity.Movie;
import vn.edu.cinema.entity.Room;
import vn.edu.cinema.entity.Showtime;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.exception.ResourceNotFoundException;
import vn.edu.cinema.repository.BookingRepository;
import vn.edu.cinema.repository.BookingSeatRepository;
import vn.edu.cinema.repository.MovieRepository;
import vn.edu.cinema.repository.RoomRepository;
import vn.edu.cinema.repository.ShowtimeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ShowtimeService {

    /** Thời gian dọn phòng giữa hai suất chiếu (phút). */
    private static final int CLEANING_MINUTES = 15;

    private final ShowtimeRepository repo;
    private final MovieRepository movieRepo;
    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;
    private final BookingSeatRepository bookingSeatRepo;

    public ShowtimeService(ShowtimeRepository repo, MovieRepository movieRepo, RoomRepository roomRepo,
                           BookingRepository bookingRepo, BookingSeatRepository bookingSeatRepo) {
        this.repo = repo;
        this.movieRepo = movieRepo;
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
        this.bookingSeatRepo = bookingSeatRepo;
    }

    public List<Showtime> findAll() {
        return repo.findAllByOrderByStartTimeAsc();
    }

    public List<Showtime> findUpcomingByMovie(Long movieId) {
        return repo.findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(movieId, LocalDateTime.now());
    }

    public Showtime get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu #" + id));
    }

    public SeatMapDto seatMap(Long id) {
        Showtime st = get(id);
        Room room = st.getRoom();
        Set<String> taken = new HashSet<>(bookingSeatRepo.findSeatCodesByShowtimeId(id));
        List<List<SeatDto>> rows = new ArrayList<>();
        for (int r = 0; r < room.getNumRows(); r++) {
            List<SeatDto> row = new ArrayList<>();
            for (int n = 1; n <= room.getSeatsPerRow(); n++) {
                String code = "" + (char) ('A' + r) + n;
                row.add(new SeatDto(code, taken.contains(code)));
            }
            rows.add(row);
        }
        return new SeatMapDto(st, rows);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Showtime create(ShowtimeForm form) {
        Showtime s = new Showtime();
        apply(s, form, null);
        return repo.save(s);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Showtime update(Long id, ShowtimeForm form) {
        Showtime s = get(id);
        if (bookingRepo.existsByShowtimeId(id)) {
            throw new BusinessException("Suất chiếu đã có vé đặt, không thể chỉnh sửa.");
        }
        apply(s, form, id);
        return repo.save(s);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Showtime s = get(id);
        if (bookingRepo.existsByShowtimeId(id)) {
            throw new BusinessException("Suất chiếu đã có vé đặt, không thể xoá.");
        }
        repo.delete(s);
    }

    private void apply(Showtime s, ShowtimeForm f, Long selfId) {
        Movie movie = movieRepo.findById(f.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim #" + f.getMovieId()));
        Room room = roomRepo.findById(f.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng #" + f.getRoomId()));

        LocalDateTime start = f.getStartTime();
        LocalDateTime end = start.plusMinutes(movie.getDurationMinutes() + CLEANING_MINUTES);
        for (Showtime other : repo.findByRoomId(room.getId())) {
            if (other.getId().equals(selfId)) {
                continue;
            }
            LocalDateTime otherEnd = other.getStartTime()
                    .plusMinutes(other.getMovie().getDurationMinutes() + CLEANING_MINUTES);
            if (start.isBefore(otherEnd) && end.isAfter(other.getStartTime())) {
                throw new BusinessException("Phòng đã có suất chiếu trùng giờ: " + other.getMovie().getTitle()
                        + " (" + other.getStartTime() + ")");
            }
        }
        s.setMovie(movie);
        s.setRoom(room);
        s.setStartTime(start);
        s.setPrice(f.getPrice());
    }
}
