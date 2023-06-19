package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher extends User{
    //Age only can be between 1 and 100
    @Column(nullable = false)
    @JsonView(Views.Teacher.class)
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age cannot be negative. Must be at least 1")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;

    //Relationships
    @ManyToOne
    @JsonView(Views.Teacher.class)
    private Department department;

    public Teacher(String name, String surname, String pass, String description, int age) {
        super(name,surname,pass,description);
        this.setEmail(this.getId()+"@urdj.es");
        this.age = age;
        this.setRoles(new ArrayList<>(List.of("USER","TEACHER")));
    }

}




















