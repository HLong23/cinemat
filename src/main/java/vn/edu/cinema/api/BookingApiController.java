package vn.edu.cinema.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.cinema.dto.BookingDto;
import vn.edu.cinema.dto.BookingRequest;
import vn.edu.cinema.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingService service;

    public BookingApiController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto book(Authentication auth, @Valid @RequestBody BookingRequest request) {
        return service.book(auth.getName(), request.showtimeId(), request.seats());
    }

    @GetMapping("/me")
    public List<BookingDto> mine(Authentication auth) {
        return service.findByUser(auth.getName());
    }

    /** Chỉ ADMIN (kiểm tra bằng @PreAuthorize trong service). */
    @GetMapping
    public List<BookingDto> all() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }
}
