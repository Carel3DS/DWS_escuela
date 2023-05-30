package es.dws.escuela.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private String surname;
    private String email;
    private String password;
    private String description;
    private int age;

    //Relationship
    @ManyToOne
    private Department department;
    @ManyToMany
    private List<Grade> grades;

    public Teacher(String name, String surname, String email, String password, String description, int age) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.description = description;
        this.age = age;
        this.department = null;
        this.grades = new ArrayList<>();
    }
}
