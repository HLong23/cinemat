package vn.edu.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cinemas")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên rạp không được để trống")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String address;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
