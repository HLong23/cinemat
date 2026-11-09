package vn.edu.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cinema.entity.Cinema;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.service.CinemaService;

@Controller
@RequestMapping("/admin/cinemas")
public class AdminCinemaController {

    private final CinemaService service;

    public AdminCinemaController(CinemaService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long edit, Model model) {
        model.addAttribute("cinemas", service.findAll());
        model.addAttribute("cinema", edit != null ? service.get(edit) : new Cinema());
        return "admin/cinemas";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("cinema") Cinema cinema, BindingResult result,
                       Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("cinemas", service.findAll());
            return "admin/cinemas";
        }
        if (cinema.getId() == null) {
            service.create(cinema);
        } else {
            service.update(cinema.getId(), cinema);
        }
        redirect.addFlashAttribute("success", "Đã lưu rạp");
        return "redirect:/admin/cinemas";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Đã xoá rạp");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cinemas";
    }
}
