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
    Integer age;

    //Relationship
    @ManyToOne
    Department department;
    @ManyToMany
    List<Grade> grades;
}
