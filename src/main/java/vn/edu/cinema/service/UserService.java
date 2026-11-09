package vn.edu.cinema.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.dto.RegisterForm;
import vn.edu.cinema.entity.AppUser;
import vn.edu.cinema.entity.Role;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /** Đăng ký tài khoản khách hàng (luôn có quyền USER). */
    @Transactional
    public AppUser register(RegisterForm form) {
        String username = form.getUsername().trim();
        if (repo.existsByUsername(username)) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        if (repo.existsByEmail(form.getEmail().trim())) {
            throw new BusinessException("Email đã được sử dụng");
        }
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(encoder.encode(form.getPassword()));
        u.setFullName(form.getFullName().trim());
        u.setEmail(form.getEmail().trim());
        u.setRole(Role.USER);
        return repo.save(u);
    }
}
