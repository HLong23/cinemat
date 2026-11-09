package vn.edu.cinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    /** Số hàng ghế (A, B, C...). */
    @Column(name = "num_rows", nullable = false)
    private int numRows;

    @Column(name = "seats_per_row", nullable = false)
    private int seatsPerRow;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    /** Mã ghế hợp lệ có dạng A1..Z30 và nằm trong sơ đồ phòng. */
    public boolean hasSeat(String code) {
        if (code == null || !code.matches("^[A-Z][0-9]{1,2}$")) {
            return false;
        }
        int row = code.charAt(0) - 'A';
        int number = Integer.parseInt(code.substring(1));
        return row < numRows && number >= 1 && number <= seatsPerRow;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getNumRows() { return numRows; }
    public void setNumRows(int numRows) { this.numRows = numRows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
    public Cinema getCinema() { return cinema; }
    public void setCinema(Cinema cinema) { this.cinema = cinema; }
}
