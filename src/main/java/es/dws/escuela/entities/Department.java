package es.dws.escuela.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({Views.Teacher.class,Views.Department.class})
    private Long id;
    @JsonView({Views.Teacher.class,Views.Department.class})
    private String name;
    @JsonView(Views.Department.class)
    private String location;
    @JsonView(Views.Department.class)
    private String description;

    @JsonView(Views.Department.class)
    @OneToMany(mappedBy = "department")
    private List<Teacher> teachers;

    public Department(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    public void addTeacher(Teacher teacher) {
        this.teachers.add(teacher);
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

    public void removeTeacher(Teacher teacher) {
        this.teachers.remove(teacher);
    }
}
