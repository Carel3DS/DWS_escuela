package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
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
@Table(name = "users")
public class User {
    @Id
    @JsonView({Views.User.class, Views.Department.class, Views.Grade.class})
    //Identifier = name.surname, all lowercase without spaces
    private String id;

    //Email is generated automatically, and unique
    @Column(unique = true)
    @JsonView(Views.User.class)
    private String email;

    //Define each attribute, applying the necessary restrictions
    @Column(nullable = false)
    @JsonView(Views.User.class)
    @NotBlank(message = "Name is required", groups = {Groups.UserGroup.class})
    @Pattern(regexp = "^$|^[\\p{L} ]+$", message = "Name must contain only Latin characters and spaces",groups = {Groups.UserGroup.class})
    private String name;

    //Both name and surname have only latin characters
    @Column(nullable = false)
    @JsonView(Views.User.class)
    @NotBlank(message = "Surname is required",groups = {Groups.UserGroup.class})
    @Pattern(regexp = "^$|^[\\p{L} ]+$", message = "Surname must contain only Latin characters and spaces",groups = {Groups.UserGroup.class})
    private String surname;

    //Rock and Roles
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;


    @Column(nullable = false)
    @NotBlank(message = "Password is required",groups = {Groups.UserGroup.class})
    /*@Pattern(regexp = "|^(?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])(?=.*[@#$*%^&+=?!]).{8,}$",
            message = """
                    Password must have at least 8 characters, including:<ul>
                    <li>one uppercase letter
                    <li>one lowercase letter</li>
                    <li>one digit</li>
                    <li>and one special character.</li></ul>
                    Please check it before submitting""", groups = {Groups.UserGroup.class, Groups.UserOptGroup.class})*/
    private String pass;

    @JsonView(Views.User.class)
    private String description;

    //Relationship
    @JsonView(Views.User.class)
    @ManyToMany(mappedBy = "users")
    private List<Grade> grades;

    public User(String name, String surname, String pass, String description, String ... roles) {
        this.name = name;
        this.surname = surname;
        this.id = "al."+name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@alumnos.urdj.es";
        this.pass = pass;
        this.description = description;
        this.grades = new ArrayList<>();
        //Set custom roles
        this.roles = new ArrayList<>(List.of(roles));
    }

    public User(String name, String surname, String pass) {
        this.name = name;
        this.surname = surname;
        this.id = "al."+name.toLowerCase().replace(" ","")+"."+surname.toLowerCase();
        this.email = this.id+"@alumnos.urdj.es";
        this.pass = pass;
        this.grades = new ArrayList<>();
        this.roles = new ArrayList<>(List.of("USER"));
    }

    public void addGrade(Grade grade) {
        this.grades.add(grade);
        grade.getUsers().add(this);
    }
    public void removeGrade(Grade grade){
        this.grades.remove(grade);
    }

}
