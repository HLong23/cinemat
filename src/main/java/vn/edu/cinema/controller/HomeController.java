package vn.edu.cinema.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.cinema.entity.Movie;
import vn.edu.cinema.service.MovieService;
import vn.edu.cinema.service.ShowtimeService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class HomeController {

    /** Cookie lưu id các phim đã xem gần đây, ngăn cách bằng dấu gạch ngang, vd "3-1-5". */
    static final String RECENT_COOKIE = "recentMovies";
    private static final int RECENT_MAX = 4;

    private final MovieService movieService;
    private final ShowtimeService showtimeService;

    public HomeController(MovieService movieService, ShowtimeService showtimeService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @CookieValue(name = RECENT_COOKIE, defaultValue = "") String recent,
                       Model model) {
        model.addAttribute("movies", movieService.search(q));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("recentMovies", movieService.findByIds(parseIds(recent)));
        return "home";
    }

    @GetMapping("/movies/{id}")
    public String detail(@PathVariable Long id,
                         @CookieValue(name = RECENT_COOKIE, defaultValue = "") String recent,
                         HttpServletResponse response,
                         Model model) {
        Movie movie = movieService.get(id);
        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimeService.findUpcomingByMovie(id));

        // Ghi nhớ phim vừa xem vào cookie (HttpCookie/ResponseCookie).
        LinkedList<Long> ids = new LinkedList<>(parseIds(recent));
        ids.remove(id);
        ids.addFirst(id);
        while (ids.size() > RECENT_MAX) {
            ids.removeLast();
        }
        String value = String.join("-", ids.stream().map(String::valueOf).toList());
        ResponseCookie cookie = ResponseCookie.from(RECENT_COOKIE, value)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "movie-detail";
    }

    private List<Long> parseIds(String raw) {
        List<Long> ids = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return ids;
        }
        for (String part : raw.split("-")) {
            try {
                ids.add(Long.parseLong(part));
            } catch (NumberFormatException ignored) {
                // bỏ qua giá trị cookie bị sửa tay
            }
        }
        return ids;
    }
}
