package es.dws.escuela.services;

import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.User;
import es.dws.escuela.repositories.UserRepository;
import es.dws.escuela.utils.HTMLPolicy;
import es.dws.escuela.valids.ValidUser;
import jakarta.persistence.EntityManager;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private EntityManager entityManager;

    //RELATIONSHIP
    @Autowired
    private GradeService gradeService;
    @Autowired
    private DepartmentService departmentService;

    public User create(User user){
        PolicyFactory policy = HTMLPolicy.POLICY_DEFINITION;;
        user.setDescription(policy.sanitize(user.getDescription()));
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

    public User update(String id, ValidUser newUser){
        Optional<User> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            User user = op.get();
            user.setDescription(newUser.getDescription());
            if(newUser.getPass() != null && newUser.getPass().length() > 0 && !newUser.getPass().equals(user.getPass())){
                user.setPass(newUser.getPass());
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
                this.gradeService.addUser(user,gradeId);
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
