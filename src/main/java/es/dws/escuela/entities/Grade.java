package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({Views.Teacher.class, Views.Grade.class})
    private Long id;
    @JsonView({Views.Teacher.class, Views.Grade.class})
    @Column(nullable = false)
    @NotBlank(message = "A name is required")
    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "The name cannot contain special characters")
    private String name;
    @JsonView(Views.Grade.class)
    private String description;
    @JsonView({Views.Teacher.class, Views.Grade.class})
    @Column(nullable = false)
    @NotNull(message = "A year is required")
    private Integer year;


    //Relationship
    @ManyToMany
    @JsonView(Views.Grade.class)
    private List<Teacher> teachers;

    public Grade(String name, String description, Integer year) {
        this.name = name;
        this.description = description;
        this.year = year;
        this.teachers = new ArrayList<>();
    }

    public void addTeacher(Teacher teacher) {
        this.teachers.add(teacher);
        teacher.getGrades().add(this);
    }

    public void removeTeacher(String teacherId){
        boolean found = false;
        int i = 0;
        //Find the teacher and remove from the list (O(N))
        while (!found && i< this.teachers.size()){
            Teacher t = this.teachers.get(i);
            if (t.getId().equals(teacherId)){
                teachers.remove(i);
                found = true;
            }else {
                i++;
            }
        }
    }
    public void removeTeacher(Teacher teacher){
        this.teachers.remove(teacher);
    }
}