package es.dws.escuela.valids;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Views;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//The following class is used only for EDIT forms (hence, values can be null)
public class ValidTeacher {

    //Age only can be between 1 and 100
    @Column(nullable = false)
    @Min(value = 1, message = "Age cannot be negative. Must be at least 1")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;

    //Make the password at least secure
    @Pattern(regexp = "^(?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])(?=.*[@#$*%^&+=?!]).{8,16}$",
            message = """
                    Password must have at least 8 characters, including:<ul>
                    <li>one uppercase letter</li>
                    <li>one lowercase letter</li>
                    <li>one digit</li>
                    <li>and one special character.</li></ul><br>
                    Please check it before submitting""")
    private String pass;

    private String description;
    //Only if department is selected (department 0 dettaches teacher from department)
    private Long departmentID;

}
