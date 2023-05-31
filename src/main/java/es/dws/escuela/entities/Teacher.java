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
    //Identifier = name.surname, all lowercase without spaces
    private String id;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private int age;
    @Column(nullable = false)
    private String password;
    private String description;

    //Relationship
    @ManyToOne
    private Department department;
    @ManyToMany
    private List<Grade> grades;

    public Teacher(String name, String surname, String password, String description, int age) {
        this.name = name;
        this.surname = surname;
        this.id = name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@urdj.es";
        this.password = password;
        this.description = description;
        this.age = age;
        this.department = null;
        this.grades = new ArrayList<>();
    }
}
