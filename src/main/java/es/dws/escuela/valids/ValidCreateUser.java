package es.dws.escuela.valids;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Views;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//The following class is used only for EDIT forms (hence, values can be null)
//TODO: Use ValidCreateUser to validate user creation (since BCrypt passwords don't comply pattern constraint)
public class ValidCreateUser {
    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^$|^[\\p{L} ]+$", message = "Surname must contain only Latin characters and spaces")
    private String name;

    //Both name and surname have only latin characters
    @Column(nullable = false)
    @NotBlank(message = "Surname is required")
    @Pattern(regexp = "^$|^[\\p{L} ]+$", message = "Surname must contain only Latin characters and spaces")
    private String surname;

    //Make the password at least secure
    @Pattern(regexp = "|^(?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])(?=.*[@#$*%^&+=?!]).{8,16}$",
            message = """
                    Password must have at least 8 characters, including:<ul>
                    <li>one uppercase letter</li>
                    <li>one lowercase letter</li>
                    <li>one digit</li>
                    <li>and one special character.</li></ul><br>
                    Please check it before submitting""")
    private String pass;

    private String description;

    private String[] roles;

}
