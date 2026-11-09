package vn.edu.cinema.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cinema.entity.Cinema;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.exception.ResourceNotFoundException;
import vn.edu.cinema.repository.CinemaRepository;
import vn.edu.cinema.repository.RoomRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository repo;
    private final RoomRepository roomRepo;

    public CinemaService(CinemaRepository repo, RoomRepository roomRepo) {
        this.repo = repo;
        this.roomRepo = roomRepo;
    }

    public List<Cinema> findAll() {
        return repo.findAll();
    }

    public Cinema get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy rạp #" + id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Cinema create(Cinema cinema) {
        cinema.setId(null);
        return repo.save(cinema);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Cinema update(Long id, Cinema data) {
        Cinema c = get(id);
        c.setName(data.getName());
        c.setAddress(data.getAddress());
        return repo.save(c);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Cinema c = get(id);
        if (roomRepo.existsByCinemaId(id)) {
            throw new BusinessException("Không thể xoá rạp còn phòng chiếu. Hãy xoá các phòng trước.");
        }
        repo.delete(c);
    }
}
