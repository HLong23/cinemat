package vn.edu.cinema.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.cinema.dto.SeatMapDto;
import vn.edu.cinema.dto.ShowtimeForm;
import vn.edu.cinema.entity.Showtime;
import vn.edu.cinema.service.ShowtimeService;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeApiController {

    private final ShowtimeService service;

    public ShowtimeApiController(ShowtimeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Showtime> list(@RequestParam(required = false) Long movieId) {
        return movieId == null ? service.findAll() : service.findUpcomingByMovie(movieId);
    }

    @GetMapping("/{id}")
    public Showtime get(@PathVariable Long id) {
        return service.get(id);
    }

    /** Sơ đồ ghế của suất chiếu, mỗi ghế có cờ taken (đã được đặt). */
    @GetMapping("/{id}/seats")
    public SeatMapDto seats(@PathVariable Long id) {
        return service.seatMap(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Showtime create(@Valid @RequestBody ShowtimeForm form) {
        return service.create(form);
    }

    @PutMapping("/{id}")
    public Showtime update(@PathVariable Long id, @Valid @RequestBody ShowtimeForm form) {
        return service.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
