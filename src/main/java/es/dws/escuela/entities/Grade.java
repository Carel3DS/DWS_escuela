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
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Integer year;


    //Relationship
    @ManyToMany
    private List<Teacher> teachers;

    public Grade(String name, String description, Integer year) {
        this.name = name;
        this.description = description;
        this.year = year;
    }
}