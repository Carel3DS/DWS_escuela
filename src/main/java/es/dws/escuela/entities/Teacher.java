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
public class Teacher {
    @Id
    String id;
    String name;
    String surname;
    String email;
    String description;
    Integer age;

    //Relationship
    @ManyToOne
    Department department;
    @ManyToMany
    List<Grade> grades;
}
