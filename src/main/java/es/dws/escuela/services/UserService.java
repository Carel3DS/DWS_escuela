package es.dws.escuela.services;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.User;
import es.dws.escuela.repositories.UserRepository;
import es.dws.escuela.utils.HTMLPolicy;
import jakarta.persistence.EntityManager;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //RELATIONSHIP
    @Autowired
    private GradeService gradeService;
    private final static PolicyFactory POLICY = HTMLPolicy.POLICY_DEFINITION;

    public User create(User user){
        user.setDescription(POLICY.sanitize(user.getDescription()));user.setDescription(POLICY.sanitize(user.getDescription()));
        user.setPass(passwordEncoder.encode(user.getPass()));
        return repository.save(user);
    }

    //Read all the users saved in repository
    public List<User> readAll(){
        return repository.findAll();
    }

    //Read by known ID
    public User read(String id){
        Optional<User> op = repository.findById(id);
        return op.orElse(null);
    }

    public User update(String id, User newUser){
        Optional<User> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            User user = op.get();
            user.setDescription(POLICY.sanitize(newUser.getDescription()));
            if(newUser.getPass() != null && newUser.getPass().length() > 0 && !newUser.getPass().equals(user.getPass())){
                user.setPass(passwordEncoder.encode(newUser.getPass()));
            }
            repository.save(user);
            return user;
        }else{
            return null;
        }
    }

    public User delete(String id){
        Optional<User> op = repository.findById(id);
        if(op.isPresent()){
            User t = op.get();
            //Clear grades list and department
            this.removeAllGrades(id);
            //Delete safely by ID
            repository.deleteById(id);
            return t;
        }else{
            return null;
        }
    }

    //Non-standard queries

    //Check if user exists
    public boolean userExists(String id){
        return repository.findById(id).isPresent();
    }

    //Assign user to grade
    public User assignGrade(String userId, Long gradeId){
        User user = this.read(userId);
        if (user != null){
            Grade grade = this.gradeService.read(gradeId);
            if(grade != null){
                user.addGrade(grade);
                this.repository.save(user);
                return user;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    //remove grade from user if assigned.
    public User removeGrade(String userId, Long gradeId){
        User user = this.read(userId);
        if (user != null){
            Grade grade = this.gradeService.read(gradeId);
            if(grade != null){
                user.removeGrade(grade);
                this.gradeService.removeUserFromGrade(gradeId,userId);
                return user;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    //Remove all the grades from the grade list of the user.
    public User removeAllGrades(String userId){
        Optional<User> op = repository.findById(userId);
        if(op.isPresent()){
            User user = op.get();
            if (!user.getGrades().isEmpty()){
                for(Grade g:user.getGrades()){
                    g.removeUser(user);
                }
                user.getGrades().clear();
                repository.save(user);
                return user;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

}
