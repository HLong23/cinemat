package vn.edu.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cinema.dto.RoomForm;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.service.CinemaService;
import vn.edu.cinema.service.RoomService;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {

    private final RoomService service;
    private final CinemaService cinemaService;

    public AdminRoomController(RoomService service, CinemaService cinemaService) {
        this.service = service;
        this.cinemaService = cinemaService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long edit, Model model) {
        model.addAttribute("form", edit != null ? RoomForm.from(service.get(edit)) : new RoomForm());
        return view(model);
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("form") RoomForm form, BindingResult result,
                       Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return view(model);
        }
        if (form.getId() == null) {
            service.create(form);
        } else {
            service.update(form.getId(), form);
        }
        redirect.addFlashAttribute("success", "Đã lưu phòng chiếu");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Đã xoá phòng");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    private String view(Model model) {
        model.addAttribute("rooms", service.findAll());
        model.addAttribute("cinemas", cinemaService.findAll());
        return "admin/rooms";
    }
}
