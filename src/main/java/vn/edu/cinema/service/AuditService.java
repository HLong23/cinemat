package vn.edu.cinema.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.entity.AuditLog;
import vn.edu.cinema.repository.AuditLogRepository;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    /** Ghi log trong transaction riêng để vẫn lưu được kể cả khi nghiệp vụ gốc bị rollback. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String username, String detail) {
        AuditLog a = new AuditLog();
        a.setAction(action);
        a.setUsername(username);
        a.setDetail(detail != null && detail.length() > 500 ? detail.substring(0, 500) : detail);
        repo.save(a);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> latest() {
        return repo.findTop30ByOrderByIdDesc();
    }
}
