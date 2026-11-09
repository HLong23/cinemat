package vn.edu.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cinema.dto.ShowtimeForm;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.service.MovieService;
import vn.edu.cinema.service.RoomService;
import vn.edu.cinema.service.ShowtimeService;

@Controller
@RequestMapping("/admin/showtimes")
public class AdminShowtimeController {

    private final ShowtimeService service;
    private final MovieService movieService;
    private final RoomService roomService;

    public AdminShowtimeController(ShowtimeService service, MovieService movieService, RoomService roomService) {
        this.service = service;
        this.movieService = movieService;
        this.roomService = roomService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long edit, Model model) {
        model.addAttribute("form", edit != null ? ShowtimeForm.from(service.get(edit)) : new ShowtimeForm());
        return view(model);
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("form") ShowtimeForm form, BindingResult result,
                       Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return view(model);
        }
        try {
            if (form.getId() == null) {
                service.create(form);
            } else {
                service.update(form.getId(), form);
            }
        } catch (BusinessException e) {
            result.reject("showtime.conflict", e.getMessage());
            return view(model);
        }
        redirect.addFlashAttribute("success", "Đã lưu suất chiếu");
        return "redirect:/admin/showtimes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Đã xoá suất chiếu");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/showtimes";
    }

    private String view(Model model) {
        model.addAttribute("showtimes", service.findAll());
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        return "admin/showtimes";
    }
}
