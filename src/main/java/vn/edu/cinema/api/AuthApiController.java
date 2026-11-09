package vn.edu.cinema.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.cinema.dto.AuthResponse;
import vn.edu.cinema.dto.LoginRequest;
import vn.edu.cinema.dto.RegisterForm;
import vn.edu.cinema.security.CustomUserDetailsService;
import vn.edu.cinema.security.JwtService;
import vn.edu.cinema.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService,
                             UserService userService, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return toResponse((UserDetails) auth.getPrincipal());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterForm form) {
        userService.register(form);
        UserDetails user = userDetailsService.loadUserByUsername(form.getUsername().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    private AuthResponse toResponse(UserDetails user) {
        String role = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new AuthResponse(jwtService.generateToken(user), "Bearer", user.getUsername(), role,
                jwtService.getExpirationMs());
    }
}
