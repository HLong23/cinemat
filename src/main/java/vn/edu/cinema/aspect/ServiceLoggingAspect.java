package vn.edu.cinema.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** AOP #1 (cross-cutting): ghi log và đo thời gian thực thi mọi method của tầng service. */
@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);
    private static final long SLOW_MS = 500;

    @Around("execution(* vn.edu.cinema.service..*(..)) && !within(vn.edu.cinema.service.AuditService)")
    public Object logExecution(ProceedingJoinPoint pjp) throws Throwable {
        String name = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            log.warn("[SERVICE] {} thất bại: {}", name, ex.toString());
            throw ex;
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (ms >= SLOW_MS) {
                log.warn("[SERVICE] {} chạy chậm: {} ms", name, ms);
            } else {
                log.debug("[SERVICE] {} xong trong {} ms", name, ms);
            }
        }
    }
}
