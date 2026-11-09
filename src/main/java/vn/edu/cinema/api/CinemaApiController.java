package vn.edu.cinema.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.cinema.entity.Cinema;
import vn.edu.cinema.service.CinemaService;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaApiController {

    private final CinemaService service;

    public CinemaApiController(CinemaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cinema> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Cinema get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cinema create(@Valid @RequestBody Cinema cinema) {
        return service.create(cinema);
    }

    @PutMapping("/{id}")
    public Cinema update(@PathVariable Long id, @Valid @RequestBody Cinema cinema) {
        return service.update(id, cinema);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
