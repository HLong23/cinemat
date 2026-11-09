package vn.edu.cinema.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.cinema.dto.RoomForm;
import vn.edu.cinema.entity.Room;
import vn.edu.cinema.service.RoomService;

import java.util.List;

/** Phòng chiếu: xem danh sách cũng yêu cầu đăng nhập (không nằm trong danh sách GET công khai). */
@RestController
@RequestMapping("/api/rooms")
public class RoomApiController {

    private final RoomService service;

    public RoomApiController(RoomService service) {
        this.service = service;
    }

    @GetMapping
    public List<Room> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Room get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room create(@Valid @RequestBody RoomForm form) {
        return service.create(form);
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable Long id, @Valid @RequestBody RoomForm form) {
        return service.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
