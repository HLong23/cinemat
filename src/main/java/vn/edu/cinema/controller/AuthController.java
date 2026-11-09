package vn.edu.cinema.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cinema.dto.RegisterForm;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @org.springframework.web.bind.annotation.ModelAttribute("form") RegisterForm form,
                           BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(form);
        } catch (BusinessException e) {
            result.reject("register.failed", e.getMessage());
            return "register";
        }
        redirect.addFlashAttribute("registered", true);
        return "redirect:/login";
    }
}
