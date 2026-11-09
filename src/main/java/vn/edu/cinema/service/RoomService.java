package vn.edu.cinema.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.dto.RoomForm;
import vn.edu.cinema.entity.Room;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.exception.ResourceNotFoundException;
import vn.edu.cinema.repository.CinemaRepository;
import vn.edu.cinema.repository.RoomRepository;
import vn.edu.cinema.repository.ShowtimeRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository repo;
    private final CinemaRepository cinemaRepo;
    private final ShowtimeRepository showtimeRepo;

    public RoomService(RoomRepository repo, CinemaRepository cinemaRepo, ShowtimeRepository showtimeRepo) {
        this.repo = repo;
        this.cinemaRepo = cinemaRepo;
        this.showtimeRepo = showtimeRepo;
    }

    public List<Room> findAll() {
        return repo.findAll();
    }

    public Room get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng #" + id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Room create(RoomForm form) {
        Room r = new Room();
        apply(r, form);
        return repo.save(r);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Room update(Long id, RoomForm form) {
        Room r = get(id);
        apply(r, form);
        return repo.save(r);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Room r = get(id);
        if (showtimeRepo.existsByRoomId(id)) {
            throw new BusinessException("Không thể xoá phòng đã có suất chiếu.");
        }
        repo.delete(r);
    }

    private void apply(Room r, RoomForm f) {
        r.setName(f.getName());
        r.setNumRows(f.getNumRows());
        r.setSeatsPerRow(f.getSeatsPerRow());
        r.setCinema(cinemaRepo.findById(f.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy rạp #" + f.getCinemaId())));
    }
}
