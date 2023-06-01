package es.dws.escuela.services;

import es.dws.escuela.entities.Teacher;
import es.dws.escuela.repositories.TeacherRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository repository;
    @Autowired
    private EntityManager entityManager;

    //RELATIONSHIP
    @Autowired
    private GradeService gradeService;
    @Autowired
    private DepartmentService departmentService;

    public Teacher create(Teacher teacher){
        return repository.save(teacher);
    }

    //Read all the teachers saved in repository
    public List<Teacher> readAll(){
        return repository.findAll();
    }

    //Read by age range
    public List<Teacher> readByAge(int min, int max) {
        TypedQuery<Teacher>  tq = entityManager.createQuery("SELECT t FROM Teacher t where t.age >= ?1 and t.age <= ?2", Teacher.class);
        tq.setParameter(1, min);
        tq.setParameter(2, max);
        return tq.getResultList();
    }
    //Read by known ID
    public Teacher read(String id){
        Optional<Teacher> op = repository.findById(id);
        return op.orElse(null);
    }
    //Read by known Name and Surname

    public Teacher read(String name, String surname){
        Optional<Teacher> op = repository.findByNameAndSurname(name,surname);
        return op.orElse(null);
    }
    public Teacher update(String id, Teacher newTeacher){
        Optional<Teacher> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Teacher teacher = op.get();
            teacher.setDescription(newTeacher.getDescription());
            teacher.setPassword(newTeacher.getPassword());
            teacher.setAge(newTeacher.getAge());
            repository.save(teacher);
            return teacher;
        }else{
            return null;
        }
    }

    public Teacher delete(String id){
        Optional<Teacher> op = repository.findById(id);
        if(op.isPresent()){
            repository.deleteById(id);
            return op.get();
        }else{
            return null;
        }
    }
}
