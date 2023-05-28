package es.dws.escuela.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
    Long id;
    String name;
    Integer year;

    //Relationship
    @ManyToMany
    List<Teacher> teachers;
}