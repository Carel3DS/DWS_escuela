package es.dws.escuela.valids;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//The following class is used only for EDIT forms (hence, values can be null)
public class ValidUser {

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

}
