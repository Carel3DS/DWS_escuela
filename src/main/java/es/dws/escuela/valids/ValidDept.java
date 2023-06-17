package es.dws.escuela.valids;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.entities.Views;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
//The following class is used only for EDIT forms (hence, values can be null)
public class ValidDept {

    @NotBlank(message = "A name is required")
    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "The name cannot contain special characters")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9.,\\s]*$", message = "The location cannot contain special characters")
    private String location;

    private String description;

}
