package vn.edu.cinema.dto;

import jakarta.validation.constraints.*;
import vn.edu.cinema.entity.Room;

public class RoomForm {

    private Long id;

    @NotBlank(message = "Tên phòng không được để trống")
    @Size(max = 100)
    private String name;

    @Min(value = 1, message = "Tối thiểu 1 hàng")
    @Max(value = 26, message = "Tối đa 26 hàng (A-Z)")
    private int numRows = 8;

    @Min(value = 1, message = "Tối thiểu 1 ghế mỗi hàng")
    @Max(value = 30, message = "Tối đa 30 ghế mỗi hàng")
    private int seatsPerRow = 10;

    @NotNull(message = "Vui lòng chọn rạp")
    private Long cinemaId;

    public static RoomForm from(Room r) {
        RoomForm f = new RoomForm();
        f.id = r.getId();
        f.name = r.getName();
        f.numRows = r.getNumRows();
        f.seatsPerRow = r.getSeatsPerRow();
        f.cinemaId = r.getCinema().getId();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getNumRows() { return numRows; }
    public void setNumRows(int numRows) { this.numRows = numRows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
    public Long getCinemaId() { return cinemaId; }
    public void setCinemaId(Long cinemaId) { this.cinemaId = cinemaId; }
}
