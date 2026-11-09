package vn.edu.cinema.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.dto.BookingDto;
import vn.edu.cinema.entity.AppUser;
import vn.edu.cinema.entity.Booking;
import vn.edu.cinema.entity.BookingSeat;
import vn.edu.cinema.entity.BookingStatus;
import vn.edu.cinema.entity.Showtime;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.exception.ResourceNotFoundException;
import vn.edu.cinema.repository.BookingRepository;
import vn.edu.cinema.repository.BookingSeatRepository;
import vn.edu.cinema.repository.ShowtimeRepository;
import vn.edu.cinema.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookingService {

    public static final int MAX_SEATS_PER_BOOKING = 8;

    private final BookingRepository bookingRepo;
    private final BookingSeatRepository bookingSeatRepo;
    private final ShowtimeRepository showtimeRepo;
    private final UserRepository userRepo;

    public BookingService(BookingRepository bookingRepo, BookingSeatRepository bookingSeatRepo,
                          ShowtimeRepository showtimeRepo, UserRepository userRepo) {
        this.bookingRepo = bookingRepo;
        this.bookingSeatRepo = bookingSeatRepo;
        this.showtimeRepo = showtimeRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public BookingDto book(String username, Long showtimeId, List<String> seatCodes) {
        if (seatCodes == null || seatCodes.isEmpty()) {
            throw new BusinessException("Vui lòng chọn ít nhất một ghế");
        }
        Set<String> seats = new LinkedHashSet<>();
        for (String s : seatCodes) {
            seats.add(s == null ? "" : s.trim().toUpperCase());
        }
        if (seats.size() > MAX_SEATS_PER_BOOKING) {
            throw new BusinessException("Mỗi lần đặt tối đa " + MAX_SEATS_PER_BOOKING + " ghế");
        }

        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng " + username));
        Showtime st = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu #" + showtimeId));

        if (!st.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Suất chiếu đã bắt đầu, không thể đặt vé");
        }
        for (String s : seats) {
            if (!st.getRoom().hasSeat(s)) {
                throw new BusinessException("Ghế không hợp lệ: " + s);
            }
        }
        Set<String> taken = new HashSet<>(bookingSeatRepo.findSeatCodesByShowtimeId(showtimeId));
        for (String s : seats) {
            if (taken.contains(s)) {
                throw new BusinessException("Ghế " + s + " đã có người đặt");
            }
        }

        Booking b = new Booking();
        b.setUser(user);
        b.setShowtime(st);
        b.setSeatCodes(String.join(",", seats));
        b.setTotalPrice(st.getPrice() * seats.size());
        b.setStatus(BookingStatus.CONFIRMED);
        b.setCreatedAt(LocalDateTime.now());
        b = bookingRepo.save(b);

        try {
            List<BookingSeat> rows = new java.util.ArrayList<>();
            for (String s : seats) {
                rows.add(new BookingSeat(b, st, s));
            }
            bookingSeatRepo.saveAllAndFlush(rows);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Ghế vừa được người khác đặt, vui lòng chọn lại");
        }
        return toDto(b);
    }

    /** Chỉ chủ vé hoặc ADMIN được huỷ. */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    public void cancel(@P("id") Long id) {
        Booking b = bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé #" + id));
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Vé đã được huỷ trước đó");
        }
        if (!b.getShowtime().getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Suất chiếu đã bắt đầu, không thể huỷ vé");
        }
        bookingSeatRepo.deleteByBookingId(id);
        b.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(b);
    }

    /** Dùng trong biểu thức @PreAuthorize. Vé không tồn tại thì để cancel() báo 404. */
    public boolean isOwner(Long bookingId, String username) {
        return bookingRepo.findById(bookingId)
                .map(b -> b.getUser().getUsername().equals(username))
                .orElse(true);
    }

    @PreAuthorize("isAuthenticated()")
    public List<BookingDto> findByUser(String username) {
        return bookingRepo.findByUserUsernameOrderByCreatedAtDesc(username).stream().map(this::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> findAll() {
        return bookingRepo.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long countConfirmed() {
        return bookingRepo.countByStatus(BookingStatus.CONFIRMED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long revenue() {
        return bookingRepo.sumRevenue(BookingStatus.CONFIRMED);
    }

    private BookingDto toDto(Booking b) {
        Showtime st = b.getShowtime();
        return new BookingDto(
                b.getId(),
                b.getUser().getUsername(),
                st.getId(),
                st.getMovie().getTitle(),
                st.getRoom().getCinema().getName(),
                st.getRoom().getName(),
                st.getStartTime(),
                Arrays.asList(b.getSeatCodes().split(",")),
                b.getTotalPrice(),
                b.getStatus(),
                b.getCreatedAt());
    }
}
