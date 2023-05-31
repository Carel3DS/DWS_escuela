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
    private Long id;
    private String name;
    private String location;
    private String description;

    //Relationship TODO: fix department's "teachers" view (Use Views implementation)
    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Teacher> teachers;

    public Department(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }
}
