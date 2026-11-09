package vn.edu.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.cinema.service.AuditService;
import vn.edu.cinema.service.BookingService;
import vn.edu.cinema.service.CinemaService;
import vn.edu.cinema.service.MovieService;
import vn.edu.cinema.service.ShowtimeService;

@Controller
public class AdminDashboardController {

    private final MovieService movieService;
    private final CinemaService cinemaService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;
    private final AuditService auditService;

    public AdminDashboardController(MovieService movieService, CinemaService cinemaService,
                                    ShowtimeService showtimeService, BookingService bookingService,
                                    AuditService auditService) {
        this.movieService = movieService;
        this.cinemaService = cinemaService;
        this.showtimeService = showtimeService;
        this.bookingService = bookingService;
        this.auditService = auditService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("movieCount", movieService.findAll().size());
        model.addAttribute("cinemaCount", cinemaService.findAll().size());
        model.addAttribute("showtimeCount", showtimeService.findAll().size());
        model.addAttribute("bookingCount", bookingService.countConfirmed());
        model.addAttribute("revenue", bookingService.revenue());
        model.addAttribute("logs", auditService.latest());
        model.addAttribute("bookings", bookingService.findAll());
        return "admin/dashboard";
    }
}
