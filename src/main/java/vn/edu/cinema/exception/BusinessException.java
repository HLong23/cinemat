package vn.edu.cinema.exception;

/** Lỗi nghiệp vụ (ghế đã đặt, dữ liệu đang được sử dụng...) - thông điệp hiển thị được cho người dùng. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
