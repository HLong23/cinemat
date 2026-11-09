package vn.edu.cinema.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.cinema.entity.Movie;
import vn.edu.cinema.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieApiController {

    private final MovieService service;

    public MovieApiController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<Movie> list(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/{id}")
    public Movie get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movie create(@Valid @RequestBody Movie movie) {
        return service.create(movie);
    }

    @PutMapping("/{id}")
    public Movie update(@PathVariable Long id, @Valid @RequestBody Movie movie) {
        return service.update(id, movie);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
