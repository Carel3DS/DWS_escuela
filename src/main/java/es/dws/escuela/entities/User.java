package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.valids.ValidCreateUser;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance
@Table(name = "users")
public class User {
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

    //Rock and Roles
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    //Make the password at least secure
    @Column(nullable = false)
    @NotBlank(message = "Password is required")

    private String pass;
    @JsonView(Views.Teacher.class)
    private String description;

    //Relationship
    @JsonView(Views.Teacher.class)
    @ManyToMany(mappedBy = "users")
    private List<Grade> grades;

    public User(String name, String surname, String pass, String description, String ... roles) {
        this.name = name;
        this.surname = surname;
        this.id = name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@alumnos.urdj.es";
        this.pass = pass;
        this.description = description;
        this.grades = new ArrayList<>();
        //Admin can be User, Teacher and Admin
        this.roles = new ArrayList<>(List.of(roles));
    }

    public User(ValidCreateUser vcUser){
        this.name = vcUser.getName();
        this.surname = surname;
        this.id = name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@alumnos.urdj.es";
        this.pass = pass;
        this.description = description;
        this.grades = new ArrayList<>();
        //Admin can be User, Teacher and Admin
        this.roles = new ArrayList<>(List.of(vcUser.getRoles()));
    }

    public void addGrade(Grade grade) {
        this.grades.add(grade);
        grade.getUsers().add(this);
    }
    public void removeGrade(Grade grade){
        this.grades.remove(grade);
        grade.getUsers().remove(this);
    }

}
