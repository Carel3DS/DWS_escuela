package es.dws.escuela.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String location;
    private String description;
    @OneToMany
    private List<Teacher> teachers;

    public Department(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }
}
