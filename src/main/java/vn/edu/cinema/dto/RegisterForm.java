package vn.edu.cinema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Chỉ dùng chữ, số, dấu . _ -")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu từ 6 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
