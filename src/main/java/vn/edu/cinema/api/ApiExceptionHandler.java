package vn.edu.cinema.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.exception.ResourceNotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Chuẩn hoá lỗi JSON cho các REST controller trong package api. */
@RestControllerAdvice(basePackages = "vn.edu.cinema.api")
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> business(BusinessException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> badCredentials(BadCredentialsException ex) {
        return body(HttpStatus.UNAUTHORIZED, "Sai tên đăng nhập hoặc mật khẩu", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.put(e.getField(), e.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", fields);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message, Object errors) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", status.value());
        m.put("message", message);
        if (errors != null) {
            m.put("errors", errors);
        }
        return ResponseEntity.status(status).body(m);
    }
}
