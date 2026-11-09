package vn.edu.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.cinema.entity.AuditLog;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop30ByOrderByIdDesc();
}
