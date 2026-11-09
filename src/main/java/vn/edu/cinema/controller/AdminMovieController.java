package vn.edu.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cinema.entity.Movie;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.service.MovieService;

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {

    private final MovieService service;

    public AdminMovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long edit, Model model) {
        model.addAttribute("movies", service.findAll());
        model.addAttribute("movie", edit != null ? service.get(edit) : new Movie());
        return "admin/movies";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("movie") Movie movie, BindingResult result,
                       Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("movies", service.findAll());
            return "admin/movies";
        }
        if (movie.getId() == null) {
            service.create(movie);
        } else {
            service.update(movie.getId(), movie);
        }
        redirect.addFlashAttribute("success", "Đã lưu phim");
        return "redirect:/admin/movies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Đã xoá phim");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/movies";
    }
}
