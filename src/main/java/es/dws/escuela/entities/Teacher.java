package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView({Views.Teacher.class, Views.Department.class, Views.Grade.class})
    //Identifier = name.surname, all lowercase without spaces
    private String id;
    @Column(unique = true)
    @JsonView(Views.Teacher.class)
    private String email;
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    private String name;
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    private String surname;
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    private int age;
    @Column(nullable = false)
    private String password;
    @JsonView(Views.Teacher.class)
    private String description;

    //Relationships
    @ManyToOne
    @JsonView(Views.Teacher.class)
    private Department department;

    @JsonView(Views.Teacher.class)
    @ManyToMany(mappedBy = "teachers")
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

    public void addGrade(Grade grade) {
        this.grades.add(grade);
        grade.getTeachers().add(this);
    }
    public void removeGrade(Grade grade){
        this.grades.remove(grade);
        grade.getTeachers().remove(this);
    }

}
