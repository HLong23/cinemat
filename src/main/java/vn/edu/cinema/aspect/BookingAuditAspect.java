package vn.edu.cinema.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.edu.cinema.dto.BookingDto;
import vn.edu.cinema.service.AuditService;

/**
 * AOP #2 (nghiệp vụ đặt vé): tự động ghi nhật ký kiểm toán mỗi khi đặt vé thành công,
 * đặt vé thất bại hoặc huỷ vé - mà không phải sửa code trong BookingService.
 */
@Aspect
@Component
public class BookingAuditAspect {

    private final AuditService auditService;

    public BookingAuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(pointcut = "execution(* vn.edu.cinema.service.BookingService.book(..))", returning = "result")
    public void afterBook(BookingDto result) {
        auditService.log("BOOKING_CREATED", result.username(),
                "Vé #" + result.id() + " - " + result.movieTitle() + " - ghế " + String.join(",", result.seats())
                        + " - " + result.totalPrice() + "đ");
    }

    @AfterThrowing(pointcut = "execution(* vn.edu.cinema.service.BookingService.book(..))", throwing = "ex")
    public void afterBookFailed(JoinPoint jp, Throwable ex) {
        Object[] args = jp.getArgs();
        auditService.log("BOOKING_FAILED", String.valueOf(args[0]),
                "Suất #" + args[1] + " - ghế " + args[2] + " - lý do: " + ex.getMessage());
    }

    @AfterReturning("execution(* vn.edu.cinema.service.BookingService.cancel(..))")
    public void afterCancel(JoinPoint jp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditService.log("BOOKING_CANCELLED", auth != null ? auth.getName() : "?", "Huỷ vé #" + jp.getArgs()[0]);
    }
}
