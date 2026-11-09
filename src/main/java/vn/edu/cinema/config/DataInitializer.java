package vn.edu.cinema.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.entity.*;
import vn.edu.cinema.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Nạp dữ liệu mẫu khi DB còn trống: 2 tài khoản, 6 phim, 2 rạp, 3 phòng và các suất chiếu 3 ngày tới. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final MovieRepository movieRepo;
    private final CinemaRepository cinemaRepo;
    private final RoomRepository roomRepo;
    private final ShowtimeRepository showtimeRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository userRepo, MovieRepository movieRepo, CinemaRepository cinemaRepo,
                           RoomRepository roomRepo, ShowtimeRepository showtimeRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.movieRepo = movieRepo;
        this.cinemaRepo = cinemaRepo;
        this.roomRepo = roomRepo;
        this.showtimeRepo = showtimeRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() == 0) {
            userRepo.save(user("admin", "admin123", "Quản trị viên", "admin@cinemat.local", Role.ADMIN));
            userRepo.save(user("user", "user123", "Khách hàng mẫu", "user@cinemat.local", Role.USER));
        }
        if (movieRepo.count() > 0) {
            return;
        }
        List<Movie> movies = new ArrayList<>();
        movies.add(movie("Inception", "Hành động, Khoa học viễn tưởng", 148, LocalDate.of(2010, 7, 16),
                "Một tên trộm chuyên đánh cắp bí mật trong giấc mơ nhận nhiệm vụ cấy một ý tưởng vào tâm trí mục tiêu."));
        movies.add(movie("Interstellar", "Khoa học viễn tưởng", 169, LocalDate.of(2014, 11, 7),
                "Nhóm nhà thám hiểm băng qua lỗ giun để tìm hành tinh mới cho nhân loại."));
        movies.add(movie("Spirited Away", "Hoạt hình, Giả tưởng", 125, LocalDate.of(2001, 7, 20),
                "Cô bé Chihiro lạc vào thế giới các vị thần và phải tìm cách cứu cha mẹ."));
        movies.add(movie("Parasite", "Hài đen, Tâm lý", 132, LocalDate.of(2019, 5, 30),
                "Gia đình nghèo từng bước len lỏi vào cuộc sống của một gia đình giàu có."));
        movies.add(movie("The Dark Knight", "Hành động, Tội phạm", 152, LocalDate.of(2008, 7, 18),
                "Batman đối đầu Joker, kẻ gieo rắc hỗn loạn khắp thành phố Gotham."));
        movies.add(movie("Your Name", "Hoạt hình, Lãng mạn", 106, LocalDate.of(2016, 8, 26),
                "Hai người trẻ xa lạ bỗng hoán đổi cơ thể cho nhau và tìm cách gặp mặt."));
        movieRepo.saveAll(movies);

        Cinema a = cinemaRepo.save(cinema("Cinemat Quận 1", "12 Nguyễn Huệ, Quận 1, TP.HCM"));
        Cinema b = cinemaRepo.save(cinema("Cinemat Cầu Giấy", "88 Trần Thái Tông, Cầu Giấy, Hà Nội"));
        List<Room> rooms = roomRepo.saveAll(List.of(
                room("Phòng 1", 8, 10, a), room("Phòng 2", 6, 8, a), room("Phòng 1", 8, 12, b)));

        LocalDate today = LocalDate.now();
        List<Showtime> showtimes = new ArrayList<>();
        for (int day = 1; day <= 3; day++) {
            for (int r = 0; r < rooms.size(); r++) {
                for (int slot = 0; slot < 3; slot++) {
                    Showtime s = new Showtime();
                    s.setMovie(movies.get((r * 3 + slot + day) % movies.size()));
                    s.setRoom(rooms.get(r));
                    s.setStartTime(LocalDateTime.of(today.plusDays(day), java.time.LocalTime.of(10 + slot * 4, 0)));
                    s.setPrice(80000 + slot * 20000L);
                    showtimes.add(s);
                }
            }
        }
        showtimeRepo.saveAll(showtimes);
    }

    private AppUser user(String username, String password, String fullName, String email, Role role) {
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setFullName(fullName);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }

    private Movie movie(String title, String genre, int minutes, LocalDate release, String description) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setGenre(genre);
        m.setDurationMinutes(minutes);
        m.setReleaseDate(release);
        m.setDescription(description);
        m.setActive(true);
        return m;
    }

    private Cinema cinema(String name, String address) {
        Cinema c = new Cinema();
        c.setName(name);
        c.setAddress(address);
        return c;
    }

    private Room room(String name, int rows, int perRow, Cinema cinema) {
        Room r = new Room();
        r.setName(name);
        r.setNumRows(rows);
        r.setSeatsPerRow(perRow);
        r.setCinema(cinema);
        return r;
    }
}
