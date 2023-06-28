package es.dws.escuela.services;

import es.dws.escuela.entities.*;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.repositories.GradeRepository;
import es.dws.escuela.utils.HTMLPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    @Autowired
    private GradeRepository repository;
    @Autowired
    private EntityManager entityManager;
    private final static PolicyFactory POLICY = HTMLPolicy.POLICY_DEFINITION;

    public Grade create(Grade grade){
        grade.setDescription(POLICY.sanitize(grade.getDescription()));
        return repository.save(grade);
    }
    public List<Grade> readAll(){
        return repository.findAll();
    }
    public Grade read(Long id){
        Optional<Grade> op = repository.findById(id);
        return op.orElse(null);
    }

    public List<Grade> readByName(String name) {
        TypedQuery<Grade> tq = entityManager.createQuery("SELECT t FROM Grade t where t.name like ?1", Grade.class);
        tq.setParameter(1, "%"+name+"%");
        return tq.getResultList();
    }

    public Grade update(Long id, Grade newGrade){
        Optional<Grade> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Grade grade = op.get();
            if(newGrade.getYear() != 0 && !newGrade.getYear().equals(grade.getYear())){
                grade.setYear(newGrade.getYear());
            }
            if(!newGrade.getName().isEmpty() && !newGrade.getName().equals(grade.getName())){
                grade.setName(newGrade.getName());
            }
            grade.setDescription(POLICY.sanitize(newGrade.getDescription()));
            //Does this overwrite the grade identified by id?
            repository.save(grade);
            return grade;
        }else{
            return null;
        }
    }

    public Grade delete(Long id){
        Optional<Grade> op = repository.findById(id);
        if(op.isPresent()){
            Grade g = op.get();
            //Remove reference of the grade for each teacher if there are teachers
            if(!g.getTeachers().isEmpty()){
                for(Teacher t:g.getTeachers()){
                    t.removeGrade(g);
                }
            }
            //Save the changes
            repository.save(g);
            //Clear teachers list
            g.getTeachers().clear();
            //Save again
            repository.save(g);
            //Delete safely by ID
            repository.deleteById(id);
            return g;
        }else{
            return null;
        }
    }

    //Adds a teacher to a grade
    void addTeacher(Teacher teacher, Long gradeId) {
        Grade grade = this.read(gradeId);
        List<Teacher> teachers = grade.getTeachers();
        grade.addTeacher(teacher);
        repository.save(grade);
    }

    //Adds a user to a grade
    void addUser(User user, Long gradeId) {
        Grade grade = this.read(gradeId);
        List<User> users = grade.getUsers();
        users.add(user);
        grade.setUsers(users);
        repository.save(grade);
    }

    //Removes teacher from a grade
    public void removeTeacherFromGrade(Long id, String teacherId) {
        Grade grade = this.read(id);
        List<Teacher> teachers = grade.getTeachers();
        boolean found = false;
        int i = 0;
        //Find the teacher and remove from the list (O(N))
        while (!found && i < teachers.size()){
            Teacher t = teachers.get(i);
            if (t.getId().equals(teacherId)){
                teachers.remove(i);
                found = true;
            }else {
                i++;
            }
        }
        grade.setTeachers(teachers);
        repository.save(grade);
    }

    //Removes a user from a grade
    public void removeUserFromGrade(Long id, String userId) {
        Grade grade = this.read(id);
        List<User> users = grade.getUsers();
        boolean found = false;
        int i = 0;
        //Find the user and remove from the list (O(N))
        while (!found  && i < users.size()){
            User t = users.get(i);
            if (t.getId().equals(userId)){
                users.remove(i);
                found = true;
            }else {
                i++;
            }
        }
        grade.setUsers(users);
        repository.save(grade);
    }


    public boolean gradeExists(long id) {
        return this.repository.existsById(id);
    }
}
