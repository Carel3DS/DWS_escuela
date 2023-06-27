package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher{
    @Id
    @JsonView({Views.Teacher.class, Views.Department.class, Views.Grade.class})
    //Identifier = name.surname, all lowercase without spaces
    private String id;

    //Email is generated automatically, and unique
    @Column(unique = true)
    @JsonView(Views.Teacher.class)
    private String email;

    //Define each attribute, applying the necessary restrictions
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^$|^[\\p{L} ]+$", message = "Surname must contain only Latin characters and spaces")
    private String name;

    //Both name and surname have only latin characters
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    @NotBlank(message = "Surname is required")
    @Pattern(regexp = "^$|^[\\p{L} ]+$", message = "Surname must contain only Latin characters and spaces")
    private String surname;

    //Age only can be between 1 and 100
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    @Range(min = 1, max = 100, message = "Age must be between 1 and 100")
    @NotNull(message = "Age is required", groups = {Groups.TeacherGroup.class})
    private Integer age;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String pass;

    //Rock and roles
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @JsonView(Views.Teacher.class)
    private String description;

    //Relationships
    @ManyToOne
    @JsonView(Views.Teacher.class)
    private Department department;

    @JsonView(Views.Teacher.class)
    @ManyToMany(mappedBy = "teachers")
    private List<Grade> grades;

    public Teacher(String name, String surname, String pass, String description, int age) {
        this.name = name;
        this.surname = surname;
        this.id = name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@urdj.es";
        this.pass = pass;
        this.description = description;
        this.age = age;
        this.department = null;
        this.grades = new ArrayList<>();
        //Teachers cannot be admin
        this.roles = new ArrayList<>(List.of("USER","TEACHER"));
    }

    public Teacher(String name, String surname, String pass, Integer age) {
        this.name = name;
        this.surname = surname;
        this.id = name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@urdj.es";
        this.pass = pass;
        this.age = age;
        this.department = null;
        this.grades = new ArrayList<>();
        //Teachers cannot be admin
        this.roles = new ArrayList<>(List.of("USER","TEACHER"));
    }

    public Teacher(String name, String surname, String pass, Integer age, String description, String ... roles) {
        this.name = name;
        this.surname = surname;
        this.id = name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@urdj.es";
        this.pass = pass;
        this.age = age;
        this.department = null;
        this.grades = new ArrayList<>();
        //Teachers cannot be admin
        this.roles = new ArrayList<>(List.of(roles));
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
